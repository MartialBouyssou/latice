package latice.model.board;

import latice.model.cell.Cell;
import latice.model.cell.SunCell;
import latice.model.cell.MoonCell;
import latice.model.cell.NormalCell;
import latice.model.tile.Tile;
import latice.model.tile.Shape;
import latice.model.tile.Color;
import latice.exception.model.InvalidPositionException;
import latice.exception.model.CellOccupiedException;
import latice.exception.model.InvalidTileException;
import java.util.List;
import java.util.ArrayList;

public class Board {

    private Cell[][] grid = null;

    private Board(Cell[][] grid){
        this.grid = grid;
    }

    public static Board create() {
        Cell[][] grid = new Cell[9][9];

        int[][] sunPositions = {
            {0, 0}, {1, 1}, {2, 2}, {8, 0}, {7, 1}, {6, 2},
            {8, 8}, {7, 7}, {6, 6}, {0, 8}, {1, 7}, {2, 6},
            {4, 0}, {0, 4}, {8, 4}, {4, 8}
        };

        int[] moonPosition = {4, 4};

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Tile tile = new Tile(Shape.FEATHER, Color.YELLOW);

                if (i == moonPosition[0] && j == moonPosition[1]) {
                    grid[i][j] = new MoonCell(tile);
                } else if (isSunPosition(i, j, sunPositions)) {
                    grid[i][j] = new SunCell(tile);
                } else {
                    grid[i][j] = new NormalCell(tile);
                }
            }
        }

        return new Board(grid);
    }

    private static boolean isSunPosition(int x, int y, int[][] sunPositions) {
        for (int[] position : sunPositions) {
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
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // up, down, left, right
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (isInside(nx, ny)) {
                neighbors.add(grid[nx][ny]);
            }
        }
        return neighbors;
    }

    public boolean isEmpty() {
        return grid == null;
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 9;
    }
}
