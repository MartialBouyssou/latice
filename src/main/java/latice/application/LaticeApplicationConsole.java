package latice.application;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.board.cell.MoonCell;
import latice.model.board.cell.SunCell;

public class LaticeApplicationConsole {
    /**
     * Prevents instantiation of this utility class.
     */
    private LaticeApplicationConsole() {
    }

    /**
     * Prints the welcome header banner of the game to the console.
     */
    public static void displayHeader() {
        System.out.println("---------------------------------------------------");
        System.out.println("-- Bienvenue dans notre magnifique jeu Latice !  --");
        System.out.println("-- développé par Gabriel, Samuel et Martial      --");
        System.out.println("---------------------------------------------------\n");
    }
    
    /**
     * Prints a simple message to the console.
     * @param texte The text to display.
     */
    public static void message(String texte) {
    	System.out.println(texte);
    }

    /**
     * Prints a title framed by separator lines to the console.
     * @param texte The title text to display.
     */
    public static void titre(String texte) {
    	System.out.println("-------------------------------------");
    	System.out.println(texte);
    	System.out.println("-------------------------------------");
    }

    /**
     * Prints a text representation of the board to the console, using
     * 'M' for moon cells, '*' for sun cells, and '.' for other cells.
     * @param board The board to display.
     */
    public static void displayBoard(Board board) {
        System.out.println("  0 1 2 3 4 5 6 7 8");
        for (int i = 0; i < 9; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 9; j++) {
                Cell cell = board.getCell(i, j);
                if (cell instanceof MoonCell) {
                    System.out.print("M ");
                } else if (cell instanceof SunCell) {
                    System.out.print("* ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

}
