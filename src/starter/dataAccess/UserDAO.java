package dataAccess;

import model.User;
import java.sql.Connection;
import java.sql.SQLException;

import static dataAccess.Database.DB;

/**
 * represents a User Data Access Object
 * provides CRUD operations for User models in the database
 */
public class UserDAO {
    //public static HashMap<String, User> userTable = new HashMap<>(); //Local memory storage
    /**
     * creates a new user in the database
     * @param user model to add
     * @throws DataAccessException if a user with that username already exists
     */

    /* //Memory storage
    public static void createUser(model.User user) throws DataAccessException {
        if(userTable.get(user.username) != null){
            throw new DataAccessException("Error: already taken");
        }
        userTable.put(user.username, user);
    }
     */

    public static void createUser(model.User user) throws DataAccessException {
        if(queryUser(user.username) == null){
            createUser(user.username, user.password, user.email); //Database Access
        }
        else{
            throw new DataAccessException("Error: already taken");
        }
    }

    private static void createUser(String username, String password, String email) throws DataAccessException {
        Connection conn = DB.getConnection();
        try (var preparedStatement = conn.prepareStatement("INSERT INTO userTable (username, password, email) VALUES(?, ?, ?)")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);

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
     * gets a user from the database
     * @param username username of the user to get
     * @return model of the user
     * @throws DataAccessException if the user doesn't exist
     */
    /*
    public static model.User getUser(String username) throws DataAccessException {
        if(userTable.get(username) == null){
            throw new DataAccessException("Error: bad request");
        }
        return userTable.get(username);
    }

     */
    public static model.User getUser(String username) throws DataAccessException {
        model.User userModel = queryUser(username);
        if(userModel != null){
            return userModel;
        }
        else throw new DataAccessException("Error: already taken");
    }

    private static model.User queryUser(String username) throws DataAccessException {
        Connection conn = DB.getConnection();
        try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM userTable WHERE username=?")) {
            preparedStatement.setString(1, username);
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) { // moves to next row in the result set; returns false if there are no more items in the set
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    return new User(username, password, email);
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

    public static void clear() throws DataAccessException{
        //userTable.clear(); //Memory storage
        DB.truncateTable("userTable");
    }

}
