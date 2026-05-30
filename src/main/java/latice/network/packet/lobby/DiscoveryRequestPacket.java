package latice.network.packet.lobby;

import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataOutputStream;
import java.io.IOException;

public class DiscoveryRequestPacket implements Packet {
    public DiscoveryRequestPacket() {
        /* It's just an informational packet saying "HEY" to everyone */
    }

    @Override
    public PacketType getType() {
        return PacketType.DISCOVERY_REQUEST;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
    }

    public static DiscoveryRequestPacket readFrom() {
        return new DiscoveryRequestPacket();
    }
}
