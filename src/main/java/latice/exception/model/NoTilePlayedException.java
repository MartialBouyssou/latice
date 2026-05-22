package latice.exception.model;

public class NoTilePlayedException extends RuntimeException {
    public NoTilePlayedException() {
        super("Cannot end turn: no tile has been played this turn");
    }

    public NoTilePlayedException(String message) {
        super(message);
    }
}
