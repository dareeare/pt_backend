package medicalcenter.userservice.exception;

import java.util.UUID;

public class UpdateException extends RuntimeException {
    private static final String MESSAGE_TEMP = "Update failed for entity with id '%s'";
    public UpdateException() {
    }

    public UpdateException(String message) {
        super(message);
    }

    public UpdateException(UUID id) {
        super(MESSAGE_TEMP.formatted(id));
    }

    public UpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateException(Throwable cause) {
        super(cause);
    }
}
