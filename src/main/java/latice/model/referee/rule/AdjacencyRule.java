package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.tile.Tile;

import java.util.List;

public class AdjacencyRule implements IRule {
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
}
