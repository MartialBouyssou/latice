package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.tile.Tile;

import java.util.List;

public class AdjacencyRule implements IRule {

    /**
     * Evaluates if the proposed placement is adjacent to at least one existing tile on the board.
     *
     * @param board The current game board.
     * @param tile The tile intended to be placed.
     * @param x The target X coordinate on the grid.
     * @param y The target Y coordinate on the grid.
     *
     * @return True if there is at least one non-empty neighboring cell, false otherwise.
     */
    @Override
    public boolean validate(Board board, Tile tile, int x, int y) {
        List<Cell> neighbors = board.getNeighbors(x, y);
        if (neighbors.isEmpty()) {
            return false;
        }

        for (Cell neighborCell : neighbors) {
            if (!neighborCell.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the execution priority level of this specific rule.
     *
     * @return The integer priority value for the adjacency rule (1).
     */
    @Override
    public int getPriority() {
        return 1;
    }
}