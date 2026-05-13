package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjacencyRuleTest {

    private Board board;
    private Tile birdRedTile;
    private Tile turtleGreenTile;
    private AdjacencyRule adjacencyRule;

    @BeforeEach
    void setUp() {
        board = Board.create();
        birdRedTile = new Tile(Shape.BIRD, Color.RED);
        turtleGreenTile = new Tile(Shape.TURTLE, Color.GREEN);
        adjacencyRule = new AdjacencyRule();
    }

    @Test
    void validateShouldReturnFalseWhenNoNeighborIsOccupied() {
        // Arrange
        int row = 4;
        int column = 4;
        // Act
        boolean valid = adjacencyRule.validate(board, birdRedTile, row, column);

        // Assert
        assertFalse(valid);
    }

    @Test
    void validateShouldReturnTrueWhenAtLeastOneNeighborIsOccupied() {
        // Arrange
        board.placeTile(birdRedTile, 4, 4);
        int targetRow = 4;
        int targetColumn = 5;
        // Act
        boolean valid = adjacencyRule.validate(board, turtleGreenTile, targetRow, targetColumn);
        // Assert
        assertTrue(valid);
    }

    @Test
    void validateShouldReturnFalseForAnIsolatedCellWithAllEmptyNeighbors() {
        // Arrange
        board.placeTile(birdRedTile, 0, 0);
        int targetRow = 8;
        int targetColumn = 8;

        // Act
        boolean valid = adjacencyRule.validate(board, turtleGreenTile, targetRow, targetColumn);
        // Assert
        assertFalse(valid);
    }

    @Test
    void validateShouldReturnTrueWhenNeighborIsAboveTheTarget() {
        // Arrange
        board.placeTile(birdRedTile, 3, 3);
        int targetRow = 4;
        int targetColumn = 3;

        // Act
        boolean valid = adjacencyRule.validate(board, turtleGreenTile, targetRow, targetColumn);
        // Assert
        assertTrue(valid);
    }

    @Test
    void validateShouldReturnTrueWhenNeighborIsToTheLeftOfTheTarget() {
        // arrange
        board.placeTile(birdRedTile, 5, 5);
        int targetRow = 5;
        int targetColumn = 6;

        // act
        boolean valid = adjacencyRule.validate(board, turtleGreenTile, targetRow, targetColumn);

        // assert
        assertTrue(valid);
    }
}
