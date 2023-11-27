package request_response;
/**
 * represents a LogoutResponse from the server
 * stores a statusCode enum and an errorMessage (if an error was thrown)
 */
public class LogoutResponse {
    /**
     * the status code indicating success or failure
     */
    public StatusCode statusCode;
    /**
     * the error message if operation fails
     */
    public String errorMessage;

    public LogoutResponse(StatusCode statusCode){
        this.statusCode = statusCode;
        this.errorMessage = null;
    }
    public LogoutResponse(StatusCode statusCode, String errorMessage){
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
