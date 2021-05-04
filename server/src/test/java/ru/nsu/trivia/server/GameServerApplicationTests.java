package ru.nsu.trivia.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import ru.nsu.trivia.common.dto.model.LobbyDTO;
import ru.nsu.trivia.common.dto.model.PlayerInLobby;
import ru.nsu.trivia.common.dto.requests.ChangeUsernameRequest;
import ru.nsu.trivia.common.dto.requests.JoinLobbyRequest;
import ru.nsu.trivia.common.dto.requests.UsingTokenRequest;
import ru.nsu.trivia.common.dto.responses.StatusResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
    void subscriptionTest() throws ExecutionException, InterruptedException {
        LobbyDTO lobby = restTemplate.postForObject(getUrl("lobby/create"), new UsingTokenRequest(tokenUser1),
                LobbyDTO.class);
        CompletableFuture<LobbyDTO> lobbyFuture = new CompletableFuture<>();
        new Thread(() -> {
            LobbyDTO lobbyDTO = restTemplate.getForObject(getUrl("lobby/subscribe?token=" + tokenUser1),
                    LobbyDTO.class);
            lobbyFuture.complete(lobbyDTO);
        }).start();
        Thread.sleep(200);
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

}
