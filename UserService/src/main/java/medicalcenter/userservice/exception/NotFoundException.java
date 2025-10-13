package medicalcenter.userservice.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.UUID;

@Log4j2
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity not found")
public class NotFoundException extends RuntimeException {
    private static final String MESSAGE_TEMP = "Entity with id '%s' not found";
    @Serial
    private static final long serialVersionUID = 8115288000624377027L;

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(UUID id) {
        super(MESSAGE_TEMP.formatted(id));
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
