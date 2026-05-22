package latice.model.referee;

import latice.model.board.Board;
import latice.model.referee.rule.AdjacencyRule;
import latice.model.referee.rule.IRule;
import latice.model.referee.rule.MatchingRule;
import latice.model.referee.rule.PositionRule;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RefereeTest {
    private Board board;
    private Referee refereeWithAllRules;
    private Tile birdRedTile;

    @BeforeEach
    void setUp() {
        board = Board.create();
        birdRedTile = new Tile(Shape.BIRD, Color.RED);
        List<IRule> rules = List.of(new PositionRule(), new AdjacencyRule(), new MatchingRule());
        refereeWithAllRules = Referee.create(rules);
    }

    @Test
    void isValidMoveShouldReturnFalseWhenPositionIsOutOfBounds() {
        // Arrange
        int invalidRow = 10;
        int invalidColumn = 0;
        // Act
        boolean valid = refereeWithAllRules.isValidMove(board, birdRedTile, invalidRow, invalidColumn);

        // Assert
        assertFalse(valid);
    }

    @Test
    void isValidMoveShouldReturnFalseWhenCellIsOccupied() {
        // Arrange
        board.placeTile(birdRedTile, 3, 3);
        Tile anotherTile = new Tile(Shape.GECKO, Color.GREEN);

        // Act
        boolean valid = refereeWithAllRules.isValidMove(board, anotherTile, 3, 3);

        assertFalse(valid);
    }

    @Test
    void isValidMoveShouldReturnFalseWhenNoNeighborIsOccupied() {
        // arrange already done with before each
        // Act
        boolean valid = refereeWithAllRules.isValidMove(board, birdRedTile, 4, 4);

        // Assert
        assertFalse(valid);
    }

    @Test
    void isValidMoveShouldReturnTrueWhenAllRulesAreSatisfied() {
        // Arrange
        board.placeTile(birdRedTile, 4, 4);
        Tile samColorTile = new Tile(Shape.GECKO, Color.RED);

        // Act
        boolean valid = refereeWithAllRules.isValidMove(board, samColorTile, 4, 5);

        // Assert
        assertTrue(valid);
    }

    @Test
    void isValidMoveShouldReturnFalseWhenTileIsIncompatibleWithNeighbor() {
        // Arrange
        board.placeTile(birdRedTile, 4, 4);
        Tile incompatibleTile = new Tile(Shape.GECKO, Color.GREEN);
        // Act
        boolean valid = refereeWithAllRules.isValidMove(board, incompatibleTile, 4, 5);
        // Assert
        assertFalse(valid);
    }

    @Test
    void isValidMoveShouldReturnTrueWhenNoRulesAreConfigured() {
        // Arrange
        Referee emptyReferee = Referee.create(List.of());

        // Act
        boolean valid = emptyReferee.isValidMove(board, birdRedTile, 0, 0);
        // Assert
        assertTrue(valid);
    }
}
