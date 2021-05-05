package ru.nsu.trivia.server.lobby;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.request.async.DeferredResult;
import ru.nsu.trivia.common.dto.model.LobbyDTO;
import ru.nsu.trivia.common.dto.model.LobbyState;
import ru.nsu.trivia.server.model.Lobby;
import ru.nsu.trivia.server.model.Player;
import ru.nsu.trivia.server.model.converters.LobbyConverter;
import ru.nsu.trivia.server.sessions.SessionData;
import ru.nsu.trivia.server.sessions.SessionService;

public class LobbyService {

    private static Logger LOG = Logger.getLogger(LobbyService.class.getName());


    private SessionService sessionService;
    private final long closedLobbyLifetime;
    private final long activeLobbyLifetime;


    private final Map<String, Lobby> playerToLobby = new ConcurrentHashMap<>();
    private final Map<String, Lobby> roomIDToLobby = new ConcurrentHashMap<>();
    private final Multimap<Lobby, DeferredResult<LobbyDTO>> subscriptions =
            Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final RoomIDGenerator idGenerator = new RoomIDGenerator();
    private final Set<ClosedLobby> closedLobbies = Collections.synchronizedSet(new HashSet<>());


    public LobbyService(SessionService sessionService, long closedLobbyLifetime, long activeLobbyLifetime) {
        this.sessionService = sessionService;
        this.closedLobbyLifetime = closedLobbyLifetime;
        this.activeLobbyLifetime = activeLobbyLifetime;
    }

    private static class ClosedLobby {
        public Lobby lobby;
        public long timestamp;

        public ClosedLobby(Lobby lobby, long timestamp) {
            this.lobby = lobby;
            this.timestamp = timestamp;
        }
    }

    public LobbyDTO getLobbyById(String roomID) {
        var lobby = roomIDToLobby.get(roomID);
        if (lobby == null) {
            throw new RuntimeException("No such lobby: " + roomID);
        }
        return LobbyConverter.convert(lobby);
    }

    public LobbyDTO getLobbyByToken(String token) {
        var lobby = playerToLobby.get(token);
        if (lobby == null) {
            throw new RuntimeException("Player not in any lobby");
        }
        return LobbyConverter.convert(lobby);
    }

    public void addSubscription(String token, long lastUpdate, DeferredResult<LobbyDTO> result) {
        var lobby = playerToLobby.get(token);
        if (lobby == null) {
            throw new RuntimeException("Player not in any lobby");
        }
        addSubscription(lobby, lastUpdate, result);
    }

    public void addSubscription(Lobby lobby, long lastUpdated, DeferredResult<LobbyDTO> result) {
        if (lobby.getState() == LobbyState.Closed || lobby.getLastUpdate() > lastUpdated) {
            notifySubscriber(result, lobby);
            return;
        }
        subscriptions.put(lobby, result);
    }

    public String createRoom() {
        String id = idGenerator.generate();
        Lobby lobby = new Lobby();
        lobby.setId(id);
        roomIDToLobby.put(id, lobby);
        return id;
    }

    public void deleteRoom(String id) {
        Lobby lobby = roomIDToLobby.get(id);
        if (lobby == null) {
            return;
        }
        lobby.setState(LobbyState.Closed);
        notifySubscribers(lobby);
        closedLobbies.add(new ClosedLobby(lobby, System.currentTimeMillis()));
    }

    public void startLobby(String token) {
        Lobby lobby = playerToLobby.get(token);
        if (lobby == null) {
            throw new RuntimeException("Can't start game: player is not in any lobby");
        }
        Player player = lobby.getPlayers().stream()
                .filter(p -> p.getToken().equals(token))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Internal error: player not found in associated lobby"));
        if (!player.isHost()) {
            throw new RuntimeException("Player is not a host");
        }
        if (lobby.getState() != LobbyState.Waiting) {
            throw new RuntimeException("Lobby has already started");
        }
        lobby.setState(LobbyState.Playing);
        notifySubscribers(lobby);
    }

    public void leaveLobby(String token) {
        var lobby = playerToLobby.get(token);
        if (lobby == null) {
            throw new RuntimeException("Player not in any lobby");
        }
        synchronized (lobby) {
            boolean isHost =
                    lobby.getPlayers().stream()
                            .filter(p -> p.getToken().equals(token))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Player not found in lobby"))
                            .isHost();
            lobby.removePlayer(playerByToken(token));
            if (isHost) {
                deleteRoom(lobby.getId());
            }
            notifySubscribers(lobby);

        }
    }

    public void connectToLobby(String token, String roomID) {
        var lobby = roomIDToLobby.get(roomID);
        if (lobby == null) {
            throw new RuntimeException("No such lobby: " + roomID);
        }
        connectToLobby(token, lobby);
    }

    public void connectToLobby(String token, Lobby lobby) {
        if (lobby.getState() != LobbyState.Waiting) {
            throw new RuntimeException("Game is already running");
        }
        synchronized (lobby) {
            Player player = playerByToken(token);
            if (lobby.getPlayerCount() == 0) {
                player.setHost(true);
            }
            lobby.addPlayer(player);
            playerToLobby.put(token, lobby);
            notifySubscribers(lobby);
        }
    }

    private void notifySubscribers(Lobby lobby) {
        lobby.setLastUpdate(System.currentTimeMillis());
        subscriptions.get(lobby).forEach(res -> notifySubscriber(res, lobby));
    }

    private void notifySubscriber(DeferredResult<LobbyDTO> result, Lobby lobby) {
        result.setResult(LobbyConverter.convert(lobby));
        subscriptions.remove(lobby, result);
    }

    private Player playerByToken(String token) {
        return new Player(sessionService.getSession(token));
    }


    @Scheduled(fixedDelay = 30000)
    private void zombieLobbyMarker() {
        long time = System.currentTimeMillis();

        long count = roomIDToLobby.values().stream()
                .filter(lobby -> time - lobby.getCreationTime() > activeLobbyLifetime)
                .peek(lobby -> deleteRoom(lobby.getId()))
                .count();
        LOG.info("zombieLobbyMarker marked " + count + " lobbies");
        LOG.info("Currently active lobbies: " + roomIDToLobby.values().size());
    }

    @Scheduled(fixedDelay = 10000)
    private void closedLobbyCollector() {
        long time = System.currentTimeMillis();
        var toDelete = closedLobbies.stream()
                .filter(cl -> cl.timestamp < time - closedLobbyLifetime)
                .collect(Collectors.toSet());
        LOG.info("closedLobbyCollector is deleting " + toDelete.size() + " lobbies");
        toDelete.forEach(closedLobby -> {
            var lobby = closedLobby.lobby;
            closedLobby.lobby.getPlayers().stream().map(SessionData::getToken).forEach(playerToLobby::remove);
            roomIDToLobby.remove(lobby.getId());
            subscriptions.removeAll(lobby);
            idGenerator.recall(lobby.getId());
            closedLobbies.remove(closedLobby);
        });
    }

}
