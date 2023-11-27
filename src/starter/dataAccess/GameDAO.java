package dataAccess;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Game;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import static dataAccess.Database.DB;

/**
 * represents an AuthToken Data Access Object
 * provides CRUD operations for AuthToken models in the database
 */
public class GameDAO {
    //public static HashMap<Integer, model.Game> gameTable = new HashMap<>(); //Local memory storage
    /**
     * creates a new game in the database
     * @param game model to add
     * @throws DataAccessException if a game with that gameID already exists
     */
    /* memory storage
    public static void createGame(model.Game game) throws DataAccessException {
        if(gameTable.get(game.gameID) != null){
            throw new DataAccessException("Error: already taken");
        }
        gameTable.put(game.gameID, game);
    }

     */

    public static void createGame(model.Game game) throws DataAccessException {
        if(queryGame(game.gameID) == null){
            createGame(game.gameID, game.whiteUsername, game.blackUsername, game.gameName, game.game); //Database Access
        }
        else{
            throw new DataAccessException("Error: already taken");
        }
    }

    private static void createGame(Integer gameID, String whiteUsername, String blackUsername, String gameName, GameImpl game) throws DataAccessException {
        Connection conn = DB.getConnection();
        String jsonGameRep = serializeGame(game);
        try (var preparedStatement = conn.prepareStatement("INSERT INTO gameTable (gameID, whiteUsername, blackUsername, gameName, gameImpl) VALUES(?, ?, ?, ?, ?)")) {
            preparedStatement.setInt(1, gameID);
            preparedStatement.setString(2, whiteUsername);
            preparedStatement.setString(3, blackUsername);
            preparedStatement.setString(4, gameName);
            preparedStatement.setString(5, jsonGameRep);

            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        finally {
            DB.closeConnection(conn);
        }
    }

    /**
     * gets a game from the database
     * @param gameID gameID of the game to get
     * @return model of the game
     * @throws DataAccessException if the game doesn't exist
     */
    /* //Memory storage
    public  static model.Game getGame(int gameID) throws DataAccessException {
        if(gameTable.get(gameID) == null){
            throw new DataAccessException("Error: bad request");
        }
        return gameTable.get(gameID);
    }

     */

    public static model.Game getGame(int gameID) throws DataAccessException {
        model.Game game = queryGame(gameID);
        if(game != null){
            return game;
        }
        else{
            throw new DataAccessException("Error: unauthorized");
        }
    }

    private static model.Game queryGame(int gameID) throws DataAccessException {
        Connection conn = DB.getConnection();
        try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, gameImpl FROM gameTable WHERE gameID=?")) {
            preparedStatement.setInt(1, gameID);
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) { // moves to next row in the result set; returns false if there are no more items in the set
                    int ID = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    String gameImpl = rs.getString("gameImpl");

                    GameImpl gameObj = deserializeGame(gameImpl);

                    return new Game(ID, whiteUsername, blackUsername, gameName, gameObj);
                }
                else{
                    return null;
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        finally{
            DB.closeConnection(conn);
        }
    }

    /**
     * returns an array of all games in the database for a user
     * @return list of user's games (empty if there are no active games)
     */
    /* Memory storage:
    public static ArrayList<Game> listGames(){
        ArrayList<Game> games = new ArrayList<>();
        for(Map.Entry<Integer, model.Game> entry : gameTable.entrySet()){
            games.add(entry.getValue());
        }
        return games;
    }
     */

    public static ArrayList<model.Game> listGames() throws DataAccessException {
        Connection conn = DB.getConnection();
        ArrayList<model.Game> games = new ArrayList<>();
        try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, gameImpl FROM gameTable ORDER BY gameID")) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) { // moves to next row in the result set; returns false if there are no more items in the set
                    int ID = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    String gameImpl = rs.getString("gameImpl");

                    GameImpl gameObj = deserializeGame(gameImpl);

                    games.add(new Game(ID, whiteUsername, blackUsername, gameName, gameObj));
                }
                return games;
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        finally{
            DB.closeConnection(conn);
        }
    }

    /**
     * updates game information
     * @param game model to update
     * @throws DataAccessException if game doesn't exist
     */
    /* Memory Storage:
    public static void updateGame(model.Game game) throws DataAccessException {
        Integer gameID = game.gameID;
        if(gameTable.get(gameID) == null){
            throw new DataAccessException("Error: bad request");
        }
        else{
            GameDAO.getGame(gameID).whiteUsername = game.whiteUsername;
            GameDAO.getGame(gameID).blackUsername = game.blackUsername;
            GameDAO.getGame(gameID).gameName = game.gameName;
            GameDAO.getGame(gameID).game = game.game;
        }
    }
     */

    public static void updateGame(model.Game game) throws DataAccessException {
        Connection conn = DB.getConnection();
        if(queryGame(game.gameID) == null){
            throw new DataAccessException("Error: bad request");
        }
        String gameState = serializeGame(game.game);
        try (var preparedStatement = conn.prepareStatement("UPDATE gameTable SET whiteUsername=?, blackUsername=?, gameImpl=? WHERE gameID=?")) {
            preparedStatement.setString(1, game.whiteUsername);
            preparedStatement.setString(2, game.blackUsername);
            preparedStatement.setString(3, gameState);
            preparedStatement.setInt(4, game.gameID);

            preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        finally{
            DB.closeConnection(conn);
        }
    }

    public static void clear() throws DataAccessException{
        //gameTable.clear(); //Memory storage
        DB.truncateTable("gameTable");
    }

    private static String serializeGame(GameImpl game){
        return getSerializer().toJson(game);
    }

    private static GameImpl deserializeGame(String game){
        return getSerializer().fromJson(game, GameImpl.class);
    }

    private static Gson getSerializer() {
        final RuntimeTypeAdapterFactory<ChessGame> gameTypeFactory = RuntimeTypeAdapterFactory
                .of(ChessGame.class, "type")
                .registerSubtype(GameImpl.class);
        final RuntimeTypeAdapterFactory<ChessBoard> boardTypeFactory = RuntimeTypeAdapterFactory
                .of(ChessBoard.class, "type")
                .registerSubtype(BoardImpl.class);
        final RuntimeTypeAdapterFactory<ChessPiece> pieceTypeFactory = RuntimeTypeAdapterFactory
                .of(ChessPiece.class, "type")
                .registerSubtype(PieceImpl.class);

        var builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(gameTypeFactory);
        builder.registerTypeAdapterFactory(boardTypeFactory);
        builder.registerTypeAdapterFactory(pieceTypeFactory);
        return builder.create();
    }}
