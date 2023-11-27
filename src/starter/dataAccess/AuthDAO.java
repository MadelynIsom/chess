package dataAccess;

import model.AuthToken;
import java.sql.Connection;
import java.sql.SQLException;

import static dataAccess.Database.DB;

/**
 * represents an AuthToken Data Access Object
 * provides CRUD operations for AuthToken models in the database
 */

public class AuthDAO {
    //public static HashMap<String, AuthToken> authTokenTable = new HashMap<>(); //Local memory storage
    /**
     * creates a new token in the database
     * @param token token model to add
     * @throws DataAccessException if the token already exists
     */
    /* //Memory Storage:
    public static void createToken(model.AuthToken token) throws DataAccessException {
        if(authTokenTable.get(token.authToken) != null){
            throw new DataAccessException("Error: already taken");
        }
        authTokenTable.put(token.authToken, token); //Memory Access
    }
    */

    public static void createToken(model.AuthToken token) throws DataAccessException {
        if(queryToken(token.authToken) == null){
            createToken(token.authToken, token.username); //Database Access
        }
        else{
            throw new DataAccessException("Error: already taken");
        }
    }

    private static void createToken(String token, String username) throws DataAccessException {
        Connection conn = DB.getConnection();
        try (var preparedStatement = conn.prepareStatement("INSERT INTO authTable (token, username) VALUES(?, ?)")) {
            preparedStatement.setString(1, token);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        finally {
            DB.closeConnection(conn);
        }
    }


    /**
     * gets authToken model given the authToken string
     * @param authToken string representation of the authToken to get
     * @return the model of the AuthToken
     * @throws DataAccessException if the authToken doesn't exist
     */

    /*  //Memory storage:
    public static model.AuthToken getToken(String authToken) throws DataAccessException {
        if(authTokenTable.get(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        return authTokenTable.get(authToken);
    }
    */

    //Database storage:
    public static model.AuthToken getToken(String authToken) throws DataAccessException {
        model.AuthToken token = queryToken(authToken);
        if(token != null){
            return token;
        }
        else{
            throw new DataAccessException("Error: unauthorized");
        }
    }

    private static model.AuthToken queryToken(String authToken) throws DataAccessException {
        Connection conn = DB.getConnection();
        try (var preparedStatement = conn.prepareStatement("SELECT token, username FROM authTable WHERE token=?")) {
            preparedStatement.setString(1, authToken);
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) { // moves to next row in the result set; returns false if there are no more items in the set
                    String token = rs.getString("token");
                    String username = rs.getString("username");
                    return new AuthToken(username, token);
                }
                else{
                    return null;
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        finally{
            DB.closeConnection(conn);
        }
    }

    /**
     * deletes a token from the database
     * @param authToken token to delete
     * @throws DataAccessException if the token doesn't exist
     */
    /* //Memory Storage:
    public static void deleteToken(String authToken) throws DataAccessException {
        if(authTokenTable.get(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        authTokenTable.remove(authToken);
    }
     */

    //Database storage:
    public static void deleteToken(String authToken) throws DataAccessException {
        if(queryToken(authToken) != null){
            removeToken(authToken);
        }
        else{
            throw new DataAccessException("Error: unauthorized");
        }
    }

    private static void removeToken(String authToken) throws DataAccessException {
        Connection conn = DB.getConnection();
        try (var preparedStatement = conn.prepareStatement("DELETE FROM authTable WHERE token=?")) {
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        finally{
            DB.closeConnection(conn);
        }
    }

    public static void clear() throws DataAccessException{
        //authTokenTable.clear(); //memory storage
        DB.truncateTable("authTable");
    }

}
