package latice.exception.network;

public class PacketDeserializationException extends RuntimeException {
    public PacketDeserializationException(String message) {
        super(message);
    }
}
