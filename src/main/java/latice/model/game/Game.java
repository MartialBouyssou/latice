package latice.model.game;

import latice.exception.model.GameNotStartException;
import latice.exception.model.NoTilePlayedException;
import latice.model.board.Board;
import latice.model.deck.Deck;
import latice.model.game.events.*;
import latice.model.player.ActionType;
import latice.model.player.Player;
import latice.model.referee.Referee;
import latice.model.tile.Tile;
import latice.utils.LangManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    public static final int MAX_CYCLES = 10;
    private static final int STARTING_ACTIONS_PER_TURN = 1;

    private final List<Player> players;
    private final String player1Username;
    private final String player2Username;
    private Board board;
    private final Referee referee;
    private Player currentPlayer;
    private int turnCount = 0;
    private final List<GameListener> listeners = new ArrayList<>();
    private int playersTurnScore;

    private boolean actionPlayedThisTurn = false;
    private int remainingActions = STARTING_ACTIONS_PER_TURN;

    /**
     * Creates a new game with the given referee and player usernames.
     * @param referee The referee used to validate moves and compute scores.
     * @param player1Username The username of the first player.
     * @param player2Username The username of the second player.
     */
    public Game(Referee referee, String player1Username, String player2Username) {
        this.referee = referee;
        this.players = new ArrayList<>();
        this.player1Username = player1Username;
        this.player2Username = player2Username;
    }

    /**
     * Creates a new game with the given referee and players object.
     * @param referee The referee used to validate moves and compute scores.
     * @param player1 The first player as an object.
     * @param player2 The second player as an object.
     */
    public Game(Referee referee, Player player1, Player player2) {
        this.referee = referee;
        this.players = new ArrayList<>(List.of(player1, player2));
        this.player1Username = player1.getName();
        this.player2Username = player2.getName();
    }

    /**
     * Creates a new game with the given referee and default player usernames.
     * @param referee The referee used to validate moves and compute scores.
     */
    public Game(Referee referee) {
        this(referee, "P1", "P2");
    }

    /**
     * Returns the list of players in the game.
     * @return The list of players.
     */
    public List<Player> getPlayers() {
        return players;
    }


    /**
     * Returns the game board.
     * @return The current board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the player whose turn it currently is.
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the number of turns played so far.
     * @return The turn count.
     */
    public int getTurnCount() {
        return turnCount;
    }

    /**
     * Returns the referee of the game.
     * @return The referee.
     */
    public Referee getReferee() {
        return referee;
    }

    /**
     * Indicates whether an action has already been played during the current turn.
     * @return True if an action was played this turn, false otherwise.
     */
    public boolean isActionPlayedThisTurn() {
        return actionPlayedThisTurn;
    }

    /**
     * Indicates whether the current player has bought an additional action.
     * @return True if more than one action is currently available, false otherwise.
     */
    public boolean isPlayTwiceActive() {
        return remainingActions > STARTING_ACTIONS_PER_TURN;
    }

    /**
     * Indicates whether the current player can still perform an action this turn.
     * @return True if at least one action remains for the current turn.
     */
    public boolean hasRemainingAction() {
        return remainingActions > 0;
    }

    /**
     * Returns the score earned by the current player during this turn.
     * @return The current turn score.
     */
    public int getPlayersTurnScore() {
        return playersTurnScore;
    }

    /**
     * Starts the game by creating the board, splitting the deck between the two
     * players, choosing a random starting player and notifying listeners.
     */
    public void startGame() {
        board = Board.create();
        remainingActions = STARTING_ACTIONS_PER_TURN;
        actionPlayedThisTurn = false;

        Deck fullDeck = Deck.create();
        List<Tile> allTiles = fullDeck.getTiles();

        List<Tile> firstHalf = new ArrayList<>();
        List<Tile> secondHalf = new ArrayList<>();

        for (int i = 0; i < allTiles.size() / 2; i++) {
            firstHalf.add(allTiles.get(i * 2));
            secondHalf.add(allTiles.get(i * 2 + 1));
        }

        players.add(Player.create(player1Username, Deck.create(firstHalf)));
        players.add(Player.create(player2Username, Deck.create(secondHalf)));

        currentPlayer = players.get(new Random().nextInt(players.size()));
        sendEvent(new GameStartedEvent(currentPlayer));
    }

    /**
     * Starts the game without creating anything, but by using packets
     * data, choosing a random starting player and notifying listeners.
     */
    public void startFromExistingPlayers() {
        board = Board.create();
        currentPlayer = players.get(0);
        remainingActions = STARTING_ACTIONS_PER_TURN;
        actionPlayedThisTurn = false;
        sendEvent(new GameStartedEvent(currentPlayer));
    }

    /**
     * Attempts to play the given tile at the given coordinates for the current
     * player, validating the move, updating the board and score, and notifying
     * listeners of the result.
     * @param tile The tile to play.
     * @param x The x coordinate where the tile is placed.
     * @param y The y coordinate where the tile is placed.
     */
    public void playTile(Tile tile, int x, int y) {
        if (currentPlayer == null) throw new GameNotStartException();

        if (!currentPlayer.getRack().getTiles().contains(tile)) {
            sendEvent(new InvalidMoveEvent(currentPlayer, tile, x, y,
                    LangManager.getInstance().get("event.invalidMove.tileError")
            ));
        } else if (!referee.isValidMove(board, tile, x, y)) {
            sendEvent(new InvalidMoveEvent(currentPlayer, tile, x, y,
                    LangManager.getInstance().get("event.invalidMove.moveError")
            ));
        } else {
            playersTurnScore = referee.calculateScore(board, tile, x, y);

            currentPlayer.getRack().getTiles().remove(tile);
            board.placeTile(tile, x, y);
            currentPlayer.incrementTilesPlayed();
            currentPlayer.addScore(playersTurnScore);
            useAction();

            sendEvent(new TilePlacedEvent(currentPlayer, tile, x, y, playersTurnScore));
            endTurnIfNoActionRemains();
        }
    }

    /**
     * Ends the current player's turn by refilling their rack, advancing to the
     * next player, and notifying listeners whether the game is over or the turn
     * simply ended.
     */
    public void endTurn() {
        if (currentPlayer == null) throw new GameNotStartException();
        if (!actionPlayedThisTurn) throw new NoTilePlayedException();

        currentPlayer.getRack().refill(currentPlayer.getDeck());

        Player lastPlayer = currentPlayer;
        int lastScore = playersTurnScore;

        turnCount++;
        nextPlayer();
        prepareNextTurn();

        if (isGameOver()) {
            sendEvent(new GameOverEvent(getWinner()));
        } else {
            sendEvent(new TurnEndedEvent(lastPlayer, lastScore, currentPlayer));
        }
    }

    /**
     * Passes the current player's turn without playing a tile and ends the turn.
     */
    public void passTurn() {
        if (currentPlayer == null) throw new GameNotStartException();
        useAllActions();
        Player lastPlayer = currentPlayer;
        int lastScore = playersTurnScore;

        currentPlayer.getRack().refill(currentPlayer.getDeck());
        turnCount++;
        nextPlayer();
        prepareNextTurn();

        if (isGameOver()) {
            sendEvent(new GameOverEvent(getWinner()));
        } else {
            sendEvent(new TurnPassedEvent(currentPlayer, lastPlayer, lastScore));
        }
    }

    /**
     * Indicates whether the game has reached an end condition.
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        if (currentPlayer == null) throw new GameNotStartException();
        return referee.isGameOver(this);
    }

    /**
     * Determines and returns the winner of the game.
     * @return The winning player.
     */
    public Player getWinner() {
        if (currentPlayer == null) throw new GameNotStartException();
        return referee.determineWinner(this);
    }

    /**
     * Advances the current player to the next player in turn order.
     */
    private void nextPlayer() {
        if (currentPlayer == null) throw new GameNotStartException();
        int currentIndex = players.indexOf(currentPlayer);
        currentPlayer = players.get((currentIndex + 1) % players.size());
    }

    /**
     * Returns the player who will play after the current player.
     * @return The next player.
     */
    public Player getNextPlayer() {
        int currentIndex = players.indexOf(currentPlayer);
        return players.get((currentIndex + 1) % players.size());
    }

    /**
     * Registers a listener to be notified of game events.
     * @param listener The listener to add.
     */
    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Dispatches the given game event to all registered listeners by calling the
     * appropriate callback based on the event's concrete type.
     * @param event The game event to dispatch.
     */
    public void sendEvent(GameEvent event) {
        for (GameListener listener : listeners) {
            if (event instanceof GameStartedEvent e) {
                listener.onGameStarted(e);
            } else if (event instanceof GameOverEvent e) {
                listener.onGameOver(e);
            } else if (event instanceof TurnPassedEvent e) {
                listener.onTurnPassed(e);
            } else if (event instanceof TurnEndedEvent e) {
                listener.onTurnEnded(e);
            } else if (event instanceof TilePlacedEvent e) {
                listener.onTilePlaced(e);
            } else if (event instanceof InvalidMoveEvent e) {
                listener.onInvalidMove(e);
            } else if (event instanceof RackExchangedEvent e) {
                listener.onRackExchanged(e);
            } else if (event instanceof CannotAffordEvent e) {
                listener.onCannotAfford(e);
            } else if (event instanceof ActionBoughtEvent e) {
                listener.onActionBought(e);
            }
        }
    }

    /**
     * Exchanges all tiles on the current player's rack, use one action and
     * ends the turn when no action remains.
     */
    public void exchangeAll() {
        if (currentPlayer == null) throw new GameNotStartException();
        currentPlayer.getRack().exchangeAll(currentPlayer.getDeck());
        useAction();
        sendEvent(new RackExchangedEvent(currentPlayer, currentPlayer.getRack()));
        endTurnIfNoActionRemains();
    }

    /**
     * Use one action after an externally synchronized rack exchange and
     * ends the turn when no action remains.
     */
    public void completeExchangeAsTurn() {
        if (currentPlayer == null) throw new GameNotStartException();
        useAction();
        endTurnIfNoActionRemains();
    }

    /**
     * Lets the current player buy an extra action if they can afford it,
     * spending the required score and activating the play-twice ability, or
     * notifying listeners that the player cannot afford it.
     */
    public void buyAnAction() {
        if (currentPlayer == null) throw new GameNotStartException();

        if (currentPlayer.canAfford(Referee.EXTRA_ACTION_COST)) {
            currentPlayer.spendScore(Referee.EXTRA_ACTION_COST);
            remainingActions++;
            sendEvent(new ActionBoughtEvent(currentPlayer, ActionType.PLAY_TWICE));
        } else {
            sendEvent(new CannotAffordEvent(
                    LangManager.getInstance().get("event.invalidMove.cannotAfford"),
                    currentPlayer
            ));
        }
    }

    /**
     * Makes the current player surrender, declaring the other player the winner
     * and notifying listeners that the game is over.
     */
    public void surrender() {
        useAllActions();
        sendEvent(new GameOverEvent(getNextPlayer()));
    }

    /**
     * Use one available action and marks the current turn as played.
     */
    private void useAction() {
        actionPlayedThisTurn = true;
        if (remainingActions > 0) {
            remainingActions--;
        }
    }

    /**
     * Use every remaining action and marks the current turn as played.
     */
    private void useAllActions() {
        actionPlayedThisTurn = true;
        remainingActions = 0;
    }

    /**
     * Ends the current turn if the active player cannot perform another action.
     */
    private void endTurnIfNoActionRemains() {
        if (!hasRemainingAction()) {
            endTurn();
        }
    }

    /**
     * Resets turn-specific counters for the next player.
     */
    private void prepareNextTurn() {
        actionPlayedThisTurn = false;
        playersTurnScore = 0;
        remainingActions = STARTING_ACTIONS_PER_TURN;
    }
}
