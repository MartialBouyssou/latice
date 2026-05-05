package latice.model.game;

import latice.model.board.Board;
import latice.model.deck.Deck;
import latice.model.player.Player;
import latice.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class Game {
    List<Player> players;
    Board board;
    // Referee referee;
    Player currentPlayer;
    int turnCount = 0;

    public Game(List<Player> players, Board board/*, Referee referee*/) {
    }

    public void startGame() {

        Board board = Board.create();
        Deck deckFull = Deck.create();
        List<Tile> firstPart = new ArrayList<>();
        List<Tile> secondPart = new ArrayList<>();
        List<Tile> tiles1 = deckFull.getTiles();

        for (int i = 0; i < deckFull.size()/2; i++) {
            firstPart.add(tiles1.get(i*2));
            secondPart.add(tiles1.get(i*2+1));
        }

        players.add(Player.create("P1", Deck.create(firstPart) ));
        players.add(Player.create("P2", Deck.create(secondPart) ));
    };

    public void nextTurn() {
        turnCount++;
        if (currentPlayer == players.get(0)) {
            currentPlayer = players.get(1);
        } else {
            currentPlayer = players.get(0);
        }
    };

    public void endTurn() {
        //TODO
    };

    public boolean isGameOver() {;
        if (turnCount == 10) {
            return true;
        };
        if (players.get(0).getDeck().isEmpty() || players.get(1).getDeck().isEmpty()){
            return true;
        }
        return false;
    };

    public Player GetWinner() {
        if (players.get(0).getDeck().size() > players.get(1).getDeck().size()) {
            return players.get(1);
        } else if (players.get(0).getDeck().size() < players.get(1).getDeck().size()) {
            return players.get(2);
        } else {
            System.out.println("Match Draw");
            return null;
        }
    };
}
