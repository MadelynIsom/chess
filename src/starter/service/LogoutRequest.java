package service;
/**
 * represents a logout request from the client
 * user hands back the authToken to be deleted
 */
public class LogoutRequest {
    /**
     * the authToken to terminate
     */
    public String authToken;

    public LogoutRequest(String authToken){
        this.authToken = authToken;
    }
}
