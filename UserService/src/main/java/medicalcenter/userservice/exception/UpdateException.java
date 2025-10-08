package medicalcenter.userservice.exception;

import java.io.Serial;
import java.util.UUID;

public class UpdateException extends RuntimeException {
    private static final String MESSAGE_TEMP = "Update failed for entity with id '%s'";
    @Serial
    private static final long serialVersionUID = 6727624372533947984L;

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
