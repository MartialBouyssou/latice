package latice.model.deck;

import latice.exception.model.EmptyDeckException;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    private Tile birdRedTile;
    private Tile turtleGreenTile;

    @BeforeEach
    void setUp() {
        birdRedTile = new Tile(Shape.BIRD, Color.RED);
        turtleGreenTile = new Tile(Shape.TURTLE, Color.GREEN);
    }

    @Test
    void createShouldProduceADeckWith72Tiles() {
        // Arrange & act
        Deck deck = Deck.create();
        // assert
        assertEquals(72, deck.size());
    }

    @Test
    void createWithCustomTilesShouldProduceADeckWithThoseTiles() {
        // Arrange
        List<Tile> customTiles = List.of(birdRedTile, turtleGreenTile);

        // Act
        Deck deck = Deck.create(customTiles);

        // Assert
        assertEquals(2, deck.size());
    }

    @Test
    void createWithEmptyListShouldProduceAnEmptyDeck() {
        // Arrange & Act
        Deck deck = Deck.create(new ArrayList<>());

        // Assert
        assertTrue(deck.isEmpty());
    }

    @Test
    void drawShouldReduceSizeByOne() {
        // arrange
        Deck deck = Deck.create();
        int initialSize = deck.size();

        // act
        deck.draw();

        // assert
        assertEquals(initialSize - 1, deck.size());
    }

    @Test
    void drawShouldReturnATile() {
        // arrange
        Deck deck = Deck.create(List.of(birdRedTile));

        // act
        Tile drawnTile = deck.draw();

        // assert
        assertNotNull(drawnTile);
    }

    @Test
    void drawOnEmptyDeckShouldThrowEmptyDeckException() {
        // Arrange
        Deck deck = Deck.create(new ArrayList<>());

        // Act & Assert
        assertThrowsExactly(EmptyDeckException.class, deck::draw);
    }

    @Test
    void addTilesShouldIncreaseTheSizeByTheNumberOfAddedTiles() {
        // Arrange
        Deck deck = Deck.create(new ArrayList<>());

        // Act
        deck.addTiles(birdRedTile, turtleGreenTile);

        // Assert
        assertEquals(2, deck.size());
    }

    @Test
    void addTilesShouldMakeTheDeckNonEmpty() {
        // Arrange
        Deck deck = Deck.create(new ArrayList<>());

        // Act
        deck.addTiles(birdRedTile);

        // Assert
        assertFalse(deck.isEmpty());
    }

    @Test
    void isEmptyShouldReturnTrueForAnEmptyDeck() {
        // Arrange
        Deck deck = Deck.create(new ArrayList<>());

        // Act
        boolean empty = deck.isEmpty();

        // Assert
        assertTrue(empty);
    }

    @Test
    void isEmptyShouldReturnFalseForANonEmptyDeck() {
        // Arrange
        Deck deck = Deck.create(List.of(birdRedTile));

        // Act
        boolean empty = deck.isEmpty();

        // Assert
        assertFalse(empty);
    }

    @Test
    void getTilesShouldReturnAllContainedTiles() {
        // Arrange
        List<Tile> originalTiles = new ArrayList<>(List.of(birdRedTile, turtleGreenTile));
        Deck deck = Deck.create(originalTiles);

        // Act
        List<Tile> tilesInDeck = deck.getTiles();

        // Assert
        assertEquals(2, tilesInDeck.size());
    }
}
