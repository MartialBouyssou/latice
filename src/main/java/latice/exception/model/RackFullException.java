package latice.exception.model;

public class RackFullException extends RuntimeException {

    /**
     * Creates the exception with the default message indicating that the rack is already full.
     */
    public RackFullException() {
        super("Rack is already full.");
    }

    /**
     * Creates the exception with a custom message.
     * @param message The detail message describing the error.
     */
    public RackFullException(String message) {
        super(message);
    }
}
