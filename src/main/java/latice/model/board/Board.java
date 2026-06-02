package latice.model.board;

import latice.model.board.cell.Cell;
import latice.model.board.cell.SunCell;
import latice.model.board.cell.MoonCell;
import latice.model.board.cell.NormalCell;
import latice.model.tile.Tile;
import latice.exception.model.InvalidPositionException;
import latice.exception.model.CellOccupiedException;
import latice.exception.model.InvalidTileException;

import java.util.List;
import java.util.ArrayList;

public class Board {
    public static final int GRID_SIZE = 9;
    public static final int[] MOON_CELL = {4, 4};

    private final Cell[][] grid;

    private static final int[][] SUN_CELLS = {
            {0, 0}, {1, 1}, {2, 2}, {8, 0}, {7, 1}, {6, 2},
            {8, 8}, {7, 7}, {6, 6}, {0, 8}, {1, 7}, {2, 6},
            {4, 0}, {0, 4}, {8, 4}, {4, 8}
    };

    /**
     * Creates a board with the given grid of cells.
     * @param grid The two-dimensional array of cells representing the board.
     */
    private Board(Cell[][] grid) {
        this.grid = grid;
    }

    /**
     * Creates a new board with all cells initialized as moon, sun, or normal cells.
     * @return A new board with its cells placed according to the game layout.
     */
    public static Board create() {
        Cell[][] grid = new Cell[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (i == MOON_CELL[0] && j == MOON_CELL[1]) {
                    grid[i][j] = new MoonCell();
                } else if (isSunCellAt(i, j)) {
                    grid[i][j] = new SunCell();
                } else {
                    grid[i][j] = new NormalCell();
                }
            }
        }

        return new Board(grid);
    }

    /**
     * Checks whether a sun cell is located at the given coordinates.
     * @param x The row coordinate to check.
     * @param y The column coordinate to check.
     * @return True if a sun cell is at the given coordinates, false otherwise.
     */
    private static boolean isSunCellAt(int x, int y) {
        for (int[] position : SUN_CELLS) {
            if (position[0] == x && position[1] == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Places a tile on the cell at the given coordinates.
     * @param tile The tile to place on the board.
     * @param x The row coordinate where the tile is placed.
     * @param y The column coordinate where the tile is placed.
     */
    public void placeTile(Tile tile, int x, int y) {
        if (tile == null) {
            throw new InvalidTileException();
        }
        if (!isInside(x, y)) {
            throw new InvalidPositionException();
        }
        if (!grid[x][y].isEmpty()) {
            throw new CellOccupiedException();
        }
        grid[x][y].setTile(tile);
    }

    /**
     * Returns the cell located at the given coordinates.
     * @param x The row coordinate of the cell.
     * @param y The column coordinate of the cell.
     * @return The cell at the given coordinates.
     */
    public Cell getCell(int x, int y) {
        if (!isInside(x, y)) {
            throw new InvalidPositionException();
        }
        return grid[x][y];
    }

    /**
     * Returns the orthogonal neighboring cells of the cell at the given coordinates.
     * @param x The row coordinate of the cell.
     * @param y The column coordinate of the cell.
     * @return The list of neighboring cells that are inside the board.
     */
    public List<Cell> getNeighbors(int x, int y) {
        if (!isInside(x, y)) {
            throw new InvalidPositionException();
        }
        List<Cell> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int neighborX = x + dir[0];
            int neighborY = y + dir[1];
            if (isInside(neighborX, neighborY)) {
                neighbors.add(grid[neighborX][neighborY]);
            }
        }
        return neighbors;
    }

    /**
     * Checks whether the given coordinates are within the board boundaries.
     * @param x The row coordinate to check.
     * @param y The column coordinate to check.
     * @return True if the coordinates are inside the board, false otherwise.
     */
    public boolean isInside(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }

    /**
     * Returns a textual representation of the board and its cells.
     * @return A string showing the grid with cell types and placed tiles.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Board ").append(GRID_SIZE).append("x").append(GRID_SIZE).append(":\n");
        sb.append(" ┌─────────────────┐\n");
        for (int i = 0; i < GRID_SIZE; i++) {
            sb.append("│");
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell cell = grid[i][j];
                if (cell.isEmpty()) {
                    if (cell instanceof MoonCell) {
                        sb.append("M ");
                    } else if (cell instanceof SunCell) {
                        sb.append("S ");
                    } else {
                        sb.append("N ");
                    }
                } else {
                    sb.append(cell.getTile().toEmoji()).append(" ");
                }
            }
            sb.append("│\n");
        }
        sb.append(" └─────────────────┘");
        return sb.toString();
    }
}
