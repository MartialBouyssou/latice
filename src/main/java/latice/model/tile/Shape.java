package latice.model.tile;

public enum Shape {
    FEATHER("feather"), BIRD("bird"), TURTLE("turtle"),
    FLOWER("flower"), GECKO("gecko"), DOLPHIN("dolphin");

    private final String shapeName;

    Shape(String shapeName) {
        this.shapeName = shapeName;
    }

    public String getShape() {
        return shapeName;
    }

    @Override
    public String toString() {
        return shapeName;
    }
}
