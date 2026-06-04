package latice.view;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import latice.model.board.Board;
import latice.model.board.BoardPosition;
import latice.model.board.BoardMoveHint;
import latice.model.board.cell.SunCell;
import latice.model.tile.Tile;
import latice.utils.theme.ThemeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoardView {
    private static final int BOARD_CENTER_INDEX = 4;
    public static final String HIGHLIGHTS_CELL_CSS = "valid-move-cell";

    private final List<Node> highlightedCells = new ArrayList<>();
    private final List<Node> scoreHintNodes = new ArrayList<>();
    private final GridPane boardGrid;

    /**
     * Creates a board view
     *
     * @param boardGrid grid
     */
    public BoardView(GridPane boardGrid) {
        this.boardGrid = Objects.requireNonNull(boardGrid);
    }

    /**
     * Resets all board cells to their themed background image
     *
     * @param board The board model
     */
    public void reset(Board board) {
        clearHighlights();

        for (Node node : boardGrid.getChildren()) {
            if (node instanceof ImageView imageView) {
                int x = getColumn(node);
                int y = getRow(node);
                imageView.setImage(new Image(getBackgroundPath(board, x, y)));
                imageView.setVisible(true);
            }
        }
    }

    /**
     * Places the given tile image in the right board cell
     *
     * @param tile The tile to display
     * @param targetX The board column
     * @param targetY The board row
     */
    public void placeTile(Tile tile, int targetX, int targetY) {
        for (Node node : boardGrid.getChildren()) {
            int x = getColumn(node);
            int y = getRow(node);

            if (x == targetX && y == targetY && node instanceof ImageView imageNode) {
                imageNode.setImage(new Image(tile.getTileImgPath()));
            }
        }
    }

    /**
     * Highlights a playable board cell without displaying a score preview.
     *
     * @param x The board column to highlight.
     * @param y The board row to highlight.
     */
    private void highlightCell(int x, int y) {
        Node cell = getCellNode(x, y);

        if (cell != null) {
            if (!cell.getStyleClass().contains(HIGHLIGHTS_CELL_CSS)) {
                cell.getStyleClass().add(HIGHLIGHTS_CELL_CSS);
                highlightedCells.add(cell);
            }
        }
    }

    /**
     * Highlights a playable board cell and displays the score preview on top of it.
     *
     * @param x The board column to highlight.
     * @param y The board row to highlight.
     * @param score The score earned by placing the selected tile on this cell.
     */
    private void highlightCell(int x, int y, int score) {
        Node cell = getCellNode(x, y);

        if (cell != null) {
            if (!cell.getStyleClass().contains(HIGHLIGHTS_CELL_CSS)) {
                cell.getStyleClass().add(HIGHLIGHTS_CELL_CSS);
                highlightedCells.add(cell);
            }

            Label scoreLabel = new Label("+" + score);
            scoreLabel.getStyleClass().add("score-hint");
            scoreLabel.setMouseTransparent(true);

            GridPane.setColumnIndex(scoreLabel, x);
            GridPane.setRowIndex(scoreLabel, y);
            GridPane.setHalignment(scoreLabel, HPos.CENTER);
            GridPane.setValignment(scoreLabel, VPos.CENTER);

            boardGrid.getChildren().add(scoreLabel);
            scoreHintNodes.add(scoreLabel);
        }
    }

    public void clearHighlights() {
        for (Node cell : highlightedCells) {
            cell.getStyleClass().remove(HIGHLIGHTS_CELL_CSS);
        }
        boardGrid.getChildren().removeAll(scoreHintNodes);

        scoreHintNodes.clear();
        highlightedCells.clear();
    }

    /**
     * Highlight the given cell position.
     * @param positions The cell to highlight position
     */
    public void highlightCells(List<BoardPosition> positions) {
        clearHighlights();

        for (BoardPosition position : positions) {
            highlightCell(position.x(), position.y());
        }
    }

    /**
     * Highlights the given valid board positions and displays the score preview for each one.
     * @param moveHints The valid move hints to display.
     */
    public void highlightCellsWithScores(List<BoardMoveHint> moveHints) {
        clearHighlights();

        for (BoardMoveHint moveHint : moveHints) {
            highlightCell(moveHint.x(), moveHint.y(), moveHint.score());
        }
    }

    /**
     * Get the absolute URL string of the background image for the cell at the given coordinates
     * @param board The game board
     * @param x the tile x positio,
     * @param y The tile y position
     * @return the path
     */
    private String getBackgroundPath(Board board, int x, int y) {
        String backgroundFile = getBackgroundFile(board, x, y);
        return Objects.requireNonNull(
                getClass().getResource("/" +
                        ThemeManager.getInstance().getBoardBgPath(backgroundFile))
        ).toExternalForm();
    }

    /**
     * Determines the filename of the background image of a board cell
     * @param board the game board used to check the cell types
     * @param x horizontal coordinate (column) of the cell
     * @param y vertical coordinate (row) of the cell
     * @return The filename of the background image
     */
    private String getBackgroundFile(Board board, int x, int y) {
        String backgroundFile = "bg_sea.png";

        if (x == BOARD_CENTER_INDEX && y == BOARD_CENTER_INDEX) {
            backgroundFile = "bg_moon.png";
        } else if (board.getCell(x, y) instanceof SunCell) {
            backgroundFile = "bg_sun.png";
        }

        return backgroundFile;
    }

    /**
     * Get the Node of a give position
     * @param x position of the node
     * @param y position of the node
     * @return the wanted node
     */
    private Node getCellNode(int x, int y) {
        for (Node node : boardGrid.getChildren()) {
            Integer column = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);

            int nodeX = 0;
            int nodeY = 0;
            if (column != null) {
                nodeX = column;
            }
            if (row != null) {
                nodeY = row;
            }

            if (node instanceof ImageView && nodeX == x && nodeY == y) {
                return node;
            }
        }

        return null;
    }

    /**
     * Determine the column index of the node in the grid pane
     * @param node The node to get the column index of
     * @return The column index
     */
    private int getColumn(Node node) {
        Integer columnIndex = GridPane.getColumnIndex(node);
        if (columnIndex == null) {
            return 0;
        }
        return columnIndex;
    }

    /**
     * Determine the row index of the node in the grid pane
     * @param node The node to get the row index of
     * @return The row index
     */
    private int getRow(Node node) {
        Integer rowIndex = GridPane.getRowIndex(node);
        if (rowIndex == null) {
            return 0;
        }
        return rowIndex;
    }
}
