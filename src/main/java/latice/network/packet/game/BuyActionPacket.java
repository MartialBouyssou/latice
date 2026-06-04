package latice.network.packet.game;

import latice.model.player.ActionType;
import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BuyActionPacket implements Packet {
    private final ActionType actionType;

    public BuyActionPacket(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public PacketType getType() {
        return PacketType.BUY_ACTION;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
        out.writeUTF(actionType.name());
    }

    public static BuyActionPacket readFrom(DataInputStream in) throws IOException {
        ActionType actionType = ActionType.valueOf(in.readUTF());
        return new BuyActionPacket(actionType);
    }
}
