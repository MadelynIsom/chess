package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

/**
 * completely wipes all model objects stored in the database
 */
public class ClearApplication {
    /**
     * clears the database
     */
    public static ClearApplicationResponse clearDatabase(){
            AuthDAO.clear();
            UserDAO.clear();
            GameDAO.clear();
            if(!AuthDAO.authTokenTable.isEmpty() || !UserDAO.userTable.isEmpty() || !GameDAO.gameTable.isEmpty()){
                return new ClearApplicationResponse(StatusCode.INTERNAL_SERVER_ERROR, "Error: failed to clear database");
            }
            else{
                return new ClearApplicationResponse(StatusCode.SUCCESS);
            }

    }
}
