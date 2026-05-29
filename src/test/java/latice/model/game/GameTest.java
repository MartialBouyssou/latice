package latice.model.game;

import latice.exception.model.GameNotStartException;
import latice.exception.model.NoTilePlayedException;
import latice.model.player.Player;
import latice.model.referee.Referee;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Referee emptyReferee;
    private Game game;
    private Tile birdRedTile;

    @BeforeEach
    void setUp() {
        emptyReferee = Referee.create(List.of());
        game = new Game(emptyReferee, "Alice", "Bob");
        birdRedTile = new Tile(Shape.BIRD, Color.RED);
    }

    @Test
    void constructorShouldSetDefaultNamesWhenOnlyRefereeIsProvided() {
        // Arrange
        Game defaultGame = new Game(emptyReferee);

        // Act
        defaultGame.startGame();

        // Assert
        assertEquals("P1", defaultGame.getPlayers().get(0).getName());
        assertEquals("P2", defaultGame.getPlayers().get(1).getName());
    }

    @Test
    void startGameShouldInitializeBoardAndPlayers() {
        // Act
        game.startGame();

        // Assert
        assertNotNull(game.getBoard());
        assertEquals(2, game.getPlayers().size());
    }

    @Test
    void playTileShouldThrowGameNotStartExceptionWhenGameIsNotStarted() {
        // Act & Assert
        assertThrows(GameNotStartException.class, () -> game.playTile(birdRedTile, 4, 4));
    }

    @Test
    void playTileShouldPlaceTileWhenMoveIsValid() {
        // Arrange
        game.startGame();
        Player currentPlayer = game.getCurrentPlayer();
        Tile tileToPlay = currentPlayer.getRack().getTiles().get(0);

        // Act
        game.playTile(tileToPlay, 4, 4);

        // Assert
        assertTrue(game.isActionPlayedThisTurn());
        assertFalse(currentPlayer.getRack().getTiles().contains(tileToPlay));
        assertEquals(tileToPlay, game.getBoard().getCell(4, 4).getTile());
    }

    @Test
    void endTurnShouldThrowGameNotStartExceptionWhenGameIsNotStarted() {
        // Act & Assert
        assertThrows(GameNotStartException.class, () -> game.endTurn());
    }

    @Test
    void endTurnShouldThrowNoTilePlayedExceptionWhenNoActionWasPlayed() {
        // Arrange
        game.startGame();

        // Act & Assert
        assertThrows(NoTilePlayedException.class, () -> game.endTurn());
    }

    @Test
    void endTurnShouldRefillRackAndAdvancePlayer() {
        // Arrange
        game.startGame();
        Player initialPlayer = game.getCurrentPlayer();
        Tile tileToPlay = initialPlayer.getRack().getTiles().get(0);
        game.playTile(tileToPlay, 4, 4);

        // Act
        game.endTurn();

        // Assert
        assertEquals(5, initialPlayer.getRack().size());
        assertNotEquals(initialPlayer, game.getCurrentPlayer());
    }

    @Test
    void passTurnShouldSetActionPlayedAndCallEndTurn() {
        // Arrange
        game.startGame();
        Player initialPlayer = game.getCurrentPlayer();

        // Act
        game.passTurn();

        // Assert
        assertNotEquals(initialPlayer, game.getCurrentPlayer());
    }

    @Test
    void buyAnActionShouldSpendScoreWhenPlayerCanAffordIt() {
        // Arrange
        game.startGame();
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayer.addScore(5);

        // Act
        game.buyAnAction();

        // Assert
        assertEquals(3, currentPlayer.getScore());
    }

    @Test
    void buyAnActionShouldNotSpendScoreWhenPlayerCannotAffordIt() {
        // Arrange
        game.startGame();
        Player currentPlayer = game.getCurrentPlayer();

        // Act
        game.buyAnAction();

        // Assert
        assertEquals(0, currentPlayer.getScore());
    }

    @Test
    void exchangeAllShouldExchangeRackAndEndTurn() {
        // Arrange
        game.startGame();
        Player initialPlayer = game.getCurrentPlayer();
        int deckSizeBeforeExchange = initialPlayer.getDeck().size();

        // Act
        game.exchangeAll();

        // Assert
        assertEquals(5, initialPlayer.getRack().size());
        assertEquals(deckSizeBeforeExchange, initialPlayer.getDeck().size());
        assertNotEquals(initialPlayer, game.getCurrentPlayer());
    }

    @Test
    void isGameOverShouldThrowGameNotStartExceptionWhenGameIsNotStarted() {
        // Act & Assert
        assertThrows(GameNotStartException.class, () -> game.isGameOver());
    }

    @Test
    void getWinnerShouldThrowGameNotStartExceptionWhenGameIsNotStarted() {
        // Act & Assert
        assertThrows(GameNotStartException.class, () -> game.getWinner());
    }

    @Test
    void getRefereeShouldReturnTheProvidedReferee() {
        // Act
        Referee currentReferee = game.getReferee();

        // Assert
        assertEquals(emptyReferee, currentReferee);
    }
}