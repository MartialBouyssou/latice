package latice.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LangManager {

    private static LangManager instance;

    private final Map<String, String> translations = new HashMap<>();

    private LangManager() {}

    public static LangManager getInstance() {
        if (instance == null) {
            instance = new LangManager();
        }
        return instance;
    }

    public void load(String langCode) {
        translations.clear();

        String path = "/latice/lang/" + langCode + ".lang";

        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                    reader.lines().forEach(line -> {
                        if (line.isBlank() || line.startsWith("#"))
                            return;

                        String[] split = line.split("=", 2);

                        if (split.length == 2) {
                            translations.put(split[0].trim(), split[1].trim());
                        }
                    });
                }
            }
        } catch (Exception e) { /* Impossible to load => no need to flood the console*/ }
    }

    public String get(String key) {
        return translations.getOrDefault(key, "?unknown." + key + ".unknown?");
    }
}