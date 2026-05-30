package latice.model.player;

import latice.model.deck.Deck;
import latice.exception.model.EmptyDeckException;
import latice.exception.model.RackFullException;
import latice.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class Rack {
    public static final int MAX_SIZE = 5;

    private final List<Tile> tiles;

    public Rack(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public Rack() {
        this(new ArrayList<>());
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void addTile(Tile tile) {
        if (tiles.size() >= MAX_SIZE) {
            throw new RackFullException();
        }
        this.tiles.add(tile);
    }

    public void removeTile(Tile... tiles) {
        for (Tile tile : tiles) {
            this.tiles.remove(tile);
        }
    }

    public int size() {
        return tiles.size();
    }

    public void refill(Deck deck) {
        while (tiles.size() < MAX_SIZE && !deck.isEmpty()) {
            addTile(deck.draw());
        }
    }

    public void exchangeAll(Deck deck) {
        if (deck.isEmpty()) {
            throw new EmptyDeckException("Cannot exchange with empty deck");
        }

        for (Tile tile : tiles) {
            deck.addTiles(tile);
        }

        deck.shuffle();
        tiles.clear();
        refill(deck);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Tile tile : tiles) {
            sb.append("\n   - ").append(tile);
        }
        return sb.toString();
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
