package ru.nsu.trivia.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import ru.nsu.trivia.common.dto.model.LobbyDTO;
import ru.nsu.trivia.common.dto.model.LobbyState;
import ru.nsu.trivia.common.dto.model.PlayerInLobby;
import ru.nsu.trivia.common.dto.model.task.SelectAnswerAnswer;
import ru.nsu.trivia.common.dto.requests.ChangeUsernameRequest;
import ru.nsu.trivia.common.dto.requests.JoinLobbyRequest;
import ru.nsu.trivia.common.dto.requests.UsingTokenRequest;
import ru.nsu.trivia.common.dto.responses.StatusResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameServerApplicationTests {

    private final static String username1 = "test-user-1";
    private final static String username2 = "test-user-2";

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @LocalServerPort
    private int port;

    private String tokenUser1 = "";
    private String tokenUser2 = "";

    @Autowired
    private TestRestTemplate restTemplate;

    private String getUrl(String path) {
        return "http://localhost:" + port + "/" + path;
    }

    @BeforeAll
    void setTokens() {
        tokenUser1 = restTemplate.postForObject(getUrl("token/generate"), null, String.class);
        tokenUser2 = restTemplate.postForObject(getUrl("token/generate"), null, String.class);
        restTemplate.postForObject(getUrl("user/nickname"), new ChangeUsernameRequest(tokenUser1, username1),
                StatusResponse.class);
        restTemplate.postForObject(getUrl("user/nickname"), new ChangeUsernameRequest(tokenUser2, username2),
                StatusResponse.class);

    }


    @Test
    void contextLoads() {
    }

    @Test
    void generateToken() {
        String token = restTemplate.postForObject(getUrl("token/generate"), null, String.class);
        log.info("Token received: " + token);
        assertEquals(128, token.length());
    }

    @Test
    void createLobbyTest() {
        LobbyDTO lobby = restTemplate.postForObject(getUrl("lobby/create"), new UsingTokenRequest(tokenUser1),
                LobbyDTO.class);
        assertEquals(1, lobby.getPlayers().size());
        assertTrue(lobby.getPlayers().stream().anyMatch(p -> p.getUsername().equals(username1)));
    }

    @Test
    void joinLobbyTest() {
        LobbyDTO lobby = restTemplate.postForObject(getUrl("lobby/create"), new UsingTokenRequest(tokenUser1),
                LobbyDTO.class);

        LobbyDTO joinedLobby = restTemplate.postForObject(getUrl("lobby/join"), new JoinLobbyRequest(tokenUser2,
                lobby.getId()), LobbyDTO.class);

        assertEquals(2, joinedLobby.getPlayers().size());
        assertTrue(joinedLobby.getPlayers().stream().anyMatch(p -> p.getUsername().equals(username1)));
        assertTrue(joinedLobby.getPlayers().stream().anyMatch(p -> p.getUsername().equals(username2)));
        assertEquals(List.of(username1), joinedLobby.getPlayers().stream()
                .filter(PlayerInLobby::isHost)
                .map(PlayerInLobby::getUsername)
                .collect(Collectors.toList()));
    }

    @Test
    void getLobbyTest() {
        LobbyDTO lobby = restTemplate.postForObject(getUrl("lobby/create"), new UsingTokenRequest(tokenUser1),
                LobbyDTO.class);

        restTemplate.postForObject(getUrl("lobby/join"), new JoinLobbyRequest(tokenUser2,
                lobby.getId()), LobbyDTO.class);

        LobbyDTO joinedLobby2 = restTemplate.getForObject(getUrl("lobby/get?token=" + tokenUser2), LobbyDTO.class);
        assertNotNull(joinedLobby2);
        assertNotNull(joinedLobby2.getPlayers());
        assertEquals(2, joinedLobby2.getPlayers().size());
        assertTrue(joinedLobby2.getPlayers().stream().anyMatch(p -> p.getUsername().equals(username1)));
        assertTrue(joinedLobby2.getPlayers().stream().anyMatch(p -> p.getUsername().equals(username2)));
        assertEquals(List.of(username1), joinedLobby2.getPlayers().stream()
                .filter(PlayerInLobby::isHost)
                .map(PlayerInLobby::getUsername)
                .collect(Collectors.toList()));

        LobbyDTO joinedLobby1 = restTemplate.getForObject(getUrl("lobby/get?token=" + tokenUser1), LobbyDTO.class);
        assertNotNull(joinedLobby1);
        assertNotNull(joinedLobby1.getPlayers());
        assertEquals(2, joinedLobby1.getPlayers().size());
        assertTrue(joinedLobby1.getPlayers().stream().anyMatch(p -> p.getUsername().equals(username1)));
        assertTrue(joinedLobby1.getPlayers().stream().anyMatch(p -> p.getUsername().equals(username2)));
        assertEquals(List.of(username1), joinedLobby1.getPlayers().stream()
                .filter(PlayerInLobby::isHost)
                .map(PlayerInLobby::getUsername)
                .collect(Collectors.toList()));
    }

    @Test
    @Timeout(value = 5)
    void subscriptionTest() throws ExecutionException, InterruptedException {
        LobbyDTO lobby = restTemplate.postForObject(getUrl("lobby/create"), new UsingTokenRequest(tokenUser1),
                LobbyDTO.class);
        CompletableFuture<LobbyDTO> lobbyFuture = new CompletableFuture<>();
        new Thread(() -> {
            LobbyDTO lobbyDTO = restTemplate.getForObject(getUrl("lobby/subscribe?token=" + tokenUser1) +
                            "&lastUpdate=" + lobby.getLastUpdated(),
                    LobbyDTO.class);
            lobbyFuture.complete(lobbyDTO);
        }).start();
        Thread.sleep(1000);
        restTemplate.postForObject(getUrl("lobby/join"), new JoinLobbyRequest(tokenUser2,
                lobby.getId()), LobbyDTO.class);
        LobbyDTO subscribedLobby = lobbyFuture.get();
        assertNotNull(subscribedLobby);
        assertNotNull(subscribedLobby.getPlayers());
        assertEquals(2, subscribedLobby.getPlayers().size());
        assertTrue(subscribedLobby.getPlayers().stream().anyMatch(p -> p.getUsername().equals(username1)));
        assertTrue(subscribedLobby.getPlayers().stream().anyMatch(p -> p.getUsername().equals(username2)));
        assertEquals(List.of(username1), subscribedLobby.getPlayers().stream()
                .filter(PlayerInLobby::isHost)
                .map(PlayerInLobby::getUsername)
                .collect(Collectors.toList()));
    }

    private LobbyDTO createLobby() {
        LobbyDTO lobby = restTemplate.postForObject(getUrl("lobby/create"), new UsingTokenRequest(tokenUser1),
                LobbyDTO.class);
        return restTemplate.postForObject(getUrl("lobby/join"), new JoinLobbyRequest(tokenUser2,
                lobby.getId()), LobbyDTO.class);
    }

    @Test
        //@Timeout(value = 5)
    void startLobbyTest() throws InterruptedException, ExecutionException {
        LobbyDTO lobby = createLobby();
        final long lastUpdated = lobby.getLastUpdated();

        CompletableFuture<LobbyDTO> lobbyFuture = new CompletableFuture<>();
        new Thread(() -> {
            LobbyDTO lobbyDTO = restTemplate.getForObject(getUrl("lobby/subscribe?token=" + tokenUser1) +
                            "&lastUpdate=" + lastUpdated,
                    LobbyDTO.class);
            System.out.println(lobbyDTO);
            lobbyFuture.complete(lobbyDTO);
        }).start();
        Thread.sleep(2000);
        StatusResponse resp = restTemplate.postForObject(getUrl("lobby/start"), new UsingTokenRequest(tokenUser1),
                StatusResponse.class);
        assertEquals(0, resp.code);
        assertNull(resp.errors);
        lobby = lobbyFuture.get();
        assertEquals(LobbyState.Playing, lobby.getState());
        assertNotNull(lobby.getCurrentTask());
    }

    @Test
    @Timeout(value = 5)
    void submitAnswerTest() throws InterruptedException, ExecutionException {
        createLobby();
        StatusResponse resp = restTemplate.postForObject(getUrl("lobby/start"), new UsingTokenRequest(tokenUser1),
                StatusResponse.class);
        assertEquals(0, resp.code);
        assertNull(resp.errors);
        LobbyDTO lobby = restTemplate.getForObject(getUrl("lobby/get?token=" + tokenUser1), LobbyDTO.class);
        assertEquals(LobbyState.Playing, lobby.getState());
        assertNotNull(lobby.getCurrentTask());
        final long lastUpdated = lobby.getLastUpdated();

        CompletableFuture<LobbyDTO> lobbyFuture = new CompletableFuture<>();
        new Thread(() -> {
            LobbyDTO lobbyDTO = restTemplate.getForObject(getUrl("lobby/subscribe?token=" + tokenUser1) +
                            "&lastUpdate=" + lastUpdated,
                    LobbyDTO.class);
            lobbyFuture.complete(lobbyDTO);
        }).start();
        StatusResponse submit1 = restTemplate.postForObject(getUrl("lobby/answer"),
                new SelectAnswerAnswer(tokenUser1, 0, 1), StatusResponse.class);
        assertEquals(0, submit1.code);
        assertNull(submit1.errors);
        StatusResponse submit2 = restTemplate.postForObject(getUrl("lobby/answer"),
                new SelectAnswerAnswer(tokenUser2, 1, 1), StatusResponse.class);
        assertEquals(0, submit2.code);
        assertNull(submit2.errors);

        lobby = lobbyFuture.get();
        assertEquals(2, lobby.getRound());
        assertEquals(0, lobby.getPlayers().get(0).getScore());
        assertTrue(lobby.getPlayers().get(1).getScore() > 0);
    }

}
