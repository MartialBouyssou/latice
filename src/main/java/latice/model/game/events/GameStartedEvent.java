package latice.model.game.events;

import latice.model.player.Player;

public class GameStartedEvent implements GameEvent {
    private final Player player;

    public GameStartedEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
