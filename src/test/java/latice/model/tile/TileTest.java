package latice.model.tile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void getTileShouldReturnTheShapeItWasCreatedWith() {
        Tile tile = new Tile(Shape.BIRD, Color.RED);

        Shape actualShape = tile.getShape();
        assertEquals(Shape.BIRD, actualShape);
    }

    @Test
    void getTileShouldReturnTheColorItWasCreatedWith() {
        Tile tile = new Tile(Shape.TURTLE, Color.GREEN);

        Color actualColor = tile.getColor();
        assertEquals(Color.GREEN, actualColor);
    }

    @Test
    void twoTilesWithSameShapeAndColorShouldBeEqual() {
        Tile firstTile  = new Tile(Shape.DOLPHIN, Color.YELLOW);
        Tile secondTile = new Tile(Shape.DOLPHIN, Color.YELLOW);

        boolean areEqual = firstTile.equals(secondTile);
        assertTrue(areEqual);
    }

    @Test
    void twoTilesWithDifferentShapeShouldNotBeEqual() {
        Tile birdTile  = new Tile(Shape.BIRD, Color.RED);
        Tile geckoTile = new Tile(Shape.GECKO, Color.RED);

        boolean areEqual = birdTile.equals(geckoTile);
        assertFalse(areEqual);
    }

    @Test
    void twoTilesWithDifferentColorShouldNotBeEqual() {
        Tile redTile  = new Tile(Shape.FLOWER, Color.RED);
        Tile tealTile = new Tile(Shape.FLOWER, Color.TEAL);

        boolean areEqual = redTile.equals(tealTile);
        assertFalse(areEqual);
    }

    @Test
    void tileShouldBeEqualToItself() {
        Tile tile = new Tile(Shape.FEATHER, Color.MAGENTA);

        boolean areEqual = tile.equals(tile);
        assertTrue(areEqual);
    }

    @Test
    void tileShouldNotBeEqualToNull() {
        Tile tile = new Tile(Shape.GECKO, Color.NAVY);

        boolean areEqual = tile.equals(null);
        assertFalse(areEqual);
    }

    @Test
    void tileShouldNotBeEqualToADifferentTypeOfObject() {
        Tile tile = new Tile(Shape.BIRD, Color.GREEN);

        boolean areEqual = tile.equals("not a tile");
        assertFalse(areEqual);
    }

    @Test
    void toStringShouldContainShapeNameAndColorName() {
        Tile tile = new Tile(Shape.BIRD, Color.RED);
        String text = tile.toString();

        assertTrue(text.contains("bird"));
        assertTrue(text.contains("red"));
    }

    @Test
    void getTileImgPathShouldReturnCorrectPath() {
        Tile tile = new Tile(Shape.BIRD, Color.RED);

        String imagePath = tile.getTileImgPath();
        assertEquals("latice/images/bird_r.png", imagePath);
    }

    @Test
    void getTileImgPathShouldUseFirstLetterOfColor() {
        Tile tile = new Tile(Shape.TURTLE, Color.YELLOW);
        String imagePath = tile.getTileImgPath();

        assertTrue(imagePath.endsWith("_y.png"));
    }
}
