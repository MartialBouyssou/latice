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

    private ThemeManager() {}

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

    public Theme getCurrentTheme() { return currentTheme; }

    public void setCurrentTheme(Theme theme) { this.currentTheme = theme; }

    public String getImagePath(String filename) {
        return "latice/images/" + currentTheme.getFolderName() + "/" + filename;
    }

    public String getTileImagePath(String shapeName, char colorInitial) {
        return getImagePath(shapeName + "_" + colorInitial + ".png");
    }

    public String getBoardBgPath(String bgName) {
        return getImagePath(bgName);
    }
}
