package latice.model.deck;

import latice.exception.model.EmptyDeckException;
import latice.model.tile.Tile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void drawShouldReduceSize() {
        Deck deck = Deck.create();
        int initialSize = deck.size();

        deck.draw();

        assertEquals(initialSize - 1, deck.size());
    }

    @Test
    void drawShouldThrowExceptionIfEmpty() {
        Deck deck = Deck.create(new ArrayList<>());

        // deck::draw is an executable method here exe by assertThrowsExactly (object::method)
        assertThrowsExactly(EmptyDeckException.class, deck::draw);
    }
}