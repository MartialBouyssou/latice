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

    private LangManager() {}

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

    public Map<String, String> loadAvailableLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        readFile(languages, LATICE_LANG_PATH + "lang.conf");
        return languages;
    }

    public void load(String langCode) {
        translations.clear();
        readFile(translations, LATICE_LANG_PATH + langCode + ".lang");
    }

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

    public String format(String key, Map<String, String> vars) {
        String text = get(key);
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

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

    public String get(String key) {
        return translations.getOrDefault(key, "?unknown." + key + ".unknown?");
    }
}
