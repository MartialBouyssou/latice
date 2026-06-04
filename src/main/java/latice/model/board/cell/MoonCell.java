package latice.model.board.cell;

import latice.model.tile.Tile;

public class MoonCell extends Cell {
    /**
     * Creates a moon cell holding the given tile.
     * @param tile The tile placed on this cell.
     */
    public MoonCell(Tile tile) {
        super(tile);
    }

    /**
     * Creates an empty moon cell with no tile.
     */
    public MoonCell() {
        super();
    }
}
