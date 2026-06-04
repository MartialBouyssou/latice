package latice.network.packet.lobby;

import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LobbyJoinPacket implements Packet {
    private final String clientName;

    public LobbyJoinPacket(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public PacketType getType() {
        return PacketType.LOBBY_JOIN;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
        out.writeUTF(clientName);
    }

    public static LobbyJoinPacket readFrom(DataInputStream in) throws IOException {
        String clientName = in.readUTF();
        return new LobbyJoinPacket(clientName);
    }
}
