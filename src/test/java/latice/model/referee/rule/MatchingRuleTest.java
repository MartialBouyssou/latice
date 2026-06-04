package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchingRuleTest {

    private Board board;
    private MatchingRule matchingRule;

    @BeforeEach
    void setUp() {
        board = Board.create();
        matchingRule = new MatchingRule();
    }

    @Test
    void validateShouldReturnTrueWhenNoNeighborIsOccupied() {
        // arrange
        Tile tile = new Tile(Shape.BIRD, Color.RED);

        // Act
        boolean valid = matchingRule.validate(board, tile, 4, 4);
        // Assert
        assertTrue(valid);
    }

    @Test
    void validateShouldReturnTrueWhenTileSharingColorWithNeighbor() {
        // Arrange
        Tile neighborTile = new Tile(Shape.BIRD, Color.RED);
        Tile placedTile = new Tile(Shape.GECKO, Color.RED);
        board.placeTile(neighborTile, 4, 4);

        // Act
        boolean valid = matchingRule.validate(board, placedTile, 4, 5);

        // Assert
        assertTrue(valid);
    }

    @Test
    void validateShouldReturnTrueWhenTileSharingShapeWithNeighbor() {
        // Arrange
        Tile neighborTile = new Tile(Shape.BIRD, Color.RED);
        Tile placedTile = new Tile(Shape.BIRD, Color.GREEN);
        board.placeTile(neighborTile, 4, 4);

        // Act
        boolean valid = matchingRule.validate(board, placedTile, 4, 5);

        // Assert
        assertTrue(valid);
    }

    @Test
    void validateShouldReturnFalseWhenTileMatchesNeitherColorNorShapeOfNeighbor() {
        // Arrange
        Tile neighborTile = new Tile(Shape.BIRD, Color.RED);
        Tile incompatibleTile = new Tile(Shape.GECKO, Color.GREEN);
        board.placeTile(neighborTile, 4, 4);

        // Act
        boolean valid = matchingRule.validate(board, incompatibleTile, 4, 5);

        // Assert
        assertFalse(valid);
    }

    @Test
    void validateShouldReturnTrueWhenTileMatchesBothColorAndShapeOfNeighbor() {
        // Arrange
        Tile neighborTile = new Tile(Shape.BIRD, Color.RED);
        Tile identicalTile = new Tile(Shape.BIRD, Color.RED);
        board.placeTile(neighborTile, 4, 4);

        // Act
        boolean valid = matchingRule.validate(board, identicalTile, 4, 5);

        // Assert
        assertTrue(valid);
    }

    @Test
    void validateShouldReturnFalseWhenOneNeighborMatchesButAnotherDoesNot() {
        // Arrange
        Tile redBirdTile = new Tile(Shape.BIRD, Color.RED);
        Tile greenGeckoTile = new Tile(Shape.GECKO, Color.GREEN);

        Tile incompatibleCandidate = new Tile(Shape.BIRD, Color.NAVY);

        board.placeTile(redBirdTile, 5, 4);
        board.placeTile(greenGeckoTile, 5, 6);

        //aCT
        boolean valid = matchingRule.validate(board, incompatibleCandidate, 5, 5);

        // Assert
        assertFalse(valid);
    }
}
