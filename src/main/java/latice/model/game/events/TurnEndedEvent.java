package latice.model.game.events;

import latice.model.player.Player;

public class TurnEndedEvent implements GameEvent {
    private final Player nextPlayer;
    private final Player lastPlayer;
    private final int lastTurnScore;

    public TurnEndedEvent(Player lastPlayer, int lastTurnScore, Player nextPlayer) {
        this.lastPlayer = lastPlayer;
        this.lastTurnScore = lastTurnScore;
        this.nextPlayer = nextPlayer;
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
