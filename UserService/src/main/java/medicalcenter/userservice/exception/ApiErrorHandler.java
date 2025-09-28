package medicalcenter.userservice.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class ApiErrorHandler {
    private static final String ERROR = "Error";
    private static final String TIMESTAMP = "Timestamp";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errors.put(error.getField(), error.getDefaultMessage());
                });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UpdateException.class)
    public ResponseEntity<Map<String, String>> handleUpdateException(UpdateException ex) {
        log.error(ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put(ERROR, ex.getMessage());
        error.put(TIMESTAMP, LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(error);
    }
}
