package latice.model.game.events;

import latice.model.player.Player;
import latice.model.tile.Tile;

public class InvalidMoveEvent implements GameEvent {
    private final Player player;
    private final Tile tile;
    private final int x;
    private final int y;
    private final String reason;

    public InvalidMoveEvent(Player player, Tile tile, int x, int y, String reason) {
        this.player = player;
        this.tile = tile;
        this.x = x;
        this.y = y;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public Player getPlayer() {
        return player;
    }

    public Tile getTile() {
        return tile;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
