package latice.model.ai;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.board.cell.MoonCell;
import latice.model.board.cell.NormalCell;
import latice.model.board.cell.SunCell;
import latice.model.tile.Tile;


public class BoardSimulator {

    private BoardSimulator() {
    }

    /**
     * Creates a deep copy of the given board, preserving cell types and placed tiles.
     * The copy is independent: placing a tile on it does not affect the original board.
     *
     * @param original The board to copy.
     * @return A new Board instance with the same state as the original.
     */
    public static Board deepCopy(Board original) {
        Cell[][] copiedGrid = new Cell[Board.GRID_SIZE][Board.GRID_SIZE];

        for (int x = 0; x < Board.GRID_SIZE; x++) {
            for (int y = 0; y < Board.GRID_SIZE; y++) {
                Cell originalCell = original.getCell(x, y);
                Cell copiedCell = cloneCell(originalCell);
                copiedGrid[x][y] = copiedCell;
            }
        }

        return BoardSimulator.fromGrid(copiedGrid);
    }

    /**
     * Clones a single cell, preserving its type (Sun/Moon/Normal) and its tile if any.
     *
     * @param cell The cell to clone.
     * @return A new Cell of the same type holding the same tile reference.
     */
    private static Cell cloneCell(Cell cell) {
        Tile tile = cell.isEmpty() ? null : cell.getTile();

        if (cell instanceof SunCell) {
            return tile != null ? new SunCell(tile) : new SunCell();
        } else if (cell instanceof MoonCell) {
            return tile != null ? new MoonCell(tile) : new MoonCell();
        } else {
            return tile != null ? new NormalCell(tile) : new NormalCell();
        }
    }

    /**
     * Builds a Board instance from a pre-filled grid of cells using package-visible reflection.
     * Falls back to Board.create() + manual tile placement to stay compatible with the
     * existing Board API (no public grid constructor is exposed).
     *
     * @param grid The grid to wrap in a Board.
     * @return A Board whose state matches the provided grid.
     */
    private static Board fromGrid(Cell[][] grid) {
        Board board = Board.create();
        for (int x = 0; x < Board.GRID_SIZE; x++) {
            for (int y = 0; y < Board.GRID_SIZE; y++) {
                if (!grid[x][y].isEmpty()) {
                    board.placeTile(grid[x][y].getTile(), x, y);
                }
            }
        }
        return board;
    }
}
