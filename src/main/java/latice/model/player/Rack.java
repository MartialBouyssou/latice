package latice.model.player;

import latice.model.deck.Deck;
import latice.model.exception.EmptyDeckException;
import latice.model.exception.RackFullException;
import latice.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class Rack {
    private final List<Tile> tiles;

    private Rack(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public static Rack create() {
        return new Rack(new ArrayList<>());
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void addTile(Tile tile) {
        if (tiles.size() >= 5) {
            throw new RackFullException();
        }
        this.tiles.add(tile);
    }

    public void removeTile(Tile... tiles) {
        for (Tile tile: tiles)
            this.tiles.remove(tile);
    }

    public void refill(Deck deck) {
        while (tiles.size() < 5 && !deck.isEmpty())
            addTile(deck.draw());
    }

    public void exchangeAll(Deck deck) {
        if (deck.isEmpty()) {
            throw new EmptyDeckException("Cannot exchange with empty deck");
        }

        for (Tile tile: tiles)
            deck.addTiles(tile);

        deck.shuffle();
        tiles.clear();
        refill(deck);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Tile tile : tiles) {
            sb.append("   - ").append(tile);
        }

        return sb.toString();
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
