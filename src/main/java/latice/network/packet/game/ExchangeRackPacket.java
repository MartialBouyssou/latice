package latice.network.packet.game;

import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataOutputStream;
import java.io.IOException;

public class ExchangeRackPacket implements Packet {

    public ExchangeRackPacket() {
        /* So let me explain why I let it empty : as we all know the draw() method isn't random
        and both deck (in client and server computer) will be the same so we just have to listen
        that there is a rack exchange to draw in both side the same cards */
    }

    @Override
    public PacketType getType() {
        return PacketType.EXCHANGE;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
    }

    public static Packet readFrom() {
        return new ExchangeRackPacket();
    }
}
