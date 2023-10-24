package service;

import dataAccess.*;
import model.Game;
import java.util.ArrayList;

/**
 * lists all games
 */
public class ListGames {
    /**
     * lists all games stored in the database
     * @return response object containing the array of game models(if successful), a status code, and an error message (if one is thrown)
     */
    public static ListGamesResponse listGames(ListGamesRequest request){
        try{
            if(AuthDAO.getToken(request.authToken) != null){
                ArrayList<Game> games = GameDAO.listGames();
                return new ListGamesResponse(games);
            }
            else{
                return new ListGamesResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
            }
        }
        catch(DataAccessException e){
            return new ListGamesResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
        }
    }
}
