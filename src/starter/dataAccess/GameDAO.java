package dataAccess;

import model.AuthToken;
import model.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * represents an AuthToken Data Access Object
 * provides CRUD operations for AuthToken models in the database
 */
public class GameDAO {
    public static HashMap<Integer, model.Game> gameTable = new HashMap<>();
    /**
     * creates a new game in the database
     * @param game model to add
     * @throws DataAccessException if a game with that gameID already exists
     */
    public static void createGame(model.Game game) throws DataAccessException {
        if(gameTable.get(game.gameID) != null){
            throw new DataAccessException("Error: already taken");
        }
        gameTable.put(game.gameID, game);
    }

    /**
     * gets a game from the database
     * @param gameID gameID of the game to get
     * @return model of the game
     * @throws DataAccessException if the game doesn't exist
     */
    public  static model.Game getGame(int gameID) throws DataAccessException {
        if(gameTable.get(gameID) == null){
            throw new DataAccessException("Error: bad request");
        }
        return gameTable.get(gameID);
    }

    /**
     * returns an array of all games in the database for a user
     * @return list of user's games (empty if there are no active games)
     */
    public static ArrayList<Game> listGames(){
        ArrayList<Game> games = new ArrayList<>();
        for(Map.Entry<Integer, model.Game> entry : gameTable.entrySet()){
            games.add(entry.getValue());
        }
        return games;
    }

    /**
     * updates game information
     * @param game model to update
     * @throws DataAccessException if game doesn't exist
     */
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

    public static void clear(){
        gameTable.clear();
    }

}
