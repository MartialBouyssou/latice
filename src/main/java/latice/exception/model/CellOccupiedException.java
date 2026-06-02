package latice.exception.model;

public class CellOccupiedException extends RuntimeException {

    /**
     * Creates the exception with the default message indicating that the cell is occupied.
     */
    public CellOccupiedException() {
        super("Cannot place tile on an occupied cell.");
    }

    /**
     * Creates the exception with a custom message.
     * @param message The detail message describing the error.
     */
    public CellOccupiedException(String message) {
        super(message);
    }
}
