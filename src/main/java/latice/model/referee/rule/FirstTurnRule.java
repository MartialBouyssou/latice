package latice.model.referee.rule;

import latice.model.board.Board;
import latice.model.tile.Tile;

import static latice.application.LaticeApplicationConsole.message;

public class FirstTurnRule implements IRule {
    @Override
    public boolean validate(Board board, Tile tile, int x, int y) {
        int moonCellX = Board.MOON_CELL[0];
        int moonCellY = Board.MOON_CELL[1];

        if (x != moonCellX || y != moonCellY) {
            message("hello boy");
            return false;
        }
        return true;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}