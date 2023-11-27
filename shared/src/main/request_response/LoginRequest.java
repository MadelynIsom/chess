package request_response;
/**
 * represents a login request from the client
 * stores a username and password
 */
public class LoginRequest {
    /**
     * the user's username
     */
    public String username;
    /**
     * the user's password
     */
    public String password;

    public LoginRequest(String username, String password){
        this.username = username;
        this.password = password;
    }
}
