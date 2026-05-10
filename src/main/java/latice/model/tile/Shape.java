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

    Shape(String shapeName, String emoji) {
        this.shapeName = shapeName;
        this.emoji = emoji;
    }

    public String getShape() {
        return shapeName;
    }

    @Override
    public String toString() {
        return emoji;
    }
}
