package latice.model.tile;

public enum Color {
    YELLOW("yellow"), NAVY("navy"), MAGENTA("magenta"),
    RED("red"), GREEN("green"), TEAL("teal");

    private final String colorName;

    /**
     * Creates a color constant with its associated name.
     * @param colorName The textual name of the color.
     */
    Color(String colorName) {
        this.colorName = colorName;
    }

    /**
     * Returns the textual name of this color.
     * @return The name of the color.
     */
    public String getColor() {
        return colorName;
    }

    /**
     * Returns the string representation of this color.
     * @return The name of the color.
     */
    @Override
    public String toString() {
        return colorName;
    }
}
