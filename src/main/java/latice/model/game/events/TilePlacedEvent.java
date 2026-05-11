package latice.model.game.events;

import latice.model.player.Player;
import latice.model.tile.Tile;

public class TilePlacedEvent implements GameEvent {
    private final Player player;
    private final Tile tile;
    private final int x;
    private final int y;
    private final int points;

    public TilePlacedEvent(Player player, Tile tile, int x, int y, int points) {
        this.player = player;
        this.tile = tile;
        this.x = x;
        this.y = y;
        this.points = points;
    }

    public Player getPlayer() {
        return player;
    }
    public Tile getTile() {
        return tile;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getPoints() {
        return points;
    }
}
