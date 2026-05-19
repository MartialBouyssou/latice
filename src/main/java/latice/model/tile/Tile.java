package latice.model.tile;

import java.util.Map;

public class Tile {
    private final Shape shape;
    private final Color color;
    private final String imagePath;

    public Tile(Shape shape, Color color) {
        this.shape = shape;
        this.color = color;
        this.imagePath = getTileImgPath();
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public String getImagePath() {
        return imagePath;
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
        if (obj == null) return false;
        if (!(obj instanceof Tile other)) return false;

        return other.shape == this.shape && other.color == this.color;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
