package latice.model.ai;

import latice.model.tile.Tile;

public class AIMove {

    private final Tile tile;
    private final int x;
    private final int y;
    private final int score;

    /**
     * Creates an AIMove with the given tile, coordinates and score.
     * @param tile The tile the AI wants to place.
     * @param x The column index on the board (0-8).
     * @param y The row index on the board (0-8).
     * @param score The points this placement would earn.
     */
    public AIMove(Tile tile, int x, int y, int score) {
        this.tile = tile;
        this.x = x;
        this.y = y;
        this.score = score;
    }

    /**
     * Returns the tile to place.
     * @return The tile.
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Returns the column index of the target cell.
     * @return The x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the row index of the target cell.
     * @return The y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the score this move would earn.
     * @return The score.
     */
    public int getScore() {
        return score;
    }
}
