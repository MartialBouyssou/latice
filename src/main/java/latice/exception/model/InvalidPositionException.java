package latice.exception.model;

public class InvalidPositionException extends RuntimeException {

    /**
     * Creates the exception with the default message indicating that the position is out of bounds.
     */
    public InvalidPositionException() {
        super("Position is out of bounds.");
    }

    /**
     * Creates the exception with a custom message.
     * @param message The detail message describing the error.
     */
    public InvalidPositionException(String message) {
        super(message);
    }
}
