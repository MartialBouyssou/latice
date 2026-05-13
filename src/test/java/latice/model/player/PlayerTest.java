package latice.model.player;

import latice.model.deck.Deck;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = Deck.create();
    }

    @Test
    void createShouldSetPlayerName() {
        // Arrange & Act
        Player player = Player.create("Alice", deck);

        // Assert
        assertEquals("Alice", player.getName());
    }

    @Test
    void createShouldStartWithZeroScore() {
        // Arrange & Act
        Player player = Player.create("Alice", deck);

        // Assert
        assertEquals(0, player.getScore());
    }

    @Test
    void createShouldStartWithZeroTilesPlayed() {
        // Arrange & Act
        Player player = Player.create("Alice", deck);

        // Assert
        assertEquals(0, player.getTilesPlayed());
    }

    @Test
    void createShouldFillRackWithFiveTiles() {
        // Arrange & act
        Player player = Player.create("Alice", deck);

        // Assert
        assertEquals(5, player.getRack().size());
    }

    @Test
    void addScoreShouldIncreaseScoreByTheGivenAmount() {
        // Arrange
        Player player = Player.create("Alice", deck);

        // Act
        player.addScore(10);

        // Assert
        assertEquals(10, player.getScore());
    }

    @Test
    void addScoreCalledMultipleTimesShouldAccumulateScore() {
        // Arrange
        Player player = Player.create("Alice", deck);

        // Act
        player.addScore(5);
        player.addScore(3);

        // Assert
        assertEquals(8, player.getScore());
    }

    @Test
    void canAffordShouldReturnTrueWhenScoreIsEqualToTheCost() {
        // Arrange
        Player player = Player.create("Alice", deck);
        player.addScore(10);

        // Act
        boolean canAfford = player.canAfford(10);

        // Assert
        assertTrue(canAfford);
    }

    @Test
    void canAffordShouldReturnTrueWhenScoreIsGreaterThanTheCost() {
        // Arrange
        Player player = Player.create("Alice", deck);
        player.addScore(15);

        // Act
        boolean canAfford = player.canAfford(10);

        // Assert
        assertTrue(canAfford);
    }

    @Test
    void canAffordShouldReturnFalseWhenScoreIsLessThanTheCost() {
        // Arrange
        Player player = Player.create("Alice", deck);
        player.addScore(5);

        // Act
        boolean canAfford = player.canAfford(10);

        // Assert
        assertFalse(canAfford);
    }

    @Test
    void spendScoreShouldReduceScoreByTheGivenCost() {
        // Arrange
        Player player = Player.create("Alice", deck);
        player.addScore(20);

        // Act
        player.spendScore(7);

        // Assert
        assertEquals(13, player.getScore());
    }

    @Test
    void spendScoreShouldThrowIllegalStateExceptionWhenPlayerCannotAffordIt() {
        // Arrange
        Player player = Player.create("Alice", deck);
        player.addScore(3);

        // Act & assert
        assertThrows(IllegalStateException.class, () -> player.spendScore(10));
    }

    @Test
    void incrementTilesPlayedShouldIncreaseCountByOne() {
        // Arrange
        Player player = Player.create("Alice", deck);

        // Act
        player.incrementTilesPlayed();
        // Assert
        assertEquals(1, player.getTilesPlayed());
    }

    @Test
    void incrementTilesPlayedCalledMultipleTimesShouldAccumulateCount() {
        // Arrange
        Player player = Player.create("Alice", deck);

        // Act
        player.incrementTilesPlayed();
        player.incrementTilesPlayed();
        player.incrementTilesPlayed();

        // Assert
        assertEquals(3, player.getTilesPlayed());
    }

    @Test
    void drawTileShouldIncreaseRackSizeByOne() {
        // Arrange
        Player player = Player.create("Alice", deck);
        int initialRackSize = player.getRack().size();

        player.getRack().removeTile(player.getRack().getTiles().get(0));

        // Act
        player.drawTile();

        // Assert
        assertEquals(initialRackSize, player.getRack().size());
    }

    @Test
    void drawTileShouldDecreaseDeckSizeByOne() {
        // Arrange
        Player player = Player.create("Alice", deck);
        int initialDeckSize = player.getDeck().size();

        player.getRack().removeTile(player.getRack().getTiles().get(0));

        // Act
        player.drawTile();

        // Assert
        assertEquals(initialDeckSize - 1, player.getDeck().size());
    }
}
