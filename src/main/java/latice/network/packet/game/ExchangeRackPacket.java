package latice.network.packet.game;

import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import latice.network.packet.Packet;
import latice.network.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRackPacket implements Packet {
    private final List<Tile> newRack;
    private final List<Tile> remainingDeck;

    public ExchangeRackPacket(List<Tile> newRack, List<Tile> remainingDeck) {
        this.newRack = newRack;
        this.remainingDeck = remainingDeck;
    }

    public List<Tile> getNewRack() { return newRack; }
    public List<Tile> getRemainingDeck() { return remainingDeck; }

    @Override
    public PacketType getType() { return PacketType.EXCHANGE; }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
        writeTiles(out, newRack);
        writeTiles(out, remainingDeck);
    }

    private void writeTiles(DataOutputStream out, List<Tile> tiles) throws IOException {
        out.writeInt(tiles.size());
        for (Tile tile : tiles) {
            out.writeUTF(tile.getColor().name());
            out.writeUTF(tile.getShape().name());
        }
    }

    public static ExchangeRackPacket readFrom(DataInputStream in) throws IOException {
        List<Tile> rack = readTiles(in);
        List<Tile> deck = readTiles(in);
        return new ExchangeRackPacket(rack, deck);
    }

    private static List<Tile> readTiles(DataInputStream in) throws IOException {
        int size = in.readInt();
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Color color = Color.valueOf(in.readUTF());
            Shape shape = Shape.valueOf(in.readUTF());
            tiles.add(new Tile(shape, color));
        }
        return tiles;
    }
}
