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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import ru.nsu.trivia.common.dto.model.LobbyDTO;
import ru.nsu.trivia.server.model.Lobby;
import ru.nsu.trivia.server.model.Player;
import ru.nsu.trivia.server.model.converters.LobbyConverter;
import ru.nsu.trivia.server.sessions.SessionService;

@Service
@EnableScheduling
public class LobbyService {

    private static Logger LOG = Logger.getGlobal();

    @Value("lobby.closed.lifetime")
    private static long CLOSED_LOBBY_LIFETIME;
    @Value("lobby.active.lifetime")
    private static long ACTIVE_LOBBY_LIFETIME;

    @Autowired
    private SessionService sessionService;

    private BiMap<String, Lobby> playerToLobby = Maps.synchronizedBiMap(HashBiMap.create());
    private Map<String, Lobby> roomIDToLobby = new ConcurrentHashMap<>();
    private Multimap<Lobby, DeferredResult<LobbyDTO>> subscriptions =
            Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private RoomIDGenerator idGenerator = new RoomIDGenerator();
    private Set<ClosedLobby> closedLobbies = Collections.synchronizedSet(new HashSet<>());

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

    public void addSubscription(String token, DeferredResult<LobbyDTO> result) {
        var lobby = playerToLobby.get(token);
        if (lobby == null) {
            throw new RuntimeException("Player not in any lobby");
        }
        addSubscription(lobby, result);
    }

    public void addSubscription(Lobby lobby, DeferredResult<LobbyDTO> result) {
        if (lobby.getState() == LobbyDTO.LobbyState.Closed) {
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

    public void deleteRoom(String token) {
        Lobby lobby = playerToLobby.get(token);
        if (lobby == null) {
            throw new RuntimeException("Player not in any lobby");
        }
        lobby.setState(LobbyDTO.LobbyState.Closed);
        notifySubscribers(lobby);
        closedLobbies.add(new ClosedLobby(lobby, System.currentTimeMillis()));
    }

    public void leaveLobby(String token) {
        var lobby = playerToLobby.get(token);
        if (lobby == null) {
            throw new RuntimeException("Player not in any lobby");
        }
        synchronized (lobby) {
            lobby.removePlayer(playerByToken(token));
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
        if (lobby.getState() != LobbyDTO.LobbyState.Waiting) {
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
        subscriptions.get(lobby).forEach(res -> notifySubscriber(res, lobby));
    }

    private void notifySubscriber(DeferredResult<LobbyDTO> result, Lobby lobby) {
        result.setResult(LobbyConverter.convert(lobby));
    }

    private Player playerByToken(String token) {
        return new Player(sessionService.getSession(token));
    }


    @Scheduled(fixedDelay = 30000)
    private void zombieLobbyMarker() {
        long time = System.currentTimeMillis();

        long count = roomIDToLobby.values().stream()
                .filter(lobby -> time - lobby.getCreationTime() > ACTIVE_LOBBY_LIFETIME)
                .peek(lobby -> deleteRoom(lobby.getId()))
                .count();
        LOG.info("zombieLobbyMarker marked " + count + "lobbies");
        LOG.info("Currently active lobbies: " + roomIDToLobby.values().size());
    }

    @Scheduled(fixedDelay = 10000)
    private void closedLobbyCollector() {
        long time = System.currentTimeMillis();
        BiMap<Lobby, String> lobbyToPlayer = playerToLobby.inverse();
        var toDelete = closedLobbies.stream()
                .filter(cl -> cl.timestamp < time - CLOSED_LOBBY_LIFETIME)
                .collect(Collectors.toSet());
        LOG.info("closedLobbyCollector is deleting " + toDelete.size() + " lobbies");
        toDelete.forEach(closedLobby -> {
            var lobby = closedLobby.lobby;
            lobbyToPlayer.remove(lobby);
            roomIDToLobby.remove(lobby.getId());
            subscriptions.removeAll(lobby);
            idGenerator.recall(lobby.getId());
            closedLobbies.remove(closedLobby);
        });
    }

}
