package latice.model.referee;

import latice.model.board.Board;
import latice.model.board.BoardPosition;
import latice.model.board.cell.Cell;
import latice.model.board.cell.SunCell;
import latice.model.game.Game;
import latice.model.player.Player;
import latice.model.referee.rule.FirstTurnRule;
import latice.model.referee.rule.IRule;
import latice.model.tile.Tile;

import java.util.ArrayList;
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

    /**
     * Creates a referee that validates moves using the given rules.
     * @param rules The list of rules used to validate moves.
     */
    private Referee(List<IRule> rules) {
        this.rules = rules;
    }

    /**
     * Creates a referee with its rules sorted by ascending priority.
     * @param rules The rules to assign to the referee.
     * @return A new referee using the sorted rules.
     */
    public static Referee create(List<IRule> rules) {
        rules = rules.stream()
                .sorted(Comparator.comparingInt(IRule::getPriority))
                .toList();
        return new Referee(rules);
    }

    /**
     * Checks whether placing a tile at the given position is a valid move according to the rules.
     * @param board The board on which the move is made.
     * @param tile The tile to place.
     * @param x The x coordinate of the target cell.
     * @param y The y coordinate of the target cell.
     * @return True if the move satisfies the rules, false otherwise.
     */
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

    /**
     * Checks all valid move position for a tile according to the rules.
     * @param board The board on which the move is made.
     * @param tile The tile to place.
     * @return The list of valid positions
     */
    public List<BoardPosition> getValidPositions(Board board, Tile tile) {
        List<BoardPosition> validPositions = new ArrayList<>();

        for (int x = 0; x < Board.GRID_SIZE; x++) {
            for (int y = 0; y < Board.GRID_SIZE; y++) {
                if (isValidMove(board, tile, x, y)) {
                    validPositions.add(new BoardPosition(x, y));
                }
            }
        }

        return validPositions;
    }

    /**
     * Calculates the score earned by placing a tile at the given position based on matching neighbors and sun bonus.
     * @param board The board on which the move is made.
     * @param tile The tile being placed.
     * @param x The x coordinate of the target cell.
     * @param y The y coordinate of the target cell.
     * @return The score earned for the placement.
     */
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

    /**
     * Determines the winning player based on the number of tiles played.
     * @param game The game whose players are compared.
     * @return The player who played more tiles, or null if it is a tie.
     */
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

    /**
     * Checks whether the game is over, either by reaching the maximum number of turns or by a player having no tiles left.
     * @param game The game to check.
     * @return True if the game is over, false otherwise.
     */
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
