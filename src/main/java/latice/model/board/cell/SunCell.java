package latice.model.board.cell;

import latice.model.tile.Tile;

public class SunCell extends Cell {
    /**
     * Creates a sun cell holding the given tile.
     * @param tile The tile placed on this cell.
     */
    public SunCell(Tile tile) {
        super(tile);
    }

    /**
     * Creates an empty sun cell with no tile.
     */
    public SunCell() {
        super();
    }
}
