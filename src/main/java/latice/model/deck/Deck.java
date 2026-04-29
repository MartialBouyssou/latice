package latice.model.deck;

import latice.model.exception.EmptyDeckException;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;

import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Tile> tiles;


    public Deck(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public Deck() {
        // TODO: peut être reprendre en init ou factory
        for (Color color: Color.values()) {
            for (Shape shape : Shape.values()) {
                addTiles(new Tile(shape, color),
                        new Tile(shape, color));

            }
        }
    }

    private void addTiles(Tile... tiles) {
        Collections.addAll(this.tiles, tiles);
    }

    public Tile draw() {
        if (isEmpty()) {
            throw new EmptyDeckException();
        }

        return tiles.remove(tiles.size() - 1);
    }

    public void shuffle() {
        Collections.shuffle(tiles);
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
