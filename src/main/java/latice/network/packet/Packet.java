package latice.network.packet;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {
    PacketType getType();
    void writeTo(DataOutputStream out) throws IOException;
}
