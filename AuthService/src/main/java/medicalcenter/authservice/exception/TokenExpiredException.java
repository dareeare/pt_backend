package medicalcenter.authservice.exception;

import java.io.Serial;

public class TokenExpiredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8100857986257235332L;

    public TokenExpiredException() {
    }

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenExpiredException(Throwable cause) {
        super(cause);
    }
}
