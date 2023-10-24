package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthToken;
import java.util.UUID;

/**
 * logs in a user
 */
public class Login {
    /**
     * logs a user into the database and starts a session
     * @param request object from the client containing a username and password
     * @return response object containing the authToken (if successful), a status code, and an error message (if one is thrown)
     */
    public static LoginResponse login(LoginRequest request) {
        try{
            if(UserDAO.getUser(request.username) != null){
                if(request.password.equals(UserDAO.getUser(request.username).password)){
                    UUID uuid = UUID.randomUUID();
                    model.AuthToken authToken = new AuthToken(request.username, uuid.toString());
                    AuthDAO.createToken(authToken);
                    return new LoginResponse(authToken);
                }
                else{
                    return new LoginResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
                }
            }
            else{
                return new LoginResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
            }
        } catch(DataAccessException e) {
            return new LoginResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
        }
    }
}
