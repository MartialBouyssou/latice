package latice.network.packet.lobby;

import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DiscoveryRequestPacket implements Packet {
    private int protocolVersion;

    public DiscoveryRequestPacket(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public PacketType getType() {
        return PacketType.DISCOVERY_REQUEST;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
        out.writeInt(protocolVersion);
    }

    public static DiscoveryRequestPacket readFrom(DataInputStream in) throws IOException {
        int protocolVersion = in.readInt();
        return new DiscoveryRequestPacket(protocolVersion);
    }
}
