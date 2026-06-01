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

public class GameStartPacket implements Packet {
    private final String starterUserName;
    private final String secondUserName;
    private final List<Tile> starterDeck;
    private final List<Tile> secondDeck;

    public GameStartPacket(String starterUserName, String secondUserName,
                           List<Tile> starterDeck, List<Tile> secondDeck) {
        this.starterUserName = starterUserName;
        this.secondUserName = secondUserName;
        this.starterDeck = starterDeck;
        this.secondDeck = secondDeck;
    }

    public String getStarterUserName() {
        return starterUserName;
    }
    public String getSecondUserName() {
        return secondUserName;
    }
    public List<Tile> getStarterDeck() {
        return starterDeck;
    }
    public List<Tile> getSecondDeck() {
        return secondDeck;
    }

    @Override
    public PacketType getType() { return PacketType.GAME_START; }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
        out.writeUTF(starterUserName);
        out.writeUTF(secondUserName);
        writeTiles(out, starterDeck);
        writeTiles(out, secondDeck);
    }

    private void writeTiles(DataOutputStream out, List<Tile> tiles) throws IOException {
        out.writeInt(tiles.size());
        for (Tile tile : tiles) {
            out.writeUTF(tile.getColor().name());
            out.writeUTF(tile.getShape().name());
        }
    }

    public static GameStartPacket readFrom(DataInputStream in) throws IOException {
        String starterName = in.readUTF();
        String secondName = in.readUTF();
        List<Tile> starterDeck = readTiles(in);
        List<Tile> secondDeck = readTiles(in);
        return new GameStartPacket(starterName, secondName, starterDeck, secondDeck);
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
