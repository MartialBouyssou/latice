package latice.model.game.events;

import latice.model.player.Player;

public class TurnPassedEvent implements GameEvent {
    private final Player nextPlayer;
    private final int lastTurnScore;
    private final Player lastPlayer;

    public TurnPassedEvent(Player nextPlayer, Player lastPlayer, int lastTurnScore) {
        this.nextPlayer = nextPlayer;
        this.lastPlayer = lastPlayer;
        this.lastTurnScore = lastTurnScore;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public Player getLastPlayer() {
        return lastPlayer;
    }

    public int getLastTurnScore() {
        return lastTurnScore;
    }
}
