package latice.model.referee;

import latice.model.board.Board;
import latice.model.game.Game;
import latice.model.player.Player;
import latice.model.referee.rule.FirstTurnRule;
import latice.model.referee.rule.IRule;
import latice.model.tile.Tile;

import java.util.Comparator;
import java.util.List;

public class Referee {
    private final List<IRule> rules;

    private Referee(List<IRule> rules) {
        this.rules = rules;
    }

    public static Referee create(List<IRule> rules) {
        rules = rules.stream()
                .sorted(Comparator.comparingInt(IRule::getPriority))
                .toList();
        return new Referee(rules);
    }

    public boolean isValidMove(Board board, Tile tile, int x, int y) {
        for (IRule rule : rules) {
            if (rule instanceof FirstTurnRule && rule.validate(board, tile, x, y)) {
                return true;
            }
            if (!rule.validate(board, tile, x, y)) {
                return false;
            }
        }
        return true;
    }

    public int calculateScore(Board board, Tile tile, int x, int y) {
        // TODO V6: implement scoring logic
        return 0;
    }

    public boolean canBuyExtraAction(int playerScore) {
        // TODO V7: return playerScore >= EXTRA_ACTION_COST
        return false;
    }

    public Player determineWinner(Game game) {
        // TODO V6: iterate game.getPlayers(), compare getTilesPlayed(), return null on tie
        return null;
    }

    public boolean isGameOver(Game game) {
        int maxTurns = 10 * game.getPlayers().size();
        if (game.getTurnCount() >= maxTurns) {
            return true;
        }

        for (Player player : game.getPlayers()) {
            if (player.getDeck().isEmpty() && player.getRack().isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
