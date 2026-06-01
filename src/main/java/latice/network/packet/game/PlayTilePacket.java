package latice.network.packet.game;

import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayTilePacket implements Packet {
    private final Tile tile;
    private final int x;
    private final int y;

    public PlayTilePacket(Tile tile, int x, int y) {
        this.tile = tile;
        this.x = x;
        this.y = y;
    }

    public Tile getTile() {
        return tile;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public PacketType getType() {
        return PacketType.TILE_PLACED;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
        out.writeUTF(tile.getColor().name());
        out.writeUTF(tile.getShape().name());
        out.writeInt(x);
        out.writeInt(y);
    }

    public static PlayTilePacket readFrom(DataInputStream in) throws IOException {
        Color color = Color.valueOf(in.readUTF());
        Shape shape = Shape.valueOf(in.readUTF());

        Tile tile = new Tile(shape, color);
        int x = in.readInt();
        int y = in.readInt();

        return new PlayTilePacket(tile, x, y);
    }
}
