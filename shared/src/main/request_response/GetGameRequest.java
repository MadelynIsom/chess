package request_response;

public class GetGameRequest {
    public String authToken;
    public int gameID;

    public GetGameRequest(String authToken, int gameID){
        this.authToken = authToken;
        this.gameID = gameID;
    }

}
