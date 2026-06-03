package latice.model.ai;

import latice.model.board.Board;
import latice.model.player.Rack;
import latice.model.referee.Referee;
import latice.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AIPlayer {

    private final Referee referee;
    private final AIDifficulty difficulty;
    private final Random random;

    /**
     * Creates an AI player with the given referee and difficulty.
     * @param referee The referee used to validate moves and compute scores.
     * @param difficulty The strategy used to select among valid moves.
     */
    public AIPlayer(Referee referee, AIDifficulty difficulty) {
        this.referee = referee;
        this.difficulty = difficulty;
        this.random = new Random();
    }

    /**
     * Returns the difficulty level of this AI player.
     * @return The difficulty set at construction time.
     */
    public AIDifficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Scans every tile in the rack against every cell on the board and returns a move.
     * Returns null if no valid placement exists.
     * In EASY mode, returns a random valid move.
     * In HARD mode, returns the valid move with the highest score.
     * @param board The current state of the game board.
     * @param rack The rack of the player whose turn it is.
     * @return The chosen AIMove, or null if no valid move exists.
     */
    public AIMove findBestMove(Board board, Rack rack) {
        List<AIMove> validMoves = new ArrayList<>();

        for (Tile tile : rack.getTiles()) {
            for (int x = 0; x < Board.GRID_SIZE; x++) {
                for (int y = 0; y < Board.GRID_SIZE; y++) {
                    if (board.getCell(x, y).isEmpty()
                            && referee.isValidMove(board, tile, x, y)) {
                        int score = referee.calculateScore(board, tile, x, y);
                        validMoves.add(new AIMove(tile, x, y, score));
                    }
                }
            }
        }

        if (validMoves.isEmpty()) {
            return null;
        }

        if (difficulty == AIDifficulty.EASY) {
            return validMoves.get(random.nextInt(validMoves.size()));
        }

        AIMove best = validMoves.get(0);
        for (AIMove move : validMoves) {
            if (move.getScore() > best.getScore()) {
                best = move;
            }
        }
        return best;
    }
}
