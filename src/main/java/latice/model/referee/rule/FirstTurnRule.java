package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.tile.Tile;

public class FirstTurnRule implements IRule {
    @Override
    public boolean validate(Board board, Tile tile, int x, int y) {
        int moonCellX = Board.MOON_CELL[0];
        int moonCellY = Board.MOON_CELL[1];
        return x == moonCellX && y == moonCellY;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
