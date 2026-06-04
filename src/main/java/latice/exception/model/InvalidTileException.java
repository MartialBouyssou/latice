package latice.exception.model;

public class InvalidTileException extends RuntimeException {

    /**
     * Creates the exception with the default message indicating that an invalid tile was provided.
     */
    public InvalidTileException() {
        super("Invalid tile provided.");
    }

    /**
     * Creates the exception with a custom message.
     * @param message The detail message describing the error.
     */
    public InvalidTileException(String message) {
        super(message);
    }
}
