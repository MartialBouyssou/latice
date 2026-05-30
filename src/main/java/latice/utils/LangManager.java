package latice.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LangManager {

    public static final String LATICE_LANG_PATH = "/latice/lang/";
    private static LangManager instance;

    private final Map<String, String> translations = new HashMap<>();

    private LangManager() {}

    public static LangManager getInstance() {
        if (instance == null) {
            instance = new LangManager();
        }
        return instance;
    }

    public Map<String, String> loadAvailableLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        String path = LATICE_LANG_PATH + "lang.conf";

        readFile(languages, path);

        return languages;
    }

    public void load(String langCode) {
        translations.clear();

        String path = LATICE_LANG_PATH + langCode + ".lang";

        readFile(translations, path);
    }

    private void readFile(Map<String, String> languages, String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    reader.lines().forEach(line -> {
                        if (line.isBlank() || line.startsWith("#")) return;
                        String[] split = line.split("=", 2);
                        if (split.length == 2) {
                            languages.put(split[0].trim(), split[1].trim());
                        }
                    });
                }
            }
        } catch (Exception e) { /* No need to flood */ }
    }

    public String format(String key, Map<String, String> vars) {
        String text = get(key);
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    public String format(String key, String... keyValues) {
        if (keyValues.length % 2 != 0) throw new IllegalArgumentException("Arguments must be key-value pairs");
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