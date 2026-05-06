package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.cell.Cell;
import latice.model.tile.Tile;

import java.util.List;

public class MatchingRule implements IRule {
    @Override
    public boolean validate(Board board, Tile tile, int x, int y) {
        List<Cell> neighbors = board.getNeighbors(x, y);

        for (Cell neighborCell : neighbors) {
            if (!neighborCell.isEmpty()) {
                Tile neighbor = neighborCell.getTile();

                boolean sameColor = neighbor.getColor() == tile.getColor();
                boolean sameShape = neighbor.getShape() == tile.getShape();

                if (!sameColor && !sameShape) {
                    return false;
                }
            }
        }
        return true;
    }
}
