//Made by Gabriel, copy paste for merge
package latice.model.game;

import latice.model.deck.Deck;
import latice.model.player.Player;
import latice.model.player.Rack;
import latice.model.referee.Referee;
import latice.model.referee.rule.AdjacencyRule;
import latice.model.referee.rule.FirstTurnRule;
import latice.model.referee.rule.IRule;
import latice.model.referee.rule.MatchingRule;
import latice.model.referee.rule.PositionRule;
import latice.model.tile.Color;
import latice.model.tile.Shape;
import latice.model.tile.Tile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void playTileShouldEndTurnWhenNoAction() {
        Tile tile = new Tile(Shape.BIRD, Color.RED);
        Player firstPlayer = createPlayer("P1", List.of(tile), List.of());
        Player secondPlayer = createPlayer("P2", List.of(new Tile(Shape.GECKO, Color.GREEN)),
                List.of());
        Game game = createStartedGame(firstPlayer, secondPlayer);

        game.playTile(tile, 4, 4);

        assertEquals(secondPlayer, game.getCurrentPlayer());
        assertEquals(1, game.getTurnCount());
    }

    @Test
    void playTileShouldKeepTurnWhenAction() {
        Tile tile = new Tile(Shape.BIRD, Color.RED);
        Player firstPlayer = createPlayer("P1", List.of(tile), List.of());
        Player secondPlayer = createPlayer("P2", List.of(new Tile(Shape.GECKO, Color.GREEN)),
                List.of());
        Game game = createStartedGame(firstPlayer, secondPlayer);

        firstPlayer.addScore(Referee.EXTRA_ACTION_COST);
        game.buyAnAction();
        game.playTile(tile, 4, 4);

        assertEquals(firstPlayer, game.getCurrentPlayer());
        assertEquals(0, game.getTurnCount());
        assertTrue(game.hasRemainingAction());
    }

    @Test
    void exchangeAllShouldEndTurnWhenNoAction() {
        Player firstPlayer = createPlayer("P1", createFullRack(), createExchangeDeck());
        Player secondPlayer = createPlayer("P2", createFullRack(), createExchangeDeck());
        Game game = createStartedGame(firstPlayer, secondPlayer);

        game.exchangeAll();

        assertEquals(secondPlayer, game.getCurrentPlayer());
        assertEquals(1, game.getTurnCount());
    }

    @Test
    void exchangeAllShouldKeepTurnWhenOneMoreAction() {
        Player firstPlayer = createPlayer("P1", createFullRack(), createExchangeDeck());
        Player secondPlayer = createPlayer("P2", createFullRack(), createExchangeDeck());
        Game game = createStartedGame(firstPlayer, secondPlayer);

        firstPlayer.addScore(Referee.EXTRA_ACTION_COST);
        game.buyAnAction();
        game.exchangeAll();

        assertEquals(firstPlayer, game.getCurrentPlayer());
        assertEquals(0, game.getTurnCount());
        assertTrue(game.hasRemainingAction());
    }

    @Test
    void passTurnShouldSendOnlyTurnPassedEventForNNormalTurn() {
        Player firstPlayer = createPlayer("P1", createFullRack(), List.of());
        Player secondPlayer = createPlayer("P2", createFullRack(), List.of());
        Game game = createStartedGame(firstPlayer, secondPlayer);
        GameTestListener listener = new GameTestListener();
        game.addListener(listener);

        game.passTurn();

        assertEquals(1, listener.turnPassedCount);
        assertEquals(0, listener.turnEndedCount);
        assertEquals(secondPlayer, game.getCurrentPlayer());
    }

    private Game createStartedGame(Player firstPlayer, Player secondPlayer) {
        Game game = new Game(createReferee(), firstPlayer, secondPlayer);
        game.startFromExistingPlayers();
        return game;
    }

    private Referee createReferee() {
        List<IRule> rules = List.of(
                new MatchingRule(),
                new AdjacencyRule(),
                new PositionRule(),
                new FirstTurnRule()
        );
        return Referee.create(rules);
    }

    private Player createPlayer(String name, List<Tile> rackTiles, List<Tile> deckTiles) {
        return Player.create(name, new Rack(rackTiles), Deck.createNoShuffle(deckTiles));
    }

    private List<Tile> createFullRack() {
        return List.of(
                new Tile(Shape.BIRD, Color.RED),
                new Tile(Shape.GECKO, Color.GREEN),
                new Tile(Shape.DOLPHIN, Color.YELLOW),
                new Tile(Shape.FLOWER, Color.MAGENTA),
                new Tile(Shape.TURTLE, Color.TEAL)
        );
    }

    private List<Tile> createExchangeDeck() {
        return List.of(
                new Tile(Shape.FEATHER, Color.NAVY),
                new Tile(Shape.FEATHER, Color.RED),
                new Tile(Shape.FEATHER, Color.GREEN),
                new Tile(Shape.FEATHER, Color.YELLOW),
                new Tile(Shape.FEATHER, Color.TEAL)
        );
    }
}
