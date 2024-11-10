package exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(String message, Exception e) {
        super(message, e);
    }
}
