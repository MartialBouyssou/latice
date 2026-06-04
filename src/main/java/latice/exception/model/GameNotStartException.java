package latice.exception.model;

public class GameNotStartException extends RuntimeException {
    /**
     * Creates the exception with the default message indicating that the game has not started yet.
     */
    public GameNotStartException() {
        super("The game haven't started yet, you can't do that.");
    }

    /**
     * Creates the exception with a custom message.
     * @param message The detail message describing the error.
     */
    public GameNotStartException(String message) {
        super(message);
    }
}
