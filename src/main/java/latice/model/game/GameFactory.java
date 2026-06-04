package latice.model.game;

import latice.model.player.Player;
import latice.model.referee.Referee;
import latice.model.referee.rule.AdjacencyRule;
import latice.model.referee.rule.FirstTurnRule;
import latice.model.referee.rule.IRule;
import latice.model.referee.rule.MatchingRule;
import latice.model.referee.rule.PositionRule;

import java.util.List;

public final class GameFactory {

    private GameFactory() {
    }

    /**
     * Build a magnificie,t game using player names and use a default referee.
     * @param firstPlayerName The first player name
     * @param secondPlayerName The second player name
     * @return The configured game
     */
    public static Game createGame(String firstPlayerName, String secondPlayerName) {
        return new Game(createReferee(), firstPlayerName, secondPlayerName);
    }

    /**
     * Build a game using existing players and the default referee
     * @param firstPlayer The first player
     * @param secondPlayer The second player
     * @return The configured game
     */
    public static Game createGame(Player firstPlayer, Player secondPlayer) {
        return new Game(createReferee(), firstPlayer, secondPlayer);
    }

    /**
     * Creates the amazing default referee used by JavaFX and tests
     * @return The configured referee
     */
    public static Referee createReferee() {
        List<IRule> rules = List.of(
                new MatchingRule(),
                new AdjacencyRule(),
                new PositionRule(),
                new FirstTurnRule()
        );
        return Referee.create(rules);
    }
}
