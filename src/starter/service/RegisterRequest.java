package service;

/**
 * represents a RegisterRequest from the client
 * stores a username, password, and email
 */
public class RegisterRequest {
    /**
     * the new user's username
     */
    public String username;
    /**
     * the new user's password
     */
    public String password;
    /**
     * the new user's email address
     */
    public String email;

    public RegisterRequest(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
