package latice.model.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TileFactory {

    private static final Map<String, Tile> filenameToTileMap = new HashMap<>();

    static {
        rebuildMap();
    }

    private TileFactory() {}

    public static void rebuildMap() {
        filenameToTileMap.clear();
        for (Color color : Color.values()) {
            for (Shape shape : Shape.values()) {
                Tile tile = new Tile(shape, color);
                String filename = shape.getShape() + "_" + color.getColor().charAt(0) + ".png";
                filenameToTileMap.put(filename, tile);
            }
        }
    }

    public static Tile getTileFromImagePath(String imagePath) {
        String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1);
        return filenameToTileMap.get(filename);
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