package latice.exception.model;

public class EmptyDeckException extends RuntimeException {

    /**
     * Creates the exception with the default message indicating that the deck is empty.
     */
    public EmptyDeckException() {
        super("Cannot draw from an empty deck.");
    }

    /**
     * Creates the exception with a custom message.
     * @param message The detail message describing the error.
     */
    public EmptyDeckException(String message) {
        super(message);
    }
}
