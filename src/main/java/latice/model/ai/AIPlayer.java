package latice.model.ai;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.board.cell.SunCell;
import latice.model.player.Rack;
import latice.model.referee.Referee;
import latice.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the artificial intelligence of the game.
 * Evaluates sequences of moves to maximize points and avoid benefiting the opponent.
 */
public class AIPlayer {

    private final Referee referee;

    /**
     * Creates an AI player with the given referee.
     *
     * @param referee The referee used to validate moves and calculate scores.
     */
    public AIPlayer(Referee referee) {
        this.referee = referee;
    }

    /**
     * Simulates the future to determine whether buying an extra action is worthwhile
     * at the start of the turn, given the current point balance (before any tile placement).
     *
     * @param board           The current game board.
     * @param rack            The rack containing the AI's tiles.
     * @param currentScore    The AI's current score.
     * @param turnsRemaining  The number of turns remaining in the game.
     * @return {@code true} if buying an action yields more points or helps finish the game, {@code false} otherwise.
     */
    public boolean isDoublePlayWorthIt(Board board, Rack rack, int currentScore, int turnsRemaining) {
        if (currentScore < Referee.EXTRA_ACTION_COST || rack.getTiles().size() < 2) {
            return false;
        }

        AIMove firstMove = findBestMove(board, rack, currentScore, turnsRemaining);
        if (firstMove == null) return false;

        Board simBoard = BoardSimulator.deepCopy(board);
        simBoard.placeTile(firstMove.getTile(), firstMove.getX(), firstMove.getY());
        Rack simRack = copyRackWithout(rack, firstMove.getTile());

        AIMove secondMove = findBestMove(simBoard, simRack, currentScore - Referee.EXTRA_ACTION_COST + firstMove.getScore(), turnsRemaining);

        if (secondMove != null) {
            int totalScoreGain = firstMove.getScore() + secondMove.getScore();
            if (totalScoreGain >= 2 || turnsRemaining <= 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the best possible move for the current board and rack.
     *
     * @param board          The current game board.
     * @param rack           The AI's rack.
     * @param currentScore   The AI's current score.
     * @param turnsRemaining The number of turns remaining.
     * @return The {@link AIMove} representing the best move, or {@code null} if no move is possible.
     */
    public AIMove findBestMove(Board board, Rack rack, int currentScore, int turnsRemaining) {
        List<AIMove> validMoves = buildValidMoves(board, rack);
        if (validMoves.isEmpty()) {
            return null;
        }

        AIMove bestMove = null;
        int bestEval = Integer.MIN_VALUE;

        for (AIMove move : validMoves) {
            int eval = evaluateMove(board, rack, move, turnsRemaining);
            if (eval > bestEval) {
                bestEval = eval;
                bestMove = move;
            }
        }

        return bestMove != null ? bestMove : validMoves.get(0);
    }

    /**
     * Evaluates a specific move by assigning it a relevance score,
     * taking into account dangerous openings (e.g. offering a Sun cell to the opponent).
     *
     * @param board          The game board.
     * @param rack           The AI's rack.
     * @param move           The move to evaluate.
     * @param turnsRemaining The number of turns remaining.
     * @return The computed evaluation score.
     */
    private int evaluateMove(Board board, Rack rack, AIMove move, int turnsRemaining) {
        int score = move.getScore();
        int eval = score * 10;

        int openSunsBefore = countOpenSunCells(board);
        Board simBoard = BoardSimulator.deepCopy(board);
        simBoard.placeTile(move.getTile(), move.getX(), move.getY());

        if (turnsRemaining > 2) {
            int openSunsAfter = countOpenSunCells(simBoard);
            int sunsOpened = openSunsAfter - openSunsBefore;

            if (sunsOpened > 0) {
                if (score < 2) {
                    eval -= 10000;
                } else if (sunsOpened > 1) {
                    eval -= 10000;
                } else {
                    eval -= 5;
                }
            }
        }

        return eval;
    }

    /**
     * Counts the number of empty Sun cells that are adjacent to at least one placed tile.
     * These are dangerous cells because the opponent could easily play there.
     *
     * @param board The board to analyze.
     * @return The number of exposed Sun cells.
     */
    private int countOpenSunCells(Board board) {
        int count = 0;
        for (int x = 0; x < Board.GRID_SIZE; x++) {
            for (int y = 0; y < Board.GRID_SIZE; y++) {
                if (board.getCell(x, y) instanceof SunCell && board.getCell(x, y).isEmpty()) {
                    for (Cell n : board.getNeighbors(x, y)) {
                        if (!n.isEmpty()) {
                            count++;
                            break;
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Builds the list of all legal and valid moves possible with the current rack.
     *
     * @param board The game board.
     * @param rack  The rack containing the available tiles.
     * @return A list containing all valid {@link AIMove} objects.
     */
    private List<AIMove> buildValidMoves(Board board, Rack rack) {
        List<AIMove> moves = new ArrayList<>();
        for (Tile tile : rack.getTiles()) {
            for (int x = 0; x < Board.GRID_SIZE; x++) {
                for (int y = 0; y < Board.GRID_SIZE; y++) {
                    if (board.getCell(x, y).isEmpty() && referee.isValidMove(board, tile, x, y)) {
                        int score = referee.calculateScore(board, tile, x, y);
                        moves.add(new AIMove(tile, x, y, score));
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Creates an independent copy of a rack with the first occurrence of a specified tile removed.
     * Used for simulating successive moves.
     *
     * @param original The original rack.
     * @param played   The tile to remove.
     * @return The new simulated rack.
     */
    private Rack copyRackWithout(Rack original, Tile played) {
        Rack copy = new Rack();
        boolean removed = false;
        for (Tile tile : original.getTiles()) {
            if (!removed && tile.equals(played)) {
                removed = true;
            } else {
                copy.addTile(tile);
            }
        }
        return copy;
    }
}