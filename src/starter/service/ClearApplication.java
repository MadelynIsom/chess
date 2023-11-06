package service;

import dataAccess.*;

/**
 * completely wipes all model objects stored in the database
 */
public class ClearApplication {
    /**
     * clears the database
     */
    public static ClearApplicationResponse clearDatabase(){
        try{
            AuthDAO.clear();
            UserDAO.clear();
            GameDAO.clear();
        }
        catch(DataAccessException e){
            return new ClearApplicationResponse(StatusCode.INTERNAL_SERVER_ERROR, "Error: failed to clear database");
        }
        return new ClearApplicationResponse(StatusCode.SUCCESS);

        /* //Memory Storage
            if(!AuthDAO.authTokenTable.isEmpty() || !UserDAO.userTable.isEmpty() || !GameDAO.gameTable.isEmpty()){
                return new ClearApplicationResponse(StatusCode.INTERNAL_SERVER_ERROR, "Error: failed to clear database");
            }
            else{
                return new ClearApplicationResponse(StatusCode.SUCCESS);
            }

         */
    }
}
