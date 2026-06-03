package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.tile.Tile;

import java.util.List;

public class MatchingRule implements IRule {

    /**
     * Evaluates if the proposed tile matches the color or shape of each adjacent tile.
     *
     * @param board The current game board.
     * @param tile The tile intended to be placed.
     * @param x The target X coordinate on the grid.
     * @param y The target Y coordinate on the grid.
     *
     * @return True if every occupied neighbor shares a color or shape with the tile.
     */
    @Override
    public boolean validate(Board board, Tile tile, int x, int y) {
        boolean valid = true;
        List<Cell> neighbors = board.getNeighbors(x, y);

        for (Cell neighborCell : neighbors) {
            if (!neighborCell.isEmpty()) {
                Tile neighbor = neighborCell.getTile();
                boolean sameColor = neighbor.getColor() == tile.getColor();
                boolean sameShape = neighbor.getShape() == tile.getShape();

                if (!sameColor && !sameShape) {
                    valid = false;
                }
            }
        }
        return valid;
    }

    /**
     * Retrieves the execution priority level of this specific rule.
     *
     * @return The integer priority value for the matching rule (1).
     */
    @Override
    public int getPriority() {
        return 1;
    }
}
