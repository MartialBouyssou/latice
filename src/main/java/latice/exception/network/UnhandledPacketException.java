package latice.exception.network;

public class UnhandledPacketException extends RuntimeException {
    public UnhandledPacketException(String message) {
        super(message);
    }
}
