package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.tile.Tile;

import static latice.application.LaticeApplicationConsole.message;

public class PositionRule implements IRule {
    @Override
    public boolean validate(Board board, Tile tile, int x, int y) {
        return board.isInside(x, y) && board.getCell(x, y).isEmpty();
    }

    @Override
    public int getPriority() {
        return -1;
    }
}