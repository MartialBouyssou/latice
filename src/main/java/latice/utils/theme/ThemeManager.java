package latice.utils.theme;

// HEY PLEASE READ ME, so I didn't write this for no reason... :(
// So here I refactor this class into a Singleton, let me explain why I did this and what I've
// understood about singleton :
// So my next implementation will be a sort of multiplayer, for that I will need multiple thread.
// So with multiple thread my last implementation of theme and Lang would be enabled in only one
// thead. In fact an instance seems to exist in only one thread, BUT singleton are here to fix this
// by existing in multiple thread. So here volatile say to the compilator "hey wait before
// publishing me null, I need to be fully initialized before being used". And synchronized say
// "hey only one thread can access this block at a time", so they don't over collapse on it.
// So that's what I've understood, I hope I'm right and if I'm not please tell me => I always
// want to learn new things !!! :)
// Thanks for reading my long long epilogue ! :)
// Source:
// https://stackoverflow.com/questions/7855700/why-is-volatile-used-in-double-checked-locking
// https://dev.to/devcorner/thread-safe-singleton-in-java-understanding-volatile-and-double-checked-locking-3d1a
// https://www.baeldung.com/java-implement-thread-safe-singleton
public class ThemeManager {
    private static volatile ThemeManager instance;
    private Theme currentTheme = Theme.DEFAULT;

    /**
     * Prevents external instantiation to strictly enforce the Singleton pattern.
     */
    private ThemeManager() {}

    /**
     * Retrieves the single, thread-safe instance of the ThemeManager.
     *
     * @return The singleton instance of ThemeManager.
     */
    public static ThemeManager getInstance() {
        if (instance == null) {
            synchronized (ThemeManager.class) {
                if (instance == null) {
                    instance = new ThemeManager();
                }
            }
        }
        return instance;
    }

    /**
     * Retrieves the currently active theme.
     *
     * @return The current Theme enum value.
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Sets a new active theme for the application.
     *
     * @param theme The new Theme to apply.
     */
    public void setCurrentTheme(Theme theme) {
        this.currentTheme = theme;
    }

    /**
     * Constructs the full file path for an image based on the current theme.
     *
     * @param filename The name of the image file (including extension).
     *
     * @return The formatted path string pointing to the themed image.
     */
    public String getImagePath(String filename) {
        return "latice/images/" + currentTheme.getFolderName() + "/" + filename;
    }

    /**
     * Constructs the specific file path for a tile image based on its shape and color.
     *
     * @param shapeName The name of the tile's shape.
     * @param colorInitial The initial character representing the tile's color.
     *
     * @return The formatted path string for the specific tile image.
     */
    public String getTileImagePath(String shapeName, char colorInitial) {
        return getImagePath(shapeName + "_" + colorInitial + ".png");
    }

    /**
     * Constructs the file path for the board's background image.
     *
     * @param bgName The filename of the background image.
     *
     * @return The formatted path string pointing to the board background.
     */
    public String getBoardBgPath(String bgName) {
        return getImagePath(bgName);
    }
}