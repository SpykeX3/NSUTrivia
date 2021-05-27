package ru.nsu.trivia.server.lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.request.async.DeferredResult;
import ru.nsu.trivia.common.dto.model.LobbyDTO;
import ru.nsu.trivia.common.dto.model.LobbyState;
import ru.nsu.trivia.common.dto.model.task.Answer;
import ru.nsu.trivia.common.dto.model.task.TaskDTO;
import ru.nsu.trivia.server.model.GameConfiguration;
import ru.nsu.trivia.server.model.Lobby;
import ru.nsu.trivia.server.model.Player;
import ru.nsu.trivia.server.model.converters.LobbyConverter;
import ru.nsu.trivia.server.sessions.SessionData;
import ru.nsu.trivia.server.sessions.SessionService;
import ru.nsu.trivia.server.task.TaskService;

public class LobbyService {

    private static Logger LOG = Logger.getLogger(LobbyService.class.getName());


    private final SessionService sessionService;
    private final TaskService taskService;
    private final long closedLobbyLifetime;
    private final long activeLobbyLifetime;


    private final Map<String, Lobby> playerToLobby = new ConcurrentHashMap<>();
    private final Map<String, Lobby> roomIDToLobby = new ConcurrentHashMap<>();
    private final Multimap<Lobby, DeferredResult<LobbyDTO>> subscriptions =
            Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final RoomIDGenerator idGenerator = new RoomIDGenerator();
    private final Set<ClosedLobby> closedLobbies = Collections.synchronizedSet(new HashSet<>());
    private final PriorityBlockingQueue<Lobby> runningLobbies = new PriorityBlockingQueue<>();

    private final GameConfiguration gameConfiguration = new GameConfiguration(
            List.of(TaskDTO.Type.Select_answer, TaskDTO.Type.Select_answer, TaskDTO.Type.Select_answer));

    public LobbyService(SessionService sessionService, TaskService taskService, long closedLobbyLifetime,
                        long activeLobbyLifetime) {
        this.sessionService = sessionService;
        this.taskService = taskService;
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
        lobby.setGameConfiguration(gameConfiguration);
        roomIDToLobby.put(id, lobby);
        return id;
    }

    public void deleteRoom(String id, LobbyState state) {
        Lobby lobby = roomIDToLobby.get(id);
        if (lobby == null) {
            return;
        }
        lobby.setState(state);
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
        synchronized (lobby) {
            lobby.setState(LobbyState.Playing);
            lobby.setNewTask(taskService.generateTask(lobby));
            runningLobbies.add(lobby);
        }
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
                deleteRoom(lobby.getId(), LobbyState.Closed);
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

    public void submitAnswer(Answer answer) {
        String token = answer.getToken();
        Lobby lobby = playerToLobby.get(token);
        if (lobby == null) {
            throw new RuntimeException("Player not in any lobby");
        }
        if (lobby.getState() != LobbyState.Playing) {
            throw new RuntimeException("Invalid lobby state: " + lobby.getState().toString());
        }
        if (lobby.getRound() != answer.getRound()) {
            throw new RuntimeException("Invalid round " + answer.getRound() + ", current round is " + lobby.getRound());
        }
        synchronized (lobby) {
            Player player =
                    lobby.getPlayers().stream()
                            .filter(p -> p.getToken().equals(token))
                            .findFirst().orElseThrow(() -> new RuntimeException("Player is not in lobby " + lobby.getId()));
            if (player.isAnswered()) {
                throw new RuntimeException("Player already submitted answer for round " + lobby.getRound());
            }
            int scoreGain = taskService.getScore(lobby.getCurrentTask(), answer);
            player.setScore(player.getScore() + scoreGain);
            player.setAnswered(true);
            if (lobby.getPlayers().stream().allMatch(Player::isAnswered)) {
                finishRound(lobby);
            }
        }
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
        List<DeferredResult<LobbyDTO>> subs = new ArrayList<>(subscriptions.get(lobby));
        subs.forEach(res -> notifySubscriber(res, lobby));
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
                .peek(lobby -> deleteRoom(lobby.getId(), LobbyState.Closed))
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

    @Scheduled(fixedDelay = 1500)
    private void proceedTasks() {
        Lobby lobby = runningLobbies.poll();
        long time = System.currentTimeMillis();
        while (lobby != null && lobby.getTaskDeadline() + 8000 < time) { // TODO process lag (maybe use properties)
            synchronized (lobby) {
                if (lobby.getState() != LobbyState.Playing) {
                    continue;
                }
                finishRound(lobby);
                //runningLobbies.add(lobby);
                lobby = runningLobbies.poll();
            }
        }
    }

    private void finishRound(Lobby lobby) {
        if (lobby.getRound() > lobby.getGameConfiguration().getTaskTypes().size()) {
            deleteRoom(lobby.getId(), LobbyState.Finished);
            return;
        }
        lobby.setNewTask(taskService.generateTask(lobby));
        lobby.getPlayers().forEach(p -> p.setAnswered(false));
        notifySubscribers(lobby);
    }

}
