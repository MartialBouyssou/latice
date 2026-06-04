package latice.model.board.cell;

import latice.model.tile.Tile;

public class NormalCell extends Cell {
        /**
         * Creates a normal cell holding the given tile.
         * @param tile The tile placed on this cell.
         */
        public NormalCell(Tile tile) {
            super(tile);
        }

        /**
         * Creates an empty normal cell with no tile.
         */
        public NormalCell() {
            super();
        }
}
