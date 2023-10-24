package service;

public class ClearApplicationResponse {
    public StatusCode statusCode;
    public String errorMessage;

    public ClearApplicationResponse(StatusCode statusCode, String errorMessage){
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public ClearApplicationResponse(StatusCode statusCode){
        this.statusCode = statusCode;
        this.errorMessage = null;
    }
}
