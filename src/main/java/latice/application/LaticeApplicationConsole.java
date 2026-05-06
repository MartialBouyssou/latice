package latice.application;

import latice.model.board.Board;

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
}


