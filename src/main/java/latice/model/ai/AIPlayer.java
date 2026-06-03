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
 * Represents an AI player that evaluates sequences of moves to maximize points and placements.
 */
public class AIPlayer {

    private final Referee referee;

    /**
     * Creates an AI player with the given referee.
     * @param referee The referee used to validate moves and compute scores.
     */
    public AIPlayer(Referee referee) {
        this.referee = referee;
    }

    /**
     * Finds the best move possible for the current board and rack.
     * @param board The current state of the game board.
     * @param rack The rack of the AI player.
     * @param currentScore The current score of the AI player.
     * @param turnsRemaining The number of turns left in the game.
     * @return The chosen AIMove, or null if no valid move exists.
     */
    public AIMove findBestMove(Board board, Rack rack, int currentScore, int turnsRemaining) {
        List<AIMove> validMoves = buildValidMoves(board, rack);
        if (validMoves.isEmpty()) {
            return null;
        }

        AIMove bestMove = null;
        int bestEval = Integer.MIN_VALUE;

        for (AIMove move : validMoves) {
            int eval = evaluateMove(board, rack, move, currentScore, 0, turnsRemaining);
            if (eval > bestEval) {
                bestEval = eval;
                bestMove = move;
            }
        }

        return bestMove != null ? bestMove : validMoves.get(0);
    }

    /**
     * Evaluates a move recursively by simulating the extra actions the AI could buy.
     * @param board The current state of the game board.
     * @param rack The rack of the AI player.
     * @param move The move to evaluate.
     * @param currentScore The current score of the AI player.
     * @param depth The current depth of the recursive evaluation.
     * @param turnsRemaining The number of turns left in the game.
     * @return The evaluated score of the move sequence.
     */
    private int evaluateMove(Board board, Rack rack, AIMove move, int currentScore, int depth, int turnsRemaining) {
        int score = move.getScore();
        int eval = score * 10;

        int newScore = currentScore + score;

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

        if (depth < 1 && newScore >= Referee.EXTRA_ACTION_COST) {
            Rack simRack = copyRackWithout(rack, move.getTile());
            if (!simRack.isEmpty()) {
                int nextScore = newScore - Referee.EXTRA_ACTION_COST;
                List<AIMove> nextMoves = buildValidMoves(simBoard, simRack);

                int bestNextEval = 0;
                for (AIMove nextMove : nextMoves) {
                    int nextEval = evaluateMove(simBoard, simRack, nextMove, nextScore, depth + 1, turnsRemaining);
                    if (nextEval > bestNextEval) {
                        bestNextEval = nextEval;
                    }
                }
                eval += bestNextEval + 20;
            }
        }

        return eval;
    }

    /**
     * Counts the number of empty sun cells that have at least one adjacent tile.
     * @param board The current state of the game board.
     * @return The number of open sun cells.
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
     * Builds a list of valid moves for the current rack on the given board.
     * @param board The current state of the game board.
     * @param rack The rack of the AI player.
     * @return A list of valid AIMoves.
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
     * Creates a copy of the rack without the first occurrence of the played tile.
     * @param original The original rack to copy.
     * @param played The tile that has just been played.
     * @return A new Rack with all tiles except the played one.
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