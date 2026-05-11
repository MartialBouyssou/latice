package latice.application;

import latice.model.board.Board;
import latice.model.board.cell.Cell;
import latice.model.board.cell.MoonCell;
import latice.model.board.cell.SunCell;

public class LaticeApplicationConsole {
    private LaticeApplicationConsole() {
    }

    public static void displayHeader() {
        System.out.println("---------------------------------------------------");
        System.out.println("-- Bienvenue dans notre magnifique jeu Latice !  --");
        System.out.println("-- développé par Gabriel, Samuel et Martial      --");
        System.out.println("---------------------------------------------------\n");
    }
    
    public static void message(String texte) {
    	System.out.println(texte);
    }
    
    public static void titre(String texte) {
    	System.out.println("-------------------------------------");
    	System.out.println(texte);
    	System.out.println("-------------------------------------");
    }

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
