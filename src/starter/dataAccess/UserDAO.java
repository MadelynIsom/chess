package dataAccess;

import model.AuthToken;
import model.User;

import java.util.HashMap;

/**
 * represents a User Data Access Object
 * provides CRUD operations for User models in the database
 */
public class UserDAO {
    public static HashMap<String, User> userTable = new HashMap<>();
    /**
     * creates a new user in the database
     * @param user model to add
     * @throws DataAccessException if a user with that username already exists
     */
    public static void createUser(model.User user) throws DataAccessException {
        if(userTable.get(user.username) != null){
            throw new DataAccessException("Error: already taken");
        }
        userTable.put(user.username, user);
    }

    /**
     * gets a user from the database
     * @param username username of the user to get
     * @return model of the user
     * @throws DataAccessException if the user doesn't exist
     */
    public static model.User getUser(String username) throws DataAccessException {
        if(userTable.get(username) == null){
            throw new DataAccessException("Error: bad request");
        }
        return userTable.get(username);
    }

    public static void clear(){
        userTable.clear();
    }

}
