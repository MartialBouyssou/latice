package latice.exception.model;

public class NoTilePlayedException extends RuntimeException {
    /**
     * Creates the exception with the default message indicating that no tile has been played this turn.
     */
    public NoTilePlayedException() {
        super("Cannot end turn: no tile has been played this turn");
    }

    /**
     * Creates the exception with a custom message.
     * @param message The detail message describing the error.
     */
    public NoTilePlayedException(String message) {
        super(message);
    }
}
