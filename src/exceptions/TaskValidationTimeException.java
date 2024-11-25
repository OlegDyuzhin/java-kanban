package exceptions;

public class TaskValidationTimeException extends RuntimeException {
    public TaskValidationTimeException(String message) {
        super(message);
    }

    public TaskValidationTimeException(String message, Exception e) {
        super(message, e);
    }
}
