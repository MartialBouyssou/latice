package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.tile.Tile;

public class PositionRule implements IRule {

    /**
     * Evaluates if the target placement coordinates are within the board's boundaries
     * and the target cell is currently empty.
     *
     * @param board The current game board.
     * @param tile The tile intended to be placed.
     * @param x The target X coordinate on the grid.
     * @param y The target Y coordinate on the grid.
     *
     * @return True if the position is inside the board limits and is empty, false otherwise.
     */
    @Override
    public boolean validate(Board board, Tile tile, int x, int y) {
        return board.isInside(x, y) && board.getCell(x, y).isEmpty();
    }

    /**
     * Retrieves the execution priority level of this specific rule.
     *
     * @return The integer priority value for the position rule (-1).
     */
    @Override
    public int getPriority() {
        return -1;
    }
}