package latice.exception.model;

public class InvalidTileException extends RuntimeException {

    public InvalidTileException() {
        super("Invalid tile provided.");
    }

    public InvalidTileException(String message) {
        super(message);
    }
}
