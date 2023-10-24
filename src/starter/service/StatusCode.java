package service;

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
}
