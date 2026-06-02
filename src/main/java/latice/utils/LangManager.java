package latice.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// HEY PLEASE READ THE COMMENT IN ThemeManager.java (line 3), so I didn't write this for
// no reason... :(
// It explain the Singleton pattern that I've used here :3
// hope you'll enjoy your reading pause !! :D
public class LangManager {
    // https://docs.oracle.com/javase/8/docs/api/java/util/logging/Logger.html
    // I've done that 'cause SonarQube absolutly wanted us to use Logger, but don't know why
    private static final Logger LOGGER = Logger.getLogger(LangManager.class.getName());
    public static final String LATICE_LANG_PATH = "/latice/lang/";

    private static volatile LangManager instance;

    private final Map<String, String> translations = new HashMap<>();

    /**
     * Prevents external instantiation to strictly enforce the Singleton pattern.
     */
    private LangManager() {}

    /**
     * Retrieves the single instance of the LangManager class using double-checked locking.
     *
     * @return The singleton instance of LangManager.
     */
    public static LangManager getInstance() {
        if (instance == null) {
            synchronized (LangManager.class) {
                if (instance == null) {
                    instance = new LangManager();
                }
            }
        }
        return instance;
    }

    /**
     * Loads the list of available languages from the master configuration file.
     *
     * @return A map containing the available language codes and their corresponding names.
     */
    public Map<String, String> loadAvailableLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        readFile(languages, LATICE_LANG_PATH + "lang.conf");
        return languages;
    }

    /**
     * Clears current translations and loads a new specific language file into memory.
     *
     * @param langCode The code of the language to load (e.g., "en_US" or "fr_FR").
     */
    public void load(String langCode) {
        translations.clear();
        readFile(translations, LATICE_LANG_PATH + langCode + ".lang");
    }

    /**
     * Reads a properties file from the given path and populates the target map.
     *
     * @param target The map where the read key-value properties will be stored.
     * @param path The resource path to the file to read.
     */
    private void readFile(Map<String, String> target, String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                LOGGER.log(Level.WARNING, "Language file not found: {0}", path);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                reader.lines().forEach(line -> {
                    if (line.isBlank() || line.startsWith("#")) return;
                    String[] split = line.split("=", 2);
                    if (split.length == 2) {
                        target.put(split[0].trim(), split[1].trim());
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to read language file: " + path + " - " + e.getMessage());
        }
    }

    /**
     * Replaces variables within a translation string using a map of provided values.
     *
     * @param key The translation key to format.
     * @param vars A map containing the variable names and their replacement values.
     *
     * @return The formatted translation string.
     */
    public String format(String key, Map<String, String> vars) {
        String text = get(key);
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    /**
     * Replaces variables within a translation string using a sequence of key-value pairs.
     *
     * @param key The translation key to format.
     * @param keyValues An alternating sequence of variable names and their replacement values.
     *
     * @return The formatted translation string.
     */
    public String format(String key, String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Arguments must be key-value pairs");
        }
        Map<String, String> vars = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            vars.put(keyValues[i], keyValues[i + 1]);
        }
        return format(key, vars);
    }

    /**
     * Retrieves the translation string associated with the given key.
     *
     * @param key The translation key to look up.
     *
     * @return The translated string, or a formatted placeholder if the key is not found.
     */
    public String get(String key) {
        return translations.getOrDefault(key, "?unknown." + key + ".unknown?");
    }
}