package latice.model.board.cell;

import latice.model.tile.Tile;
public abstract class Cell {
    private Tile tile = null;

    /**
     * Creates a cell holding the given tile.
     * @param tile The tile placed on this cell.
     */
    Cell(Tile tile){
        this.tile = tile;
    }

    /**
     * Creates an empty cell with no tile.
     */
    Cell(){
        tile = null;
    }

    /**
     * Checks whether this cell has no tile.
     * @return True if the cell is empty, false otherwise.
     */
    public boolean isEmpty(){
        return tile == null;
    };

    /**
     * Places the given tile on this cell.
     * @param tile The tile to place on this cell.
     * @return The tile that was placed.
     */
    public Tile setTile(Tile tile){
        this.tile = tile;
        return tile;
    };

    /**
     * Returns the tile currently placed on this cell.
     * @return The tile on this cell, or null if the cell is empty.
     */
    public Tile getTile(){
        return tile;
    };

}