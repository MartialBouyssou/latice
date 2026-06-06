package latice.model.ai;

import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AIMoveTest {

    @Test
    void testAIMoveConstructorAndGetters() {
        // Arrange
        Tile tile = new Tile(Shape.TURTLE, Color.GREEN);
        int expectedX = 4;
        int expectedY = 5;
        int expectedScore = 3;

        // Act
        AIMove move = new AIMove(tile, expectedX, expectedY, expectedScore);

        // Assert
        assertEquals(tile, move.getTile());
        assertEquals(expectedX, move.getX());
        assertEquals(expectedY, move.getY());
        assertEquals(expectedScore, move.getScore());
    }
}