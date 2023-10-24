package dataAccess;

import model.AuthToken;

import java.util.HashMap;

/**
 * represents an AuthToken Data Access Object
 * provides CRUD operations for AuthToken models in the database
 */

public class AuthDAO {
    public static HashMap<String, AuthToken> authTokenTable = new HashMap<>();
    /**
     * creates a new token in the database
     * @param token token model to add
     * @throws DataAccessException if the token already exists
     */
    public static void createToken(model.AuthToken token) throws DataAccessException {
        if(authTokenTable.get(token.authToken) != null){
            throw new DataAccessException("Error: already taken");
        }
        authTokenTable.put(token.authToken, token);
    }

    /**
     * gets authToken model given the authToken string
     * @param authToken string representation of the authToken to get
     * @return the model of the AuthToken
     * @throws DataAccessException if the authToken doesn't exist
     */
    public static model.AuthToken getToken(String authToken) throws DataAccessException {
        if(authTokenTable.get(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        return authTokenTable.get(authToken);
    }

    /**
     * deletes a token from the database
     * @param authToken token to delete
     * @throws DataAccessException if the token doesn't exist
     */
    public static void deleteToken(String authToken) throws DataAccessException {
        if(authTokenTable.get(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        authTokenTable.remove(authToken);
    }

    public static void clear(){
        authTokenTable.clear();
    }


}
