package latice.model.tile;

import latice.utils.theme.ThemeManager;

import java.util.Objects;

public class Tile {
    private final Shape shape;
    private final Color color;

    /**
     * Creates a tile with the given shape and color.
     * @param shape The shape of the tile.
     * @param color The color of the tile.
     */
    public Tile(Shape shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    /**
     * Returns the shape of this tile.
     * @return The shape of the tile.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Returns the color of this tile.
     * @return The color of the tile.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the string representation of this tile.
     * @return A string combining the shape name and the color.
     */
    @Override
    public String toString() {
        return shape.getShape() + " " + color;
    }

    /**
     * Returns the emoji representation of this tile's shape.
     * @return The emoji of the tile's shape.
     */
    public String toEmoji() {
        return shape.toString();
    }

    /**
     * Checks whether this tile is equal to another object.
     * @param obj The object to compare with.
     * @return True if the object is a tile with the same shape and color, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tile other)) return false;
        return other.shape == this.shape && other.color == this.color;
    }

    /**
     * Computes the hash code of this tile.
     * @return The hash code based on the shape and color.
     */
    @Override
    public int hashCode() {
        return Objects.hash(shape, color);
    }

    /**
     * Returns the image path of this tile for the current theme.
     * @return The path to the tile's image.
     */
    public String getTileImgPath() {
        return ThemeManager.getInstance().getTileImagePath(
                shape.getShape(),
                color.getColor().charAt(0)
        );
    }
}
