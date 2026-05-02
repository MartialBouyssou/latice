package latice.model.deck;

import latice.model.exception.EmptyDeckException;
import latice.model.tile.Tile;
import latice.model.tile.TileFactory;

import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Tile> tiles;


    private Deck(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public static Deck create() {
        Deck deck = new Deck(TileFactory.createFullDeckTiles());
        deck.shuffle();
        return deck;
    }

    public static Deck create(List<Tile> tiles) {
        Deck deck = new Deck(tiles);
        deck.shuffle();
        return deck;
    }

    public int size() {
        return tiles.size();
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public Tile draw() {
        if (isEmpty()) {
            throw new EmptyDeckException();
        }

        return tiles.remove(tiles.size() - 1);
    }

    public void addTiles(Tile... tiles) {
        Collections.addAll(this.tiles, tiles);
    }

    public void shuffle() {
        Collections.shuffle(tiles);
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
