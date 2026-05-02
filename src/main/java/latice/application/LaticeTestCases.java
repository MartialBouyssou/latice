package latice.application;

import latice.model.deck.Deck;
import latice.model.player.Player;
import latice.model.tile.Tile;

import static latice.application.LaticeApplicationConsole.*;

import java.util.ArrayList;
import java.util.List;

public class LaticeTestCases {

    public static void main(String[] args) {
    	Deck deckFull = Deck.create();
    	
    	List<Tile> firstPart1 = new ArrayList<>();
    	List<Tile> secondPart1 = new ArrayList<>();
    	List<Tile> tiles1 = deckFull.getTiles();
    	
    	for (int i = 0; i < deckFull.size()/2; i++) {
    		firstPart1.add(tiles1.get(i*2));
    		secondPart1.add(tiles1.get(i*2+1));
    	}
    	
    	Deck deckP1 = Deck.create(firstPart1);
    	Deck deckP2 = Deck.create(secondPart1);
   
    	
    	message("Nombre Tuile P1 :" + deckP1.size());
    	message("Nombre Tuile P1 :" + deckP2.size());
    	
    	deckP1.shuffle();
    	deckP2.shuffle();
    	
    	Player player1 = Player.create("firstPlayer", deckP1);
    	Player player2 = Player.create("firstPlayer", deckP2);

    	message("Rack first player : " + player1.getRack());
    	
    	player1.getRack().exchangeAll(deckP1);
    	message("Nombre Tuile P1 :" + deckP1.size());
    	message("Rack first player : " + player1.getRack());
    	
   }
}