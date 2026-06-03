package latice.network.packet.lobby;

import latice.network.packet.Packet;
import latice.network.packet.PacketType;
import latice.network.packet.game.PassTurnPacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class LobbyReadyPacket implements Packet {

    public LobbyReadyPacket() { /* It's just an informational packet so nothing to say... */ }

    @Override
    public PacketType getType() {
        return PacketType.LOBBY_READY;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
    }

    public static LobbyReadyPacket readFrom() {
        return new LobbyReadyPacket();
    }
}
