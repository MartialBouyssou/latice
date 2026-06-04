package latice.network.packet.game;

import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataOutputStream;
import java.io.IOException;

public class PassTurnPacket implements Packet {

    public PassTurnPacket() {
        /* Empty constructor bc there is no data to receive or comm */
    }

    @Override
    public PacketType getType() {
        return PacketType.PASS_TURN;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
    }

    public static PassTurnPacket readFrom() {
        return new PassTurnPacket();
    }
}
