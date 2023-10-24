package service;

import chess.GameImpl;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.Game;
import java.util.Random;

/**
 * creates a game
 */
public class CreateGame {
    /**
     * creates a new game for the user given the name it will be called
     * @param createGameRequest contains the name of the game
     * @return the response object containing the game model(if successful), a status code, and an error message (if one is thrown)
     */
    public static CreateGameResponse createGame(CreateGameRequest createGameRequest){
        try{
            if(AuthDAO.getToken(createGameRequest.authToken) != null){
                Random rand = new Random();
                Integer gameID = rand.nextInt(999999999);
                GameImpl gameImpl = new GameImpl();
                model.Game game = new Game(gameID,null, null, createGameRequest.gameName, gameImpl);
                GameDAO.createGame(game);
                return new CreateGameResponse(game.gameID);
            }
            else{
                return new CreateGameResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
            }
        }
        catch(DataAccessException e){
            return new CreateGameResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
        }
    }
}
