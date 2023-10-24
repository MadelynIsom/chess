package passoffTests.serverTests;
import dataAccess.*;
import model.*;
import org.junit.jupiter.api.*;
import service.*;
import java.util.UUID;

import javax.print.attribute.standard.JobName;
import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceTests {

    @BeforeEach
    public void clearDatabase(){
        ClearApplication.clearDatabase();
    }

    @Test
    @DisplayName("Register New User")
    public void successfullyRegisterNewUser() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("madelyn", "hi", "madelyn@gmail.com");
        RegisterResponse response = new Register().register(request);
        assertEquals(200, response.statusCode.code);
        model.AuthToken token = AuthDAO.getToken(response.authToken.authToken);
        assert(token != null);
        model.User user = UserDAO.getUser("madelyn");
        assert(user.username.equals("madelyn"));
        assert(user.password.equals("hi"));
        assert(user.email.equals("madelyn@gmail.com"));
    }

    @Test
    @DisplayName("Register Existing User")
    public void alreadyTakenRegisterUser() throws DataAccessException {
        UserDAO.createUser(new User("madelyn", "M0nsterMash200", "madelyn@gmail.com"));
        RegisterRequest request = new RegisterRequest("madelyn", "hi", "george@yahoo.com");
        RegisterResponse response = new Register().register(request);
        assert(response.statusCode.code ==  403);
        assert(response.authToken == null);
        model.User user = UserDAO.getUser("madelyn");
        assert(user.username.equals("madelyn"));
        assert(user.password.equals("M0nsterMash200"));
        assert(user.email.equals("madelyn@gmail.com"));
    }

    @Test
    @DisplayName("Login")
    public void successfullyLoginUser() throws DataAccessException {
        UserDAO.createUser(new User("madelyn", "M0nsterMash200", "madelyn@gmail.com"));
        LoginRequest request = new LoginRequest("madelyn", "M0nsterMash200");
        LoginResponse response = new Login().login(request);
        assert(response.statusCode.code == 200);
        assert(AuthDAO.getToken(response.authToken.authToken) != null);
    }

    @Test
    @DisplayName("Login Unauthorized User")
    public void unauthorizedLoginUser() {
        LoginRequest request = new LoginRequest("joe", "sneaky");
        LoginResponse response = new Login().login(request);
        assert(response.statusCode.code == 401 && response.errorMessage.equals("Error: unauthorized"));
        assert(response.authToken == null);
    }

    @Test
    @DisplayName("Logout")
    public void successfullyLogoutUser() {
        RegisterRequest request = new RegisterRequest("Edmond", "IWantOUT", "CountOfMonteCristo@getRevenge.com");
        RegisterResponse response = new Register().register(request);
        String token = response.authToken.authToken;
        LogoutRequest logoutRequest = new LogoutRequest(token);
        LogoutResponse logoutResponse = new Logout().logout(logoutRequest);
        assert(logoutResponse.statusCode.code == 200);
    }

    @Test
    @DisplayName("Logout Unauthorized User")
    public void unauthorizedLogoutUser() {
        UUID uuid = UUID.randomUUID();
        LogoutRequest request = new LogoutRequest(uuid.toString());
        LogoutResponse response = new Logout().logout(request);
        assert(response.statusCode.code == 401);
    }

    @Test
    @DisplayName("List Games")
    public void successfullyListGames() {
        RegisterRequest request = new RegisterRequest("AbominableSnowman", "FrostGiant335", "SnowCones4Life@gmail.com");
        RegisterResponse response = new Register().register(request);
        String token = response.authToken.authToken;

        CreateGameRequest createGameRequest1 = new CreateGameRequest(token, "Game1");
        new CreateGame().createGame(createGameRequest1);
        CreateGameRequest createGameRequest2 = new CreateGameRequest(token, "Game2");
        new CreateGame().createGame(createGameRequest2);

        ListGamesRequest listGamesRequest = new ListGamesRequest(token);
        ListGamesResponse listGamesResponse = new ListGames().listGames(listGamesRequest);
        assert(listGamesResponse.statusCode.code == 200);
        assert(listGamesResponse.games.size() == 2);
    }

    @Test
    @DisplayName("Unauthorized List Games")
    public void unauthorizedListGames() {
        UUID uuid = UUID.randomUUID();
        ListGamesRequest request = new ListGamesRequest(uuid.toString());
        ListGamesResponse response = new ListGames().listGames(request);
        assert(response.statusCode.code == 401 && response.errorMessage.equals("Error: unauthorized"));
        assert(response.games == null);
    }

    @Test
    @DisplayName("Create New Game")
    public void successfullyCreateNewGame() {
        RegisterRequest request = new RegisterRequest("WinnieThePooh", "IAlwaysForget", "HoneyHoney@HundredAcreWoods.com");
        RegisterResponse response = new Register().register(request);
        String token = response.authToken.authToken;
        CreateGameRequest createGameRequest = new CreateGameRequest(token, "myGame");
        CreateGameResponse createGameResponse = new CreateGame().createGame(createGameRequest);
        assert(createGameResponse.statusCode.code == 200);
        assert(createGameResponse.gameID != null);
    }

    @Test
    @DisplayName("Unauthorized To Create Game")
    public void unauthorizedCreateGame() {
        UUID uuid = UUID.randomUUID();
        CreateGameRequest request = new CreateGameRequest(uuid.toString(), "sneakyGame");
        CreateGameResponse response = new CreateGame().createGame(request);
        assert(response.statusCode.code == 401 && response.errorMessage.equals("Error: unauthorized"));
        assert(response.gameID == null);
    }

    @Test
    @DisplayName("Two Players Join Game")
    public void successfullyJoinGame() {
        RegisterRequest request1 = new RegisterRequest("Scar", "#MyNewEra", "Scar@PrideRock.com");
        RegisterResponse response1 = new Register().register(request1);
        String token1 = response1.authToken.authToken;
        RegisterRequest request2 = new RegisterRequest("Simba", "RememberWhoYouRRR", "Simba@PrideRock.com");
        RegisterResponse response2 = new Register().register(request2);
        String token2 = response2.authToken.authToken;

        CreateGameResponse createGameResponse = new CreateGame().createGame(new CreateGameRequest(token1, "For The Throne"));
        Integer gameID = createGameResponse.gameID;

        JoinGameRequest joinGameRequest1 = new JoinGameRequest(token1, PlayerColor.BLACK, gameID);
        JoinGameResponse joinGameResponse1 = new JoinGame().joinGame(joinGameRequest1);

        JoinGameRequest joinGameRequest2 = new JoinGameRequest(token2, PlayerColor.WHITE, gameID);
        JoinGameResponse joinGameResponse2 = new JoinGame().joinGame(joinGameRequest2);

        assert(joinGameResponse1.statusCode.code == 200);
        assert(joinGameResponse2.statusCode.code == 200);
    }

    @Test
    @DisplayName("Unauthorized To Join Game")
    public void unauthorizedJoinGame() {
        UUID uuid = UUID.randomUUID();

        RegisterRequest request = new RegisterRequest("WinnieThePooh", "IAlwaysForget", "HoneyHoney@HundredAcreWoods.com");
        RegisterResponse response = new Register().register(request);
        String token = response.authToken.authToken;
        CreateGameRequest createGameRequest = new CreateGameRequest(token, "myGame");
        CreateGameResponse createGameResponse = new CreateGame().createGame(createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(uuid.toString(), PlayerColor.WHITE, createGameResponse.gameID);
        JoinGameResponse joinGameResponse = new JoinGame().joinGame(joinGameRequest);

        assert(joinGameResponse.statusCode.code == 401 && joinGameResponse.errorMessage.equals("Error: unauthorized"));
    }


    @Test
    @DisplayName("Already Taken Join Game")
    public void alreadyTakenJoinGame() {
        RegisterRequest request1 = new RegisterRequest("Scar", "#MyNewEra", "Scar@PrideRock.com");
        RegisterResponse response1 = new Register().register(request1);
        String token1 = response1.authToken.authToken;
        RegisterRequest request2 = new RegisterRequest("Simba", "RememberWhoYouRRR", "Simba@PrideRock.com");
        RegisterResponse response2 = new Register().register(request2);
        String token2 = response2.authToken.authToken;
        RegisterRequest request3 = new RegisterRequest("Timon", "HakunaMatata", "Timon@PrideRock.com");
        RegisterResponse response3 = new Register().register(request3);
        String token3 = response3.authToken.authToken;

        CreateGameResponse createGameResponse = new CreateGame().createGame(new CreateGameRequest(token1, "For The Throne"));
        Integer gameID = createGameResponse.gameID;

        JoinGameRequest joinGameRequest1 = new JoinGameRequest(token1, PlayerColor.BLACK, gameID);
        new JoinGame().joinGame(joinGameRequest1);

        JoinGameRequest joinGameRequest2 = new JoinGameRequest(token2, PlayerColor.WHITE, gameID);
        new JoinGame().joinGame(joinGameRequest2);

        JoinGameRequest joinGameRequest3 = new JoinGameRequest(token3, PlayerColor.WHITE, gameID);
        JoinGameResponse joinGameResponse3 = new JoinGame().joinGame(joinGameRequest3);

        JoinGameRequest joinGameRequest4 = new JoinGameRequest(token3, PlayerColor.BLACK, gameID);
        JoinGameResponse joinGameResponse4 = new JoinGame().joinGame(joinGameRequest4);

        assert(joinGameResponse3.statusCode.code == 403 && joinGameResponse3.errorMessage.equals("Error: already taken"));
        assert(joinGameResponse4.statusCode.code == 403 && joinGameResponse4.errorMessage.equals("Error: already taken"));
    }

}
