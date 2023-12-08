package request_response;

import model.AuthToken;

public class GetAuthTokenResponse {

    public AuthToken authToken;
    /**
     * the status code to indicate success or failure
     */
    public StatusCode statusCode;
    /**
     * the error message if operation fails
     */
    public String errorMessage;

    public GetAuthTokenResponse(AuthToken authToken){
        this.authToken = authToken;
        this.statusCode = StatusCode.SUCCESS;
        this.errorMessage = null;
    }
    public GetAuthTokenResponse(StatusCode statusCode, String errorMessage){
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
