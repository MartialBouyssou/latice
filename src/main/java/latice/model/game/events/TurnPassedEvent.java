package latice.model.game.events;

import latice.model.player.Player;

public class TurnPassedEvent implements GameEvent {
    private final Player nextPlayer;
    private final int currentPlayerScore;
    private final Player lastPlayer;

    public TurnPassedEvent(Player nextPlayer, Player lastPlayer, int currentPayerScore) {
        this.nextPlayer = nextPlayer;
        this.lastPlayer = lastPlayer;
        this.currentPlayerScore = currentPayerScore;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public Player getLastPlayer() {
        return lastPlayer;
    }

    public int getCurrentPlayerScore() {
        return currentPlayerScore;
    }
}
