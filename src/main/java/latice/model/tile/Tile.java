package latice.model.tile;

import latice.utils.theme.ThemeManager;

import java.util.Objects;

public class Tile {
    private final Shape shape;
    private final Color color;

    public Tile(Shape shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return shape.getShape() + " " + color;
    }

    public String toEmoji() {
        return shape.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tile other)) return false;
        return other.shape == this.shape && other.color == this.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shape, color);
    }

    public String getTileImgPath() {
        return ThemeManager.getInstance().getTileImagePath(
                shape.getShape(),
                color.getColor().charAt(0)
        );
    }
}
