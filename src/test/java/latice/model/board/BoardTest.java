package latice.model.board;

import latice.exception.model.CellOccupiedException;
import latice.exception.model.InvalidPositionException;
import latice.exception.model.InvalidTileException;
import latice.model.board.cell.Cell;
import latice.model.board.cell.MoonCell;
import latice.model.board.cell.NormalCell;
import latice.model.board.cell.SunCell;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;
    private Tile birdRedTile;

    @BeforeEach
    void setUp() {
        board = Board.create();
        birdRedTile = new Tile(Shape.BIRD, Color.RED);
    }

    @Test
    void createShouldPlaceMoonCellAtCenterPosition4And4() {
        // Arrange & Act
        Cell centerCell = board.getCell(4, 4);

        // Assert
        assertInstanceOf(MoonCell.class, centerCell);
    }

    @Test
    void createShouldPlaceSunCellAtKnownSunPosition() {
        // Arrange & Act
        Cell sunCorner = board.getCell(0, 0);

        // Assert
        assertInstanceOf(SunCell.class, sunCorner);
    }

    @Test
    void createShouldPlaceNormalCellAtNonSpecialPosition() {
        // Arrange & act
        Cell normalCell = board.getCell(1, 0);

        // Assert
        assertInstanceOf(NormalCell.class, normalCell);
    }

    @Test
    void createShouldProduceABoardWithAllCellsInitiallyEmpty() {
        // All in one : AAA
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                assertTrue(board.getCell(row, column).isEmpty());
            }
        }
    }

    @Test
    void isInsideShouldReturnTrueForTopLeftCorner() {
        // Arrange & Act
        boolean inside = board.isInside(0, 0);

        // Assert
        assertTrue(inside);
    }

    @Test
    void isInsideShouldReturnTrueForBottomRightCorner() {
        // Arrange & Act
        boolean inside = board.isInside(8, 8);

        // Assert
        assertTrue(inside);
    }

    @Test
    void isInsideShouldReturnFalseForNegativeRow() {
        // Arrange & Act
        boolean inside = board.isInside(-1, 0);

        // Assert
        assertFalse(inside);
    }

    @Test
    void isInsideShouldReturnFalseForNegativeColumn() {
        // Arrnge & Act
        boolean inside = board.isInside(0, -1);

        // Assert
        assertFalse(inside);
    }

    @Test
    void isInsideShouldReturnFalseForRowEqualToGridSize() {
        // Arrange & Act
        boolean inside = board.isInside(9, 0);

        // Assert
        assertFalse(inside);
    }

    @Test
    void isInsideShouldReturnFalseForColumnEqualToGridSize() {
        // Arrange & Act
        boolean inside = board.isInside(0, 9);

        // Assert
        assertFalse(inside);
    }

    @Test
    void placeTileShouldMakeCellNonEmpty() {
        // arrange
        int row = 2;
        int column = 3;

        // act
        board.placeTile(birdRedTile, row, column);

        // assert
        assertFalse(board.getCell(row, column).isEmpty());
    }

    @Test
    void placeTileShouldStoreTheCorrectTileInTheCell() {
        // Arrange
        int row = 2;
        int column = 3;

        // Act
        board.placeTile(birdRedTile, row, column);

        // assert
        assertEquals(birdRedTile, board.getCell(row, column).getTile());
    }

    @Test
    void placeTileShouldThrowInvalidTileExceptionWhenTileIsNull() {
        // everything's in one line : AAA
        assertThrows(InvalidTileException.class, () -> board.placeTile(null, 0, 0));
    }

    @Test
    void placeTileShouldThrowInvalidPositionExceptionWhenPositionIsOutOfBounds() {
        // AAA
        assertThrows(InvalidPositionException.class,
                () -> board.placeTile(birdRedTile, 10, 0));
    }

    @Test
    void placeTileShouldThrowCellOccupiedExceptionWhenCellAlreadyHasATile() {
        // Arrange
        board.placeTile(birdRedTile, 3, 3);
        Tile secondTile = new Tile(Shape.GECKO, Color.GREEN);

        // Act & Assert
        assertThrows(CellOccupiedException.class, () -> board.placeTile(secondTile, 3, 3));
    }

    @Test
    void getCellShouldThrowInvalidPositionExceptionForOutOfBoundsPosition() {
        // AAA
        assertThrows(InvalidPositionException.class, () -> board.getCell(-1, 0));
    }

    @Test
    void getCellShouldReturnTheCellAtTheGivenPosition() {
        // Arrange
        board.placeTile(birdRedTile, 5, 5);

        // Act
        Cell cell = board.getCell(5, 5);

        // Assert
        assertEquals(birdRedTile, cell.getTile());
    }

    @Test
    void getNeighborsShouldReturnTwoNeighborsForCornerCell() {
        // Arrange & Act
        List<Cell> neighbors = board.getNeighbors(0, 0);

        // Assert
        assertEquals(2, neighbors.size());
    }

    @Test
    void getNeighborsShouldReturnThreeNeighborsForEdgeCell() {
        // Arrange & Act
        List<Cell> neighbors = board.getNeighbors(0, 4);

        // Assert
        assertEquals(3, neighbors.size());
    }

    @Test
    void getNeighborsShouldReturnFourNeighborsForCenterCell() {
        // Arrange & Act
        List<Cell> neighbors = board.getNeighbors(4, 4);

        // Assert
        assertEquals(4, neighbors.size());
    }

    @Test
    void getNeighborsShouldThrowInvalidPositionExceptionForOutOfBoundsPosition() {
        // AAA
        assertThrows(InvalidPositionException.class, () -> board.getNeighbors(-1, 0));
    }
}
