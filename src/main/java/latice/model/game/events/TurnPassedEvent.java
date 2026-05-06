package latice.model.game.events;

import latice.model.player.Player;

public class TurnPassedEvent implements GameEvent {
    private final Player nextPlayer;
    private final Player lastPlayer;

    public TurnPassedEvent(Player nextPlayer, Player lastPlayer) {
        this.nextPlayer = nextPlayer;
        this.lastPlayer = lastPlayer;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public Player getLastPlayer() {
        return lastPlayer;
    }
}
