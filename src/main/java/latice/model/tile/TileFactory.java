package latice.model.tile;

import java.util.*;

public final class TileFactory {

    private TileFactory() {}

    public static List<Tile> createFullDeckTiles() {
        List<Tile> tiles = new ArrayList<>();

        for (Color color : Color.values()) {
            for (Shape shape : Shape.values()) {
                tiles.add(new Tile(shape, color));
                tiles.add(new Tile(shape, color));
            }
        }

        return tiles;
    }
}