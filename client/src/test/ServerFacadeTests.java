
import org.junit.jupiter.api.*;
import request_response.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    URI uri = new URI("http://localhost:8080");
    ServerFacade server = new ServerFacade(uri);

    public ServerFacadeTests() throws URISyntaxException {
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        server.clearDatabase();
    }

    @Test
    @DisplayName("Register New User")
    public void successfullyRegisterNewUser() throws Exception {
        RegisterRequest request = new RegisterRequest("madelyn", "hi", "madelyn@gmail.com");
        RegisterResponse response = server.register(request);
        assertEquals(200, response.statusCode.code);
        assertNotNull(response.authToken);
        assertEquals("madelyn", response.authToken.username);
    }


    @Test
    @DisplayName("Register Existing User")
    public void alreadyTakenRegisterUser() throws Exception {
        RegisterRequest request1 = new RegisterRequest("madelyn", "hi", "madelyn@gmail.com");
        RegisterResponse response1 = server.register(request1);
        assertEquals(200, response1.statusCode.code);
        RegisterRequest request2 = new RegisterRequest("madelyn", "M0nsterMash200", "madMichelle@gmail.com");
        RegisterResponse response2 = server.register(request2);
        assertEquals(403, response2.statusCode.code);
    }

    @Test
    @DisplayName("Login")
    public void successfullyLoginUser() throws Exception {
        server.register(new RegisterRequest("madelyn", "hi", "madelyn@gmail.com"));
        LoginRequest request = new LoginRequest("madelyn", "hi");
        LoginResponse response = server.login(request);
        assertEquals(200, response.statusCode.code);
        assertNotNull(response.authToken.authToken);
    }

    @Test
    @DisplayName("Login Unauthorized User")
    public void unauthorizedLoginUser() throws Exception{
        LoginResponse response = server.login(new LoginRequest("joe", "sneaky"));
        assertEquals(401, response.statusCode.code);
        assertNull(response.authToken);
    }

    @Test
    @DisplayName("Logout")
    public void successfullyLogoutUser() throws Exception{
        server.register(new RegisterRequest("madelyn", "hi", "madelyn@gmail.com"));
        LoginResponse response = server.login(new LoginRequest("madelyn", "hi"));
        String token = response.authToken.authToken;
        LogoutRequest logoutRequest = new LogoutRequest(token);
        LogoutResponse logoutResponse = server.logout(logoutRequest);
        assertEquals(200, logoutResponse.statusCode.code);
    }

    @Test
    @DisplayName("Logout Unauthorized User")
    public void unauthorizedLogoutUser() throws Exception{
        UUID uuid = UUID.randomUUID();
        LogoutRequest request = new LogoutRequest(uuid.toString());
        LogoutResponse response = server.logout(request);
        assertEquals(401, response.statusCode.code);
    }

    @Test
    @DisplayName("Create New Game")
    public void successfullyCreateNewGame() throws Exception{
        RegisterRequest request = new RegisterRequest("WinnieThePooh", "IAlwaysForget", "HoneyHoney@HundredAcreWoods.com");
        RegisterResponse response = server.register(request);
        String token = response.authToken.authToken;
        CreateGameRequest createGameRequest = new CreateGameRequest(token, "myGame");
        CreateGameResponse createGameResponse = server.createGame(createGameRequest);
        assertEquals(200, createGameResponse.statusCode.code);
        assertNotNull(createGameResponse.gameID);
    }

    @Test
    @DisplayName("Unauthorized To Create Game")
    public void unauthorizedCreateGame() throws Exception{
        UUID uuid = UUID.randomUUID();
        CreateGameRequest request = new CreateGameRequest(uuid.toString(), "sneakyGame");
        CreateGameResponse response = server.createGame(request);
        assertEquals(401, response.statusCode.code);
        assertNull(response.gameID);
    }

    @Test
    @DisplayName("List Games")
    public void successfullyListGames() throws Exception{
        RegisterRequest request = new RegisterRequest("AbominableSnowman", "FrostGiant335", "SnowCones4Life@gmail.com");
        RegisterResponse response = server.register(request);
        String token = response.authToken.authToken;

        CreateGameRequest createGameRequest1 = new CreateGameRequest(token, "Game1");
        server.createGame(createGameRequest1);
        CreateGameRequest createGameRequest2 = new CreateGameRequest(token, "Game2");
        server.createGame(createGameRequest2);

        ListGamesRequest listGamesRequest = new ListGamesRequest(token);
        ListGamesResponse listGamesResponse = server.listGames(listGamesRequest);
        assertEquals(200, listGamesResponse.statusCode.code);
        assertEquals(2, listGamesResponse.games.size());
    }

    @Test
    @DisplayName("Unauthorized List Games")
    public void unauthorizedListGames() throws Exception{
        UUID uuid = UUID.randomUUID();
        ListGamesRequest request = new ListGamesRequest(uuid.toString());
        ListGamesResponse response = server.listGames(request);
        assertEquals(401, response.statusCode.code);
        assertNull(response.games);
    }


    @Test
    @DisplayName("Two Players Join Game")
    public void successfullyJoinGame() throws Exception{
        RegisterRequest request1 = new RegisterRequest("Scar", "#MyNewEra", "Scar@PrideRock.com");
        RegisterResponse response1 = server.register(request1);
        String token1 = response1.authToken.authToken;
        RegisterRequest request2 = new RegisterRequest("Simba", "RememberWhoYouRRR", "Simba@PrideRock.com");
        RegisterResponse response2 = server.register(request2);
        String token2 = response2.authToken.authToken;

        CreateGameResponse createGameResponse = server.createGame(new CreateGameRequest(token1, "For The Throne"));
        Integer gameID = createGameResponse.gameID;

        JoinGameRequest joinGameRequest1 = new JoinGameRequest(token1, PlayerColor.BLACK, gameID);
        JoinGameResponse joinGameResponse1 = server.joinGame(joinGameRequest1);

        JoinGameRequest joinGameRequest2 = new JoinGameRequest(token2, PlayerColor.WHITE, gameID);
        JoinGameResponse joinGameResponse2 = server.joinGame(joinGameRequest2);

        assertEquals(200, joinGameResponse1.statusCode.code);
        assertEquals(200, joinGameResponse2.statusCode.code);
    }

    @Test
    @DisplayName("Unauthorized To Join Game")
    public void unauthorizedJoinGame() throws Exception{
        UUID uuid = UUID.randomUUID();

        RegisterRequest request = new RegisterRequest("WinnieThePooh", "IAlwaysForget", "HoneyHoney@HundredAcreWoods.com");
        RegisterResponse response = server.register(request);
        String token = response.authToken.authToken;
        CreateGameRequest createGameRequest = new CreateGameRequest(token, "myGame");
        CreateGameResponse createGameResponse = server.createGame(createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(uuid.toString(), PlayerColor.WHITE, createGameResponse.gameID);
        JoinGameResponse joinGameResponse = server.joinGame(joinGameRequest);

        assertEquals(401, joinGameResponse.statusCode.code);
    }


    @Test
    @DisplayName("Already Taken Join Game")
    public void alreadyTakenJoinGame() throws Exception{
        RegisterRequest request1 = new RegisterRequest("Scar", "#MyNewEra", "Scar@PrideRock.com");
        RegisterResponse response1 = server.register(request1);
        String token1 = response1.authToken.authToken;
        RegisterRequest request2 = new RegisterRequest("Simba", "RememberWhoYouRRR", "Simba@PrideRock.com");
        RegisterResponse response2 = server.register(request2);
        String token2 = response2.authToken.authToken;
        RegisterRequest request3 = new RegisterRequest("Timon", "HakunaMatata", "Timon@PrideRock.com");
        RegisterResponse response3 = server.register(request3);
        String token3 = response3.authToken.authToken;

        CreateGameResponse createGameResponse = server.createGame(new CreateGameRequest(token1, "For The Throne"));
        Integer gameID = createGameResponse.gameID;

        JoinGameRequest joinGameRequest1 = new JoinGameRequest(token1, PlayerColor.BLACK, gameID);
        server.joinGame(joinGameRequest1);

        JoinGameRequest joinGameRequest2 = new JoinGameRequest(token2, PlayerColor.WHITE, gameID);
        server.joinGame(joinGameRequest2);

        JoinGameRequest joinGameRequest3 = new JoinGameRequest(token3, PlayerColor.WHITE, gameID);
        JoinGameResponse joinGameResponse3 = server.joinGame(joinGameRequest3);

        JoinGameRequest joinGameRequest4 = new JoinGameRequest(token3, PlayerColor.BLACK, gameID);
        JoinGameResponse joinGameResponse4 = server.joinGame(joinGameRequest4);

        assertEquals(403, joinGameResponse3.statusCode.code);
        assertEquals(403, joinGameResponse4.statusCode.code);
    }

}
