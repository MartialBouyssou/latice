package latice.model.game;

import latice.model.board.Board;
import latice.model.deck.Deck;
import latice.model.player.Player;
import latice.model.referee.Referee;
import latice.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private Board board;
    private final Referee referee;
    private Player currentPlayer;
    private int turnCount = 0;
    private final Player firstPlayer = players.get(0);
    private final Player secondPlayer = players.get(1);

    public Game(Referee referee) {
        this.referee = referee;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void startGame() {
        board = Board.create();
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

        currentPlayer = firstPlayer;
    }

    public void nextTurn() {
        turnCount++;
        if (currentPlayer == firstPlayer) {
            currentPlayer = secondPlayer;
        } else {
            currentPlayer = firstPlayer;
        }
    }

    public void endTurn() {
        //TODO: implement end turn logic (ex check if game is over, etc..)
    }

    public boolean isGameOver() {
        return referee.isGameOver(this);
    }

    public Player getWinner() {
        int firstPlayerTileCount = firstPlayer.getDeck().size() + firstPlayer.getRack().size();
        int secondPlayerTileCount = secondPlayer.getDeck().size() + secondPlayer.getRack().size();

        if (firstPlayerTileCount > secondPlayerTileCount) {
            return firstPlayer;
        } else if (firstPlayerTileCount < secondPlayerTileCount) {
            return secondPlayer;
        } else {
            // TODO: implement Draw when the 2 players have the same amount of tiles
            return Player.create("Draw : no one won", players.get(0).getDeck());
        }
    }
}
