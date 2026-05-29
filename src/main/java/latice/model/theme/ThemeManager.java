package latice.model.theme;

public class ThemeManager {

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

    private static ThemeManager instance;
    private Theme currentTheme = Theme.DEFAULT;

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public Theme getCurrentTheme() { return currentTheme; }

    public void setCurrentTheme(Theme theme) { this.currentTheme = theme; }

    public String getImagePath(String filename) {
        return "latice/images/" + currentTheme.getFolderName() + "/" + filename;
    }

    public String getTileImagePath(String shapeName, char colorInitial) {
        String filename = shapeName + "_" + colorInitial + ".png";
        return getImagePath(filename);
    }

    public String getBoardBgPath(String bgName) {
        return getImagePath(bgName);
    }
}