package latice.model.ai;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.board.cell.MoonCell;
import latice.model.board.cell.NormalCell;
import latice.model.board.cell.SunCell;
import latice.model.tile.Tile;

public class BoardSimulator {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private BoardSimulator() {
    }

    /**
     * Creates a deep copy of the given board, preserving cell types and already placed tiles.
     * The copy is fully independent: placing a tile on the copy will not affect the original board.
     *
     * @param original The original game board to copy.
     * @return A new {@link Board} instance with the same state as the original.
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
     * Clones a single cell, preserving its exact type (Sun, Moon, or Normal)
     * as well as the tile it may contain.
     *
     * @param cell The cell to clone.
     * @return A new {@link Cell} instance of the same type, containing the same tile.
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
     * Builds a {@link Board} instance from a pre-filled grid of cells.
     * Uses {@code Board.create()} followed by manual tile placement to stay compatible
     * with the existing Board API (which does not expose a public constructor taking a grid).
     *
     * @param grid The two-dimensional array representing the cell grid.
     * @return A new board configured according to the provided grid.
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