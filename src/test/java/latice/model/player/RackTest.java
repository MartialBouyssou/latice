package latice.model.player;

import latice.exception.model.EmptyDeckException;
import latice.exception.model.RackFullException;
import latice.model.deck.Deck;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RackTest {

    private Tile birdRedTile;
    private Tile turtleGreenTile;
    private Tile dolphinYellowTile;
    private Tile geckoNavyTile;
    private Tile flowerMagentaTile;

    @BeforeEach
    void setUp() {
        birdRedTile = new Tile(Shape.BIRD, Color.RED);
        turtleGreenTile = new Tile(Shape.TURTLE, Color.GREEN);
        dolphinYellowTile = new Tile(Shape.DOLPHIN, Color.YELLOW);
        geckoNavyTile = new Tile(Shape.GECKO, Color.NAVY);
        flowerMagentaTile = new Tile(Shape.FLOWER, Color.MAGENTA);
    }

    @Test
    void addTileShouldIncreaseSizeByOne() {
        // Arrange
        Rack rack = new Rack();

        // Act
        rack.addTile(birdRedTile);

        // Assert
        assertEquals(1, rack.size());
    }

    @Test
    void addTileShouldThrowRackFullExceptionWhenRackAlreadyHasFiveTiles() {
        // Arrange
        Rack rack = new Rack(new ArrayList<>(List.of(
                birdRedTile, turtleGreenTile, dolphinYellowTile,
                geckoNavyTile, flowerMagentaTile
        )));

        // Act & assert
        assertThrows(RackFullException.class,
                () -> rack.addTile(new Tile(Shape.FEATHER, Color.TEAL)));
    }

    @Test
    void removeTileShouldDecreaseSizeByOne() {
        // Arrange
        Rack rack = new Rack(new ArrayList<>(List.of(birdRedTile, turtleGreenTile)));

        // Act
        rack.removeTile(birdRedTile);

        // Assert
        assertEquals(1, rack.size());
    }

    @Test
    void removeTileShouldActuallyRemoveTheSpecifiedTile() {
        // Arrange
        Rack rack = new Rack(new ArrayList<>(List.of(birdRedTile, turtleGreenTile)));

        // Act
        rack.removeTile(birdRedTile);

        // Assert
        assertFalse(rack.getTiles().contains(birdRedTile));
    }

    @Test
    void removeTileShouldAcceptMultipleTilesAtOnce() {
        // Arrange
        Rack rack = new Rack(new ArrayList<>(List.of(birdRedTile, turtleGreenTile, dolphinYellowTile)));

        // Act
        rack.removeTile(birdRedTile, turtleGreenTile);

        // Assert
        assertEquals(1, rack.size());
    }

    @Test
    void refillShouldFillRackUpToFiveTiles() {
        // Arrange
        Rack rack = new Rack();
        Deck deck = Deck.create();

        // Act
        rack.refill(deck);

        // Assert
        assertEquals(5, rack.size());
    }

    @Test
    void refillShouldNotExceedFiveTilesWhenRackIsAlreadyPartiallyFilled() {
        // Arrange
        Rack rack = new Rack(new ArrayList<>(List.of(birdRedTile, turtleGreenTile)));
        Deck deck = Deck.create();

        // Act
        rack.refill(deck);

        // Assert
        assertEquals(5, rack.size());
    }

    @Test
    void refillShouldNotAddTilesWhenDeckIsEmpty() {
        // Arrange
        Rack rack = new Rack(new ArrayList<>(List.of(birdRedTile)));
        Deck emptyDeck = Deck.create(new ArrayList<>());

        // Act
        rack.refill(emptyDeck);

        // Assert
        assertEquals(1, rack.size());
    }

    @Test
    void exchangeAllShouldRefillRackWith5NewTiles() {
        // Arrange
        Rack rack = new Rack(new ArrayList<>(List.of(
                birdRedTile, turtleGreenTile, dolphinYellowTile,
                geckoNavyTile, flowerMagentaTile
        )));
        Deck largeDeck = Deck.create();

        // Act
        rack.exchangeAll(largeDeck);

        // Assert
        assertEquals(5, rack.size());
    }

    @Test
    void exchangeAllShouldThrowEmptyDeckExceptionWhenDeckIsEmpty() {
        // Arrange
        Rack rack = new Rack(new ArrayList<>(List.of(birdRedTile)));
        Deck emptyDeck = Deck.create(new ArrayList<>());

        // Act & assert
        assertThrows(EmptyDeckException.class, () -> rack.exchangeAll(emptyDeck));
    }

    @Test
    void isEmptyShouldReturnTrueForNewEmptyRack() {
        // Arrange
        Rack rack = new Rack();

        // Act
        boolean empty = rack.isEmpty();

        // Assert
        assertTrue(empty);
    }

    @Test
    void isEmptyShouldReturnFalseAfterTileIsAdded() {
        // Arrange
        Rack rack = new Rack();

        // Act
        rack.addTile(birdRedTile);

        // Assert
        assertFalse(rack.isEmpty());
    }

    @Test
    void sizeShouldReturnZeroForNewRack() {
        // Arrange
        Rack rack = new Rack();

        // Act
        int size = rack.size();

        // Assert
        assertEquals(0, size);
    }

    @Test
    void sizeShouldReflectNumberOfTilesInRack() {
        // Arrange
        Rack rack = new Rack(new ArrayList<>(List.of(birdRedTile, turtleGreenTile)));

        // Act
        int size = rack.size();

        // Assert
        assertEquals(2, size);
    }
}
