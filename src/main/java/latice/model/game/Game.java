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
    int turnCount;

    public Game(List<Player> players, Board board/*, Referee referee*/) {
        this.players = players;
        this.board = board;
        // this.referee = referee;
        this.currentPlayer = players.get(0);
        this.turnCount = 0;
    }

    public void startGame() {
        Board board = Board.create();
        Deck deckFull = Deck.create();
        List<Tile> firstPart1 = new ArrayList<>();
        List<Tile> secondPart1 = new ArrayList<>();
        List<Tile> tiles1 = deckFull.getTiles();

        for (int i = 0; i < deckFull.size()/2; i++) {
            firstPart1.add(tiles1.get(i*2));
            secondPart1.add(tiles1.get(i*2+1));
        }

        players.get(0).getDeck().create(firstPart1);
        players.get(1).getDeck().create(secondPart1);

        players.get(0).getRack().exchangeAll(players.get(0).getDeck());
        players.get(1).getRack().exchangeAll(players.get(1).getDeck());


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
