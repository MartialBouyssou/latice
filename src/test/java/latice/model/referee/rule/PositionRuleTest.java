package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionRuleTest {
    private Board board;
    private Tile birdRedTile;
    private PositionRule positionRule;

    @BeforeEach
    void setUp() {
        board = Board.create();
        birdRedTile = new Tile(Shape.BIRD, Color.RED);
        positionRule = new PositionRule();
    }

    @Test
    void validateShouldReturnTrueForAnEmptyCellInsideTheBoard() {
        int row = 3;
        int column = 3;
        boolean valid = positionRule.validate(board, birdRedTile, row, column);

        assertTrue(valid);
    }

    @Test
    void validateShouldReturnFalseForAPositionOutsideTheBoard() {
        int invalidRow = 10;
        int invalidColumn = 0;
        boolean valid = positionRule.validate(board, birdRedTile, invalidRow, invalidColumn);

        assertFalse(valid);
    }

    @Test
    void validateShouldReturnFalseForAnAlreadyOccupiedCell() {
        int row = 3;
        int column = 3;
        board.placeTile(birdRedTile, row, column);
        Tile anotherTile = new Tile(Shape.GECKO, Color.GREEN);
        boolean valid = positionRule.validate(board, anotherTile, row, column);

        assertFalse(valid);
    }
}
