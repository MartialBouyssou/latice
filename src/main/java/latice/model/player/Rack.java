package latice.model.player;

import latice.model.deck.Deck;
import latice.model.exception.EmptyDeckException;
import latice.model.exception.RackFullException;
import latice.model.tile.Tile;

import java.util.Collections;
import java.util.List;

public class Rack {
    private List<Tile> tiles;

    public Rack() {
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

        // TODO : Vérifier s'il faut remettre les tiles dans le deck

        tiles.clear();
        refill(deck);
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
