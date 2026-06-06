package latice.model.ai;

import latice.model.board.Board;
import latice.model.player.Rack;
import latice.model.referee.Referee;
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

class AIPlayerTest {

    private Referee referee;
    private AIPlayer aiPlayer;
    private Board board;
    private Rack rack;

    @BeforeEach
    void setUp() {
        // Arrange
        board = Board.create();
        List<IRule> rules = List.of(
                new MatchingRule(),
                new AdjacencyRule(),
                new PositionRule()
        );
        referee = Referee.create(rules);
        aiPlayer = new AIPlayer(referee);
        rack = new Rack();

        board.placeTile(new Tile(Shape.BIRD, Color.RED), 4, 4);
    }

    @Test
    void testFindBestMoveNoValidMoves() {
        // Arrange
        rack.addTile(new Tile(Shape.TURTLE, Color.NAVY));

        // Act
        AIMove move = aiPlayer.findBestMove(board, rack, 0, 5);

        // Assert
        assertNull(move);
    }

    @Test
    void testFindBestMoveWithValidMove() {
        // Arrange
        rack.addTile(new Tile(Shape.BIRD, Color.GREEN));

        // Act
        AIMove move = aiPlayer.findBestMove(board, rack, 0, 5);

        // Assert
        assertNotNull(move);
    }

    @Test
    void testFindBestMoveSelectsBestOption() {
        // Arrange
        board.placeTile(new Tile(Shape.BIRD, Color.YELLOW), 4, 6);
        rack.addTile(new Tile(Shape.BIRD, Color.GREEN));

        // Act
        AIMove move = aiPlayer.findBestMove(board, rack, 0, 5);

        // Assert
        assertNotNull(move);
        assertEquals(4, move.getX());
        assertEquals(5, move.getY());
    }

    @Test
    void testIsDoublePlayWorthItNotEnoughPoints() {
        // Arrange
        rack.addTile(new Tile(Shape.BIRD, Color.GREEN));
        rack.addTile(new Tile(Shape.DOLPHIN, Color.RED));

        // Act
        boolean worthIt = aiPlayer.isDoublePlayWorthIt(board, rack, Referee.EXTRA_ACTION_COST - 1, 5);

        // Assert
        assertFalse(worthIt);
    }

    @Test
    void testIsDoublePlayWorthItNotEnoughTiles() {
        // Arrange
        rack.addTile(new Tile(Shape.BIRD, Color.GREEN));

        // Act
        boolean worthIt = aiPlayer.isDoublePlayWorthIt(board, rack, Referee.EXTRA_ACTION_COST, 5);

        // Assert
        assertFalse(worthIt);
    }

    @Test
    void testIsDoublePlayWorthItNoFirstMovePossible() {
        // Arrange
        rack.addTile(new Tile(Shape.TURTLE, Color.NAVY));
        rack.addTile(new Tile(Shape.FLOWER, Color.MAGENTA));

        // Act
        boolean worthIt = aiPlayer.isDoublePlayWorthIt(board, rack, Referee.EXTRA_ACTION_COST, 5);

        // Assert
        assertFalse(worthIt);
    }

    @Test
    void testIsDoublePlayWorthItIsWorthIt() {
        // Arrange
        board.placeTile(new Tile(Shape.DOLPHIN, Color.RED), 3, 5);
        board.placeTile(new Tile(Shape.GECKO, Color.RED), 5, 5);

        rack.addTile(new Tile(Shape.TURTLE, Color.RED));
        rack.addTile(new Tile(Shape.FLOWER, Color.RED));

        // Act
        boolean worthIt = aiPlayer.isDoublePlayWorthIt(board, rack, Referee.EXTRA_ACTION_COST, 5);

        // Assert
        assertTrue(worthIt);
    }
}