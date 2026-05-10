package latice.exception.model;

public class InvalidPositionException extends RuntimeException {

    public InvalidPositionException() {
        super("Position is out of bounds.");
    }

    public InvalidPositionException(String message) {
        super(message);
    }
}
