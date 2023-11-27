package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import request_response.LogoutRequest;
import request_response.LogoutResponse;
import request_response.StatusCode;

/**
 * logs out a user
 */
public class Logout {
    /**
     * logs out the user by deleting their authToken and effectively ending their session
     * @param request object from the client containing their authToken
     * @return response object simply containing a status code enum and an error message (if one was thrown)
     */
    public static LogoutResponse logout(LogoutRequest request){
        try{
            if(AuthDAO.getToken(request.authToken) != null){
                AuthDAO.deleteToken(request.authToken);
                return new LogoutResponse(StatusCode.SUCCESS);
            }
            else{
                return new LogoutResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
            }
        }
        catch(DataAccessException e){
            return new LogoutResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
        }
    }
}
