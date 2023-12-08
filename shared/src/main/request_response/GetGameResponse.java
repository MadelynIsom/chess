package request_response;

import model.Game;

import java.util.ArrayList;

public class GetGameResponse {
    public Game game;

    /**
     * the status code indicating success or failure
     */
    public StatusCode statusCode;
    /**
     * the error message if operation fails
     */
    public String errorMessage;

    /**
     * constructor for a successful operation
     * @param game game
     */
    public GetGameResponse(Game game){
        this.game = game;
        this.statusCode = StatusCode.SUCCESS;
        this.errorMessage = null;
    }
    /**
     * constructor for an unsuccessful operation
     * @param statusCode error code
     * @param errorMessage error message
     */
    public GetGameResponse(StatusCode statusCode, String errorMessage){
        this.game = null;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
