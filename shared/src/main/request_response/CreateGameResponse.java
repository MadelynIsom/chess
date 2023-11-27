package request_response;

/**
 * represents a CreateGameResponse from the server
 * stores a game model (if successful), a statusCode enum, and an errorMessage (if an error was thrown)
 */

public class CreateGameResponse {
    /**
     * the model object for the game
     */
    public Integer gameID;
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
     * @param gameID new game created
     */
    public CreateGameResponse(Integer gameID){
        this.gameID = gameID;
        this.statusCode = StatusCode.SUCCESS;
    }

    /**
     * constructor for an unsuccessful operation
     * @param statusCode error code
     * @param errorMessage error message
     */
    public CreateGameResponse(StatusCode statusCode, String errorMessage){
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
