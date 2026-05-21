package latice.model.game;

import latice.exception.model.GameNotStartException;
import latice.model.board.Board;
import latice.model.deck.Deck;
import latice.model.game.events.*;
import latice.model.player.Player;
import latice.model.referee.Referee;
import latice.model.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    public static final int MAX_TURN_COUNT = 10;
    private final List<Player> players;
    private Board board;
    private final Referee referee;
    private Player currentPlayer;
    private int turnCount;
    private final List<GameListener> listeners = new ArrayList<>();
    private int playersTurnScore;

    public Game(Referee referee) {
        this.referee = referee;
        this.players = new ArrayList<>();
        this.turnCount = 0;
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

    public Referee getReferee() {
        return referee;
    }

    // V4
    public void startGame() {
        board = Board.create();

        Deck fullDeck = Deck.create();
        List<Tile> allTiles = fullDeck.getTiles();

        List<Tile> firstHalf = new ArrayList<>();
        List<Tile> secondHalf = new ArrayList<>();

        for (int i = 0; i < allTiles.size() / 2; i++) {
            firstHalf.add(allTiles.get(i * 2));
            secondHalf.add(allTiles.get(i * 2 + 1));
        }

        players.add(Player.create("P1", Deck.create(firstHalf)));
        players.add(Player.create("P2", Deck.create(secondHalf)));

        currentPlayer = players.get(new Random().nextInt(players.size()));

        sendEvent(new GameStartedEvent(currentPlayer));
    }

    public void playTile(Tile tile, int x, int y) {
        if (currentPlayer == null) {
            throw new GameNotStartException();
        }

        if (!currentPlayer.getRack().getTiles().contains(tile)) {
            sendEvent(new InvalidMoveEvent(currentPlayer, tile, x, y));
            return;
        }

        playersTurnScore = referee.calculateScore(this.getBoard(), tile, x, y);

        currentPlayer.getRack().getTiles().remove(tile);
        currentPlayer.incrementTilesPlayed();
        currentPlayer.addScore(playersTurnScore);

        board.getCell(x, y).setTile(tile);

        sendEvent(new TilePlacedEvent(currentPlayer, tile, x, y, playersTurnScore));
    }

    public void endTurn() {
        if (currentPlayer == null) {
            throw new GameNotStartException();
        }
        currentPlayer.getRack().refill(currentPlayer.getDeck());

        Player lastPlayer = currentPlayer;
        nextPlayer();

        if (referee.isGameOver(this)) {
            sendEvent(new GameOverEvent(getWinner()));
        } else {
            // TODO: calculate last turn score ?
            sendEvent(new TurnEndedEvent(lastPlayer, playersTurnScore, currentPlayer));
        }

        turnCount++;
        playersTurnScore = 0;
    }

    public boolean isGameOver() {
        if (currentPlayer == null) {
            throw new GameNotStartException();
        }

        return referee.isGameOver(this);
    }

    public Player getWinner() {
        if (currentPlayer == null) {
            throw new GameNotStartException();
        }

        return referee.determineWinner(this);
    }

    private void nextPlayer() {
        if (currentPlayer == null) {
            throw new GameNotStartException();
        }

        int currentIndex = players.indexOf(currentPlayer);
        currentPlayer = players.get((currentIndex + 1) % players.size());
    }

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    private void sendEvent(GameEvent event) {
        for (GameListener listener : listeners) {
            if (event instanceof GameStartedEvent e) listener.onGameStarted(e);
            if (event instanceof GameOverEvent e) listener.onGameOver(e);

            if (event instanceof TurnPassedEvent e) listener.onTurnPassed(e);
            if (event instanceof TurnEndedEvent e) listener.onTurnEnded(e);

            if (event instanceof TilePlacedEvent e) listener.onTilePlaced(e);
            if (event instanceof InvalidMoveEvent e) listener.onInvalidMove(e);

            if (event instanceof RackExchangedEvent e) listener.onRackExchanged(e);
        }
    }
}
