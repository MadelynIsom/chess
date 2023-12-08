package passoffTests.serverTests;

import chess.GameImpl;
import chess.InvalidMoveException;
import chess.MoveImpl;
import chess.PositionImpl;
import dataAccess.*;
import org.junit.jupiter.api.*;
import service.ClearApplication;
import model.*;

import java.util.ArrayList;
import java.util.UUID;

import static dataAccess.AuthDAO.*;
import static dataAccess.Database.DB;
import static dataAccess.GameDAO.*;
import static dataAccess.UserDAO.createUser;
import static dataAccess.UserDAO.getUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dataAccess.DataAccessException;

import org.junit.jupiter.api.Assertions;


public class DAOTests {
    private static final UUID VALID_UUID = UUID.randomUUID();
    private static final AuthToken token = new AuthToken("madD0g", VALID_UUID.toString());

    private static final User user = new User("georgie", "help", "SS.Georgie");

    private static final Game game = new Game(2, "Simba", "Scar", "For the Throne", new GameImpl(), false);
    private static final Game gameUpdate = new Game(2, "Nala", "Simba", "For Pride Rock", new GameImpl(), false);
    private static final Game game2 = new Game(45, "Charlie", "Willie", "For the Candy!", new GameImpl(), false);

    @BeforeAll
    public static void configureDatabase() throws DataAccessException {
        DB.configureDatabase();
    }

    @BeforeEach
    public void clearDatabase(){
        ClearApplication.clearDatabase();
    }

    @Test
    @DisplayName("TestDAOClears")
    public void testDAOClears() {
        Assertions.assertDoesNotThrow(AuthDAO::clear);
        Assertions.assertDoesNotThrow(UserDAO::clear);
        Assertions.assertDoesNotThrow(GameDAO::clear);
    }

    @Test
    @DisplayName("successfulCreateToken")
    public void successfulCreateToken() throws DataAccessException {
        createToken(token);
        assertEquals(token.authToken, getToken(token.authToken).authToken);
        assertEquals(token.username, getToken(token.authToken).username);
    }

    @Test
    @DisplayName("failToCreateDuplicateToken")
    public void failToCreateDuplicateToken() throws DataAccessException {
        createToken(token);
        Assertions.assertThrows(dataAccess.DataAccessException.class, () ->createToken(token), "Error: already taken");
    }

    @Test
    @DisplayName("successfulGetToken")
    public void successfulGetToken() throws DataAccessException {
        createToken(token);
        assertEquals(token.authToken, getToken(token.authToken).authToken);
    }

    @Test
    @DisplayName("failToGetToken")
    public void failToGetToken() {
        Assertions.assertThrows(dataAccess.DataAccessException.class, () -> getToken(token.authToken), "Error: unauthorized");
    }

    @Test
    @DisplayName("successfulDeleteToken")
    public void successfulDeleteToken() throws DataAccessException {
        createToken(token);
        deleteToken(token.authToken);
        Assertions.assertThrows(dataAccess.DataAccessException.class, () -> getToken(token.authToken), "Error: unauthorized");
    }

    @Test
    @DisplayName("failToDeleteToken")
    public void failToDeleteToken() {
        Assertions.assertThrows(dataAccess.DataAccessException.class, () -> getToken(token.authToken), "Error: unauthorized");
    }

    @Test
    @DisplayName("successfulCreateUser")
    public void successfulCreateUser() throws DataAccessException {
        createUser(user);
        assertEquals(user.username, getUser(user.username).username);
        assertEquals(user.password, getUser(user.username).password);
        assertEquals(user.email, getUser(user.username).email);
    }

    @Test
    @DisplayName("failToCreateDuplicateUser")
    public void failToCreateDuplicateUser() throws DataAccessException {
        createUser(user);
        Assertions.assertThrows(dataAccess.DataAccessException.class, () ->createUser(user), "Error: already taken");
    }

    @Test
    @DisplayName("successfulGetUser")
    public void successfulGetUser() throws DataAccessException {
        createUser(user);
        assertEquals(user.username, getUser(user.username).username);
    }

    @Test
    @DisplayName("failToGetUser")
    public void failToGetUser() {
        Assertions.assertThrows(dataAccess.DataAccessException.class, () -> getUser(token.authToken), "Error: unauthorized");
    }
    @Test
    @DisplayName("successfulCreateGame")
    public void successfulCreateGame() throws DataAccessException {
        createGame(game);
        model.Game savedGame = getGame(game.gameID);
        assertEquals(game.gameID, savedGame.gameID);
        assertEquals(game.whiteUsername, savedGame.whiteUsername);
        assertEquals(game.blackUsername, savedGame.blackUsername);
        assertEquals(game.gameName, savedGame.gameName);
        assertEquals(game.game, savedGame.game);
    }

    @Test
    @DisplayName("failToCreateDuplicateGame")
    public void failToCreateDuplicateGame() throws DataAccessException {
        createGame(game);
        Assertions.assertThrows(dataAccess.DataAccessException.class, () ->createGame(game), "Error: already taken");
    }

    @Test
    @DisplayName("successfulGetGame")
    public void successfulGetGame() throws DataAccessException {
        createGame(game);
        model.Game savedGame = getGame(game.gameID);
        assertEquals(game.gameID, savedGame.gameID);
        assertEquals(game.whiteUsername, savedGame.whiteUsername);
        assertEquals(game.blackUsername, savedGame.blackUsername);
        assertEquals(game.gameName, savedGame.gameName);
        assertEquals(game.game, savedGame.game);
    }

    @Test
    @DisplayName("failToGetGame")
    public void failToGetGame() {
        Assertions.assertThrows(dataAccess.DataAccessException.class, () -> getGame(game.gameID), "Error: unauthorized");
    }

    @Test
    @DisplayName("successfulListGames")
    public void successfulListGames() throws DataAccessException {
        createGame(game);
        createGame(game2);
        ArrayList<Game> games = listGames();

        assertEquals(game, games.get(0));
        assertEquals(game2, games.get(1));
    }

    @Test
    @DisplayName("failToListGames")
    public void failToListGames() throws DataAccessException {
        assertEquals(new ArrayList<Game>(), listGames());
    }

    @Test
    @DisplayName("successfulUpdateGames")
    public void successfulUpdateGame() throws DataAccessException, InvalidMoveException {
        createGame(game);
        gameUpdate.game.makeMove(new MoveImpl(new PositionImpl(2,1), new PositionImpl(3, 1), null));
        updateGame(gameUpdate);
        Game savedGame = getGame(game.gameID);
        assertEquals(gameUpdate.whiteUsername, savedGame.whiteUsername);
        assertEquals(gameUpdate.blackUsername, savedGame.blackUsername);
        assertEquals(gameUpdate.game, savedGame.game);
    }

    @Test
    @DisplayName("failToUpdateGame")
    public void failToUpdateGame() {
        Assertions.assertThrows(dataAccess.DataAccessException.class, () -> updateGame(game), "Error: unauthorized");
    }

}
