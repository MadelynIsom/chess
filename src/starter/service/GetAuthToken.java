package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.AuthToken;
import model.Game;
import request_response.GetAuthTokenRequest;
import request_response.GetAuthTokenResponse;
import request_response.GetGameResponse;
import request_response.StatusCode;

public class GetAuthToken {
    public static GetAuthTokenResponse getAuthToken(GetAuthTokenRequest request){
        try{
            AuthToken authToken = AuthDAO.getToken(request.authToken);
            return new GetAuthTokenResponse(authToken);
        }
        catch(DataAccessException e){
            return new GetAuthTokenResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
        }
    }
}
