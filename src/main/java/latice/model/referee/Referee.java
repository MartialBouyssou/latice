package latice.model.referee;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.board.cell.SunCell;
import latice.model.game.Game;
import latice.model.player.Player;
import latice.model.referee.rule.FirstTurnRule;
import latice.model.referee.rule.IRule;
import latice.model.tile.Tile;

import java.util.Comparator;
import java.util.List;

public class Referee {
    public static final int EXTRA_ACTION_COST = 2;
    private static final int SCORE_DOUBLE = 1;
    private static final int SCORE_TREFOIL = 2;
    private static final int SCORE_LATICE = 4;
    private static final int SCORE_SUN_BONUS = 2;
    private static final int NEIGHBORS_FOR_DOUBLE = 2;
    private static final int NEIGHBORS_FOR_TREFOIL = 3;
    private static final int NEIGHBORS_FOR_LATICE = 4;

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
            if (rule instanceof FirstTurnRule) {
                if (rule.validate(board, tile, x, y)) {
                    return true;
                }
            } else if (!rule.validate(board, tile, x, y)) {
                return false;
            }
        }
        return true;
    }

    public int calculateScore(Board board, Tile tile, int x, int y) {
        List<Cell> neighbors = board.getNeighbors(x, y);
        int matchingCount = 0;

        for (Cell neighbor : neighbors) {
            if (!neighbor.isEmpty() && (neighbor.getTile().getColor() == tile.getColor()
                    || neighbor.getTile().getShape() == tile.getShape())) {
                matchingCount++;
            }
        }

        int score = switch (matchingCount) {
            case NEIGHBORS_FOR_DOUBLE -> SCORE_DOUBLE;
            case NEIGHBORS_FOR_TREFOIL -> SCORE_TREFOIL;
            case NEIGHBORS_FOR_LATICE -> SCORE_LATICE;
            default -> 0;
        };

        if (board.getCell(x, y) instanceof SunCell) {
            score += SCORE_SUN_BONUS;
        }

        return score;
    }

    public Player determineWinner(Game game) {
        Player first = game.getPlayers().get(0);
        Player second = game.getPlayers().get(1);

        if (first.getTilesPlayed() > second.getTilesPlayed()) {
            return first;
        } else if (second.getTilesPlayed() > first.getTilesPlayed()) {
            return second;
        }

        return null;
    }

    public boolean isGameOver(Game game) {
        int totalTurns = Game.MAX_CYCLES * game.getPlayers().size();

        if (game.getTurnCount() >= totalTurns) {
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
