package latice.model.game.events;

import latice.model.player.Player;
import latice.model.player.Rack;

public class RackExchangedEvent implements GameEvent {
    private final Player player;
    private final Rack lastRack;

    public RackExchangedEvent(Player player, Rack lastRack) {
        this.player = player;
        this.lastRack = lastRack;
    }

    public Rack getLastRack() {
        return lastRack;
    }

    public Player getPlayer() {
        return player;
    }
}
