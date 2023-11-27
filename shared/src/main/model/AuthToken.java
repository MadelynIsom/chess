package model;

/**
 * represents a model object of an AuthToken
 * stores a username and an authToken
 */

public class AuthToken {
    /**
     * the user's username
     */
    public String username;
    /**
     * the authToken for the user's current session
     */
    public String authToken;

    public AuthToken(String username, String authToken){
        this.username = username;
        this.authToken = authToken;
    }
}
