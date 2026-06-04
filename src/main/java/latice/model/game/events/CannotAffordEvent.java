package latice.model.game.events;

import latice.model.player.Player;

public class CannotAffordEvent implements GameEvent {
    private final String reason;
    private final Player player;

    public CannotAffordEvent(String reason, Player player) {
        this.reason = reason;
        this.player = player;
    }

    public String getReason() {
        return reason;
    }

    public Player getPlayer() {
        return player;
    }
}
