package latice.exception.model;

public class CellOccupiedException extends RuntimeException {

    public CellOccupiedException() {
        super("Cannot place tile on an occupied cell.");
    }

    public CellOccupiedException(String message) {
        super(message);
    }
}
