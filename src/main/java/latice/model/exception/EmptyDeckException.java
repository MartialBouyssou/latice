package latice.model.exception;

public class EmptyDeckException extends RuntimeException {

    public EmptyDeckException() {
        super("Cannot draw from an empty deck.");
    }

    public EmptyDeckException(String message) {
        super(message);
    }
}
