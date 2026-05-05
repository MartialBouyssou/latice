package latice.model.board;

import latice.model.cell.Cell;
import latice.model.cell.SunCell;
import latice.model.cell.MoonCell;
import latice.model.cell.NormalCell;
import latice.model.tile.Tile;
import latice.model.tile.Shape;
import latice.model.tile.Color;
import java.util.List;

public class Board {

    private Cell[][] grid = null;

    private Board(Cell[][] grid){
        this.grid = grid ;
    };

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
    };

    private static boolean isSunPosition(int x, int y, int[][] sunPositions) {
        for (int[] position : sunPositions) {
            if (position[0] == x && position[1] == y) {
                return true;
            }
        }
        return false;
    };

    public void placeTile(Tile tile, int x, int y) {
        grid[x][y].setTile(tile);
    };

    public Cell getCell(int x, int y) {
        return null;
    };

    public List<Cell> getNeighbors(int x, int y) {
        return null;
    };

    public boolean isEmpty() {
        return grid == null;
    };

    public boolean isInside(int x, int y) {
        return grid[x][y] == null;
    };

}
