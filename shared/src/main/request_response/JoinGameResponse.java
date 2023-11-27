package request_response;

/**
 * represents a JoinGameResponse from the server
 * stores a statusCode enum and an errorMessage (if an error was thrown)
 */

public class JoinGameResponse {
    /**
     * the status code to indicate success or failure
     */
    public StatusCode statusCode;
    /**
     * the error message if operation fails
     */
    public String errorMessage;

    public JoinGameResponse(StatusCode statusCode){
        this.statusCode = statusCode;
        this.errorMessage = null;
    }
    public JoinGameResponse(StatusCode statusCode, String errorMessage){
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
