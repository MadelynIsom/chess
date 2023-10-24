package service;
/**
 * represents a LoginResponse from the server
 * stores an authToken model (if successful), a statusCode enum, and an errorMessage (if an error was thrown)
 */

public class LoginResponse {
    /**
     * the authToken to use during the user's session
     */
    public model.AuthToken authToken;
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
     * @param authToken authToken for the user
     */
    public LoginResponse(model.AuthToken authToken){
        this.authToken = authToken;
        this.statusCode = StatusCode.SUCCESS;
    }

    /**
     * constructor for an unsuccessful operation
     * @param statusCode error code
     * @param errorMessage error message
     */
    public LoginResponse(StatusCode statusCode, String errorMessage){
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
