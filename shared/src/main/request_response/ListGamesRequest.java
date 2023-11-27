package request_response;

/**
 * represents a request from the client to list all games
 * user needs be authenticated to list games
 */
public class ListGamesRequest {
    /**
     * the authToken to authenticate the request
     */
    public String authToken;

    public ListGamesRequest(String authToken){
        this.authToken = authToken;
    }
}
