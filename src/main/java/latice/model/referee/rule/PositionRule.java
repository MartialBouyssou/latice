package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.tile.Tile;

public class PositionRule implements IRule {
    @Override
    public boolean validate(Board board, Tile tile, int x, int y) {
        return board.isInside(x, y) && board.getCell(x, y).isEmpty();
    }
}