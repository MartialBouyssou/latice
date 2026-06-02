package latice.model.deck;

import latice.exception.model.EmptyDeckException;
import latice.model.tile.Tile;
import latice.model.tile.TileFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Tile> tiles;


    /**
     * Creates a deck from a copy of the given list of tiles.
     * @param tiles The tiles used to initialize the deck.
     */
    private Deck(List<Tile> tiles) {
        this.tiles = new ArrayList<>(tiles);
    }

    /**
     * Creates a new shuffled deck containing the full set of game tiles.
     * @return A new shuffled deck with all game tiles.
     */
    public static Deck create() {
        Deck deck = new Deck(TileFactory.createFullDeckTiles());
        deck.shuffle();
        return deck;
    }

    /**
     * Creates a new shuffled deck from the given list of tiles.
     * @param tiles The tiles used to build the deck.
     * @return A new shuffled deck containing the given tiles.
     */
    public static Deck create(List<Tile> tiles) {
        Deck deck = new Deck((tiles));
        deck.shuffle();
        return deck;
    }

    /**
     * Returns the number of tiles currently in the deck.
     * @return The number of tiles in the deck.
     */
    public int size() {
        return tiles.size();
    }

    /**
     * Returns the list of tiles contained in the deck.
     * @return The list of tiles in the deck.
     */
    public List<Tile> getTiles() {
        return tiles;
    }

    /**
     * Removes and returns the top tile of the deck.
     * @return The tile drawn from the top of the deck.
     */
    public Tile draw() {
        if (isEmpty()) {
            throw new EmptyDeckException();
        }

        return tiles.remove(tiles.size() - 1);
    }

    /**
     * Adds the given tiles to the deck.
     * @param tiles The tiles to add to the deck.
     */
    public void addTiles(Tile... tiles) {
        Collections.addAll(this.tiles, tiles);
    }

    /**
     * Randomly shuffles the tiles in the deck.
     */
    public void shuffle() {
        Collections.shuffle(tiles);
    }

    /**
     * Checks whether the deck has no tiles left.
     * @return True if the deck is empty, false otherwise.
     */
    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
