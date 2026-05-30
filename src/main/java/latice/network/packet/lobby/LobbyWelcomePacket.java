package latice.network.packet.lobby;

import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LobbyWelcomePacket implements Packet {
    private final String hostName;

    public LobbyWelcomePacket(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return hostName;
    }

    @Override
    public PacketType getType() {
        return PacketType.LOBBY_WELCOME;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
        out.writeUTF(hostName);
    }

    public static LobbyWelcomePacket readFrom(DataInputStream in) throws IOException {
        String hostName = in.readUTF();
        return new LobbyWelcomePacket(hostName);
    }
}
