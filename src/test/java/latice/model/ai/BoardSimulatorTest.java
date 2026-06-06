package latice.model.ai;

import latice.model.board.Board;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class BoardSimulatorTest {

    @Test
    void testDeepCopyEmptyBoard() {
        // Arrange
        Board original = Board.create();

        // Act
        Board copy = BoardSimulator.deepCopy(original);

        // Assert
        assertNotSame(original, copy);
        assertTrue(copy.getCell(4, 4).isEmpty());
    }

    @Test
    void testDeepCopyWithTiles() {
        // Arrange
        Board original = Board.create();
        Tile tile1 = new Tile(Shape.BIRD, Color.RED);
        Tile tile2 = new Tile(Shape.DOLPHIN, Color.YELLOW);

        original.placeTile(tile1, 4, 4);
        original.placeTile(tile2, 0, 0);

        // Act
        Board copy = BoardSimulator.deepCopy(original);

        // Assert
        assertNotSame(original, copy);
        assertFalse(copy.getCell(4, 4).isEmpty());
        assertEquals(tile1, copy.getCell(4, 4).getTile());

        assertFalse(copy.getCell(0, 0).isEmpty());
        assertEquals(tile2, copy.getCell(0, 0).getTile());

        Tile newTile = new Tile(Shape.FLOWER, Color.MAGENTA);
        copy.placeTile(newTile, 1, 1);
        assertTrue(original.getCell(1, 1).isEmpty());
    }

    @Test
    void testPrivateConstructor() throws Exception {
        // Arrange
        Constructor<BoardSimulator> constructor = BoardSimulator.class.getDeclaredConstructor();

        // Act
        constructor.setAccessible(true);
        constructor.newInstance();

        // Assert
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
}