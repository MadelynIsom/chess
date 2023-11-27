package request_response;

/**
 * represents all the status codes the server can return
 */
public enum StatusCode {
    SUCCESS(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    INTERNAL_SERVER_ERROR(500);

    public final int code;
    StatusCode(int code) {
        this.code = code;
    }

    public static StatusCode fromInteger(int x) {
        switch(x) {
            case 200:
                return SUCCESS;
            case 400:
                return BAD_REQUEST;
            case 401:
                return UNAUTHORIZED;
            case 403:
                return FORBIDDEN;
            case 500:
                return INTERNAL_SERVER_ERROR;
        }
        return null;
    }
}
