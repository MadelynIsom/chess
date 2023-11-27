package service;

import dataAccess.DataAccessException;
import dataAccess.*;
import model.*;
import request_response.JoinGameRequest;
import request_response.JoinGameResponse;
import request_response.PlayerColor;
import request_response.StatusCode;

/**
 * joins a game
 */
public class JoinGame {
    /**
     * fills the client request to join a game
     * @param joinGameRequest holds intended team color and game id
     * @return response that reports on success or failure
     */
    public static JoinGameResponse joinGame(JoinGameRequest joinGameRequest){
        try{
            AuthDAO.getToken(joinGameRequest.authToken);
            String username = AuthDAO.getToken(joinGameRequest.authToken).username;
            Game game;
            try{
                game = GameDAO.getGame(joinGameRequest.gameID);
            }
            catch(DataAccessException e){
                return new JoinGameResponse(StatusCode.BAD_REQUEST, "Error: bad request");
            }
            if(joinGameRequest.playerColor == null){
                return new JoinGameResponse(StatusCode.SUCCESS);
            }
            if(joinGameRequest.playerColor == PlayerColor.WHITE && game.whiteUsername == null){
                game.whiteUsername = username;
                GameDAO.updateGame(game);
                return new JoinGameResponse(StatusCode.SUCCESS);
            }
            else if(joinGameRequest.playerColor == PlayerColor.BLACK && game.blackUsername == null){
                game.blackUsername = username;
                GameDAO.updateGame(game);
                return new JoinGameResponse(StatusCode.SUCCESS);
            }
            else{
                return new JoinGameResponse(StatusCode.FORBIDDEN, "Error: already taken");
            }
        }
        catch(DataAccessException e){
            return new JoinGameResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
        }
    }
}
