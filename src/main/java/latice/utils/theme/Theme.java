package latice.utils.theme;

public enum Theme {
    DEFAULT("default", "Classic"),
    ANIMAL_CROSSING("animal_crossing", "Animal Crossing"),
    ZELDA("zelda", "Zelda");

    private final String folderName;
    private final String displayName;

    Theme(String folderName, String displayName) {
        this.folderName = folderName;
        this.displayName = displayName;
    }

    public String getFolderName() { return folderName; }
    public String getDisplayName() { return displayName; }
}
