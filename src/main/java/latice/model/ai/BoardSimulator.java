package latice.model.ai;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.board.cell.MoonCell;
import latice.model.board.cell.NormalCell;
import latice.model.board.cell.SunCell;
import latice.model.tile.Tile;

public class BoardSimulator {

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     */
    private BoardSimulator() {
    }

    /**
     * Crée une copie profonde (deep copy) du plateau donné, en préservant les types de cellules
     * et les tuiles déjà placées. La copie est totalement indépendante : placer une tuile sur
     * cette copie n'affectera pas le plateau d'origine.
     *
     * @param original Le plateau de jeu original à copier.
     * @return Une nouvelle instance de {@link Board} avec le même état que l'original.
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
     * Clone une cellule unique en préservant sa nature exacte (Soleil, Lune ou Normale)
     * ainsi que la tuile qu'elle contient éventuellement.
     *
     * @param cell La cellule à cloner.
     * @return Une nouvelle instance de {@link Cell} du même type, contenant la même tuile.
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
     * Construit une instance de {@link Board} à partir d'une grille de cellules pré-remplie.
     * Utilise {@code Board.create()} puis un placement manuel des tuiles pour rester compatible
     * avec l'API existante de Board (qui n'expose pas de constructeur public prenant une grille).
     *
     * @param grid Le tableau à deux dimensions représentant la grille de cellules.
     * @return Un nouveau plateau configuré selon la grille fournie.
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