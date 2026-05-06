package latice.model.referee;

import latice.model.board.Board;
import latice.model.game.Game;
import latice.model.referee.rule.IRule;
import latice.model.tile.Tile;

import java.util.List;

public class Referee {
    private final List<IRule> rules;

    public Referee(List<IRule> rules) {
        this.rules = rules;
    }

    public boolean isValidMove(Board board, Tile tile, int x, int y) {
        for (IRule rule : rules) {
            if (!rule.validate(board, tile, x, y)) {
                return false;
            }
        }
        return true;
    }

    public int calculateScore(Board board, Tile tile, int x, int y) {
        // TODO: Implémenter le système de score
        return 0;
    }

    public boolean isGameOver(Game game) {
        if (game.getTurnCount() == 10) {
            return true;
        }
        // Check if players have any tile left (in rack / deck)
        boolean playerOneHasNoTiles = game.getPlayers().get(0).getDeck().isEmpty() &&
                game.getPlayers().get(0).getRack().isEmpty();
        boolean playerTwoHasNoTiles = game.getPlayers().get(1).getDeck().isEmpty() &&
                game.getPlayers().get(1).getRack().isEmpty();

        return playerOneHasNoTiles || playerTwoHasNoTiles;
    }
}
