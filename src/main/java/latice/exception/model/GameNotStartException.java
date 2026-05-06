package latice.exception.model;

public class GameNotStartException extends RuntimeException {
    public GameNotStartException() {
        super("The game haven't started yet, you can't do that.");
    }

    public GameNotStartException(String message) {
        super(message);
    }
}
