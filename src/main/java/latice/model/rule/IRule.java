package latice.model.rule;

import latice.model.board.Board;
import latice.model.tile.Tile;

public interface IRule {
    boolean validate(Board board, Tile tile, int x, int y);
}
