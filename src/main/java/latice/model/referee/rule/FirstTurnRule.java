package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.tile.Tile;

public class FirstTurnRule implements IRule {

    /**
     * Evaluates if the proposed placement for the first turn is exactly on the designated moon cell.
     *
     * @param board The current game board.
     * @param tile The tile intended to be placed.
     * @param x The target X coordinate on the grid.
     * @param y The target Y coordinate on the grid.
     *
     * @return True if the coordinates match the moon cell's location, false otherwise.
     */
    @Override
    public boolean validate(Board board, Tile tile, int x, int y) {
        int moonCellX = Board.MOON_CELL[0];
        int moonCellY = Board.MOON_CELL[1];
        return x == moonCellX && y == moonCellY;
    }

    /**
     * Retrieves the execution priority level of this specific rule.
     *
     * @return The integer priority value for the first turn rule (0).
     */
    @Override
    public int getPriority() {
        return 0;
    }
}