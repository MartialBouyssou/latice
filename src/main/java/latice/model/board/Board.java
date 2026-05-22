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

    private Cell[][] grid = null;
    private static final int[][] sunCells = {
            {0, 0}, {1, 1}, {2, 2}, {8, 0}, {7, 1}, {6, 2},
            {8, 8}, {7, 7}, {6, 6}, {0, 8}, {1, 7}, {2, 6},
            {4, 0}, {0, 4}, {8, 4}, {4, 8}
    };

    public static final int[] MOON_CELL = {4, 4};

    private Board(Cell[][] grid) {
        this.grid = grid;
    }

    public static Board create() {
        Cell[][] grid = new Cell[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
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

    private static boolean isSunCellAt(int x, int y) {
        for (int[] position : Board.sunCells) {
            if (position[0] == x && position[1] == y) {
                return true;
            }
        }
        return false;
    }

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

    public Cell getCell(int x, int y) {
        if (!isInside(x, y)) {
            throw new InvalidPositionException();
        }
        return grid[x][y];
    }

    public List<Cell> getNeighbors(int x, int y) {
        if (!isInside(x, y)) {
            throw new InvalidPositionException();
        }
        List<Cell> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int neighbordX = x + dir[0];
            int neighbordY = y + dir[1];
            if (isInside(neighbordX, neighbordY)) {
                neighbors.add(grid[neighbordX][neighbordY]);
            }
        }
        return neighbors;
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 9;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Board 9x9:\n");
        sb.append(" ┌─────────────────┐\n");
        for (int i = 0; i < 9; i++) {
            sb.append("│");
            for (int j = 0; j < 9; j++) {
                if (isSunCellAt(i, j)) {
                    if (grid[i][j].isEmpty()) {
                        sb.append("S ");
                    } else {
                        sb.append(grid[i][j].getTile().toEmoji()).append(" ");
                    }
                } else if (i == MOON_CELL[0] && j == MOON_CELL[1]) {
                    if (grid[i][j].isEmpty()) {
                        sb.append("M ");
                    } else {
                        sb.append(grid[i][j].getTile().toEmoji()).append(" ");
                    }
                } else {
                    if (grid[i][j].isEmpty()) {
                        sb.append("N ");
                    } else {
                        sb.append(grid[i][j].getTile().toEmoji()).append(" ");
                    }
                }
            }
            sb.append("│\n");
        }
        sb.append(" └─────────────────┘");
        return sb.toString();
    }
}