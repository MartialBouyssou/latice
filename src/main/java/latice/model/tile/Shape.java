package latice.model.tile;

public enum Shape {
    FEATHER("feather", "\uD83E\uDEB6"),
    BIRD("bird", "\uD83D\uDC26"),
    TURTLE("turtle", "\uD83D\uDC22"),
    FLOWER("flower", "\uD83C\uDF38"),
    GECKO("gecko", "\uD83E\uDD8E"),
    DOLPHIN("dolphin", "\uD83D\uDC2C");

    private final String shapeName;
    private final String emoji;

    /**
     * Creates a shape constant with its associated name and emoji.
     * @param shapeName The textual name of the shape.
     * @param emoji The emoji representing the shape.
     */
    Shape(String shapeName, String emoji) {
        this.shapeName = shapeName;
        this.emoji = emoji;
    }

    /**
     * Returns the textual name of this shape.
     * @return The name of the shape.
     */
    public String getShape() {
        return shapeName;
    }

    /**
     * Returns the string representation of this shape.
     * @return The emoji representing the shape.
     */
    @Override
    public String toString() {
        return emoji;
    }
}
