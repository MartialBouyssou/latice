package latice.exception.network;

public class ConnectionRefusedException extends RuntimeException {
    public ConnectionRefusedException(String message) {
        super(message);
    }
}
