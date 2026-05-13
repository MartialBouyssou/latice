package latice.model.tile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TileFactoryTest {

    @Test
    void createFullDeckTilesShouldReturn72Tiles() {
        // arrange & act
        List<Tile> tiles = TileFactory.createFullDeckTiles();

        // assert
        assertEquals(72, tiles.size());
    }

    @Test
    void createFullDeckTilesShouldContainEachCombinationTwice() {
        // arrange
        List<Tile> tiles = TileFactory.createFullDeckTiles();

        // act
        long birdRedCount = tiles.stream()
                .filter(tile -> tile.getShape() == Shape.BIRD && tile.getColor() == Color.RED)
                .count();

        // assert
        assertEquals(2, birdRedCount);
    }

    @Test
    void createFullDeckTilesShouldCoverAllColorsAndShapes() {
        // Arrange
        List<Tile> tiles = TileFactory.createFullDeckTiles();

        // Aact & assert
        for (Color color : Color.values()) {
            for (Shape shape : Shape.values()) {
                Tile expected = new Tile(shape, color);
                long count = tiles.stream().filter(tile -> tile.equals(expected)).count();
                assertEquals(2, count,
                        "Expected 2 copies of " + shape + " " + color + " but found " + count);
            }
        }
    }
}
