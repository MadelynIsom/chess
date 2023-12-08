package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.Game;
import request_response.*;

import java.util.ArrayList;

public class GetGame {
    public static GetGameResponse getGame(GetGameRequest request){
        try{
            if(AuthDAO.getToken(request.authToken) != null){
                Game game = GameDAO.getGame(request.gameID);
                return new GetGameResponse(game);
            }
            else{
                return new GetGameResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
            }
        }
        catch(DataAccessException e){
            return new GetGameResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
        }
    }
}
