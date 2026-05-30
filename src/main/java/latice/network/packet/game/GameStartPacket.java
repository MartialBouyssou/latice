package latice.network.packet.game;

import latice.model.deck.Deck;
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
    private final String starterName;
    private final Deck clientDeck;

    public GameStartPacket(String starterName, Deck clientDeck) {
        this.starterName = starterName;
        this.clientDeck = clientDeck;
    }

    public String getStarterName() {
        return starterName;
    }

    public Deck getClientDeck() {
        return clientDeck;
    }

    @Override
    public PacketType getType() {
        return PacketType.GAME_START;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(getType().ordinal());
        out.writeUTF(starterName);

        out.writeInt(clientDeck.getTiles().size());
        for (Tile tile : clientDeck.getTiles()) {
            out.writeUTF(tile.getColor().name());
            out.writeUTF(tile.getShape().name());
        }
    }

    public static GameStartPacket readFrom(DataInputStream in) throws IOException {
        String starterName = in.readUTF();

        int size = in.readInt();
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Color color = Color.valueOf(in.readUTF());
            Shape shape = Shape.valueOf(in.readUTF());
            tiles.add(new Tile(shape, color));
        }

        return new GameStartPacket(starterName, Deck.createNoShuffle(tiles));
    }
}
