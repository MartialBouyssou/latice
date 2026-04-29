package latice.model.tile;

public enum Color {
    YELLOW("yellow"), NAVY("navy"), MAGENTA("magenta"),
    RED("red"), GREEN("green"), TEAL("teal");

    private final String colorName;

    Color(String colorName) {
        this.colorName = colorName;
    }

    public String getColor() {
        return colorName;
    }

    @Override
    public String toString() {
        return colorName;
    }
}
