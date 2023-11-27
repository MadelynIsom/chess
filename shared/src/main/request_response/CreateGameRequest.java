package request_response;
/**
 * represents a request to create a new game from the client
 * simply stores what the user wants the game to be called
 */
public class CreateGameRequest {
    /**
     * the authToken to authenticate the request
     */
    public String authToken;
    /**
     * the name the user wants the game to be called
     */
    public String gameName;

    public CreateGameRequest(String authToken, String gameName){
        this.gameName = gameName;
        this.authToken = authToken;
    }
}
