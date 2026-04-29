package latice.model.exception;

public class RackFullException extends RuntimeException {

    public RackFullException() {
        super("Rack is already full.");
    }

    public RackFullException(String message) {
        super(message);
    }
}
