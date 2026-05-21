package latice.model.referee;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.game.Game;
import latice.model.player.Player;
import latice.model.referee.rule.IRule;
import latice.model.tile.Tile;

import java.util.List;

public class Referee {
    private final List<IRule> rules;

    public Referee(List<IRule> rules) {
        this.rules = rules;
    }

    // V5
    public boolean isValidMove(Board board, Tile tile, int x, int y) {
        for (IRule rule : rules) {
            if (!rule.validate(board, tile, x, y)) {
                return false;
            }
        }
        return true;
    }

    public int calculateScore(Board board, Tile tile, int x, int y) {
        List<Cell> neighbors = board.getNeighbors(x, y);
        int matchingCount = 0;

        for (Cell neighbor : neighbors) {
            if (!neighbor.isEmpty() && (neighbor.getTile().getColor() == tile.getColor() ||
                            neighbor.getTile().getShape() == tile.getShape())) {
                    matchingCount++;
                }
        }

        return switch (matchingCount) {
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 4;
            default -> 0;
        };
    }

    public boolean canBuyExtraAction(int playerScore) {
        // TODO V7: return playerScore >= EXTRA_ACTION_COST
        return false;
    }

    public Player determineWinner(Game game) {
        int firstPlayerPlayedCount = game.getPlayers().get(0).getTilesPlayed();
        int secondPlayerPlayedCount = game.getPlayers().get(1).getTilesPlayed();

        if (firstPlayerPlayedCount > secondPlayerPlayedCount) {
            return game.getPlayers().get(0);
        } else if (secondPlayerPlayedCount > firstPlayerPlayedCount) {
            return game.getPlayers().get(1);
        }

        return null;
    }

    public boolean isGameOver(Game game) {
        int maxTurns = Game.MAX_TURN_COUNT * game.getPlayers().size();
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
