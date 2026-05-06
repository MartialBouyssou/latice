package latice.model.cell;

import latice.model.tile.Tile;
public abstract class Cell {
    private Tile tile = null;

    Cell( ){
        tile = null;
    }

    public boolean isEmpty(){
        return tile == null;
    };

    public Tile setTile(Tile tile){
        this.tile = tile;
        return tile;
    };

    public Tile getTile(){
        return tile;
    };

}