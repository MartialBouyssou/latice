package latice.model.tile;

import java.util.*;

public final class TileFactory {

    private static final Map<String, Tile> imageToTileMap = new HashMap<>();

    static {
        for (Color color : Color.values()) {
            for (Shape shape : Shape.values()) {
                Tile tile = new Tile(shape, color);
                imageToTileMap.put(tile.getImagePath(), tile);
            }
        }
    }

    private TileFactory() {}

    public static Tile getTileFromImagePath(String imagePath) {
        return imageToTileMap.get(imagePath);
    }


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