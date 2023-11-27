package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import request_response.RegisterRequest;
import request_response.RegisterResponse;
import request_response.StatusCode;

import java.util.UUID;

/**
 * registers a user
 */
public class Register {
    /**
     * registers a user in the database
     * @param request object from the client containing a username, password, and email
     * @return response object containing the authToken (if successful), a status code, and an error message (if one is thrown)
     */
    public static RegisterResponse register(RegisterRequest request) {
        //create token
        UUID uuid = UUID.randomUUID();
        //create User and AuthToken model with request object
        if(request.username == null || request.password == null || request.email == null){
            return new RegisterResponse(StatusCode.BAD_REQUEST, "Error: bad request");
        }
        model.User user = new model.User(request.username, request.password, request.email);
        model.AuthToken token = new model.AuthToken(request.username, uuid.toString());
        //add user and auth models to database
        try {
            UserDAO.createUser(user);
            AuthDAO.createToken(token);
        } catch (DataAccessException e) {
            return new RegisterResponse(StatusCode.FORBIDDEN, "Error: already taken");
        }
        //create the response and return
        return new RegisterResponse(token);
    }


}
