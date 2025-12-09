package medicalcenter.userservice.exception;

public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s с ID %s не найден", entityName, id));
    }
    
    public EntityNotFoundException(String entityName, String fieldName, Object value) {
        super(String.format("%s с %s '%s' не найден", entityName, fieldName, value));
    }
}