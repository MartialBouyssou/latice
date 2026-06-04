package latice.model.player;

import latice.model.deck.Deck;
import latice.exception.model.EmptyDeckException;
import latice.exception.model.RackFullException;
import latice.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class Rack {
    public static final int MAX_SIZE = 5;

    private List<Tile> tiles;

    /**
     * Creates a rack backed by the given list of tiles.
     * @param tiles The list of tiles to use as the rack's contents.
     */
    public Rack(List<Tile> tiles) {
        this.tiles = new ArrayList<>(tiles);
    }

    /**
     * Creates an empty rack with no tiles.
     */
    public Rack() {
        this(new ArrayList<>());
    }

    /**
     * Returns the list of tiles currently in the rack.
     * @return The tiles held in the rack.
     */
    public List<Tile> getTiles() {
        return tiles;
    }

    /**
     * Set the list of tiles currently in the rack.
     * @param tiles ArrayList to set to the rack.
     */
    public void setTiles(List<Tile> tiles) {
        this.tiles = new ArrayList<>(tiles);
    }

    /**
     * Adds a tile to the rack if it is not already full.
     * @param tile The tile to add to the rack.
     */
    public void addTile(Tile tile) {
        if (tiles.size() >= MAX_SIZE) throw new RackFullException();
        tiles.add(tile);
    }

    /**
     * Removes the given tiles from the rack.
     * @param tiles The tiles to remove from the rack.
     */
    public void removeTile(Tile... tiles) {
        for (Tile tile : tiles) this.tiles.remove(tile);
    }

    /**
     * Returns the number of tiles currently in the rack.
     * @return The number of tiles in the rack.
     */
    public int size() {
        return tiles.size();
    }

    /**
     * Refills the rack from the deck until it is full or the deck is empty.
     * @param deck The deck to draw tiles from.
     */
    public void refill(Deck deck) {
        while (tiles.size() < MAX_SIZE && !deck.isEmpty()) {
            addTile(deck.draw());
        }
    }

    /**
     * Returns all rack tiles to the deck, shuffles it and refills the rack.
     * @param deck The deck to exchange tiles with.
     */
    public void exchangeAll(Deck deck) {
        if (deck.isEmpty()) throw new EmptyDeckException("Cannot exchange with empty deck");
        for (Tile tile : tiles) deck.addTiles(tile);
        deck.shuffle();
        tiles.clear();
        refill(deck);
    }

    /**
     * Checks whether the rack contains no tiles.
     * @return True if the rack is empty, false otherwise.
     */
    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    /**
     * Returns a textual representation listing the tiles in the rack.
     * @return A string describing the rack's contents.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Tile tile : tiles) sb.append("\n   - ").append(tile);
        return sb.toString();
    }
}
