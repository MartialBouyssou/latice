package latice.network.packet.lobby;

import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DiscoveryResponsePacket implements Packet {
    private final String lobbyName;
    private final int tcpPort;

    public DiscoveryResponsePacket(String lobbyName, int tcpPort) {
        this.lobbyName = lobbyName;
        this.tcpPort = tcpPort;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    @Override
    public PacketType getType() {
        return PacketType.DISCOVERY_RESPONSE;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
        out.writeUTF(lobbyName);
        out.writeInt(tcpPort);
    }

    public static DiscoveryResponsePacket readFrom(DataInputStream in) throws IOException {
        String host = in.readUTF();
        int port = in.readInt();

        return new DiscoveryResponsePacket(host, port);
    }
}
