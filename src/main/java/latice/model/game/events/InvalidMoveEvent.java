package latice.model.game.events;

import latice.model.player.Player;
import latice.model.tile.Tile;

public class InvalidMoveEvent implements GameEvent {
    private final Player player;
    private final Tile tile;
    private final int x;
    private final int y;

    public InvalidMoveEvent(Player player, Tile tile, int x, int y) {
        this.player = player;
        this.tile = tile;
        this.x = x;
        this.y = y;
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
