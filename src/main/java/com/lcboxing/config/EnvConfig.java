package com.lcboxing.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static Dotenv dotenv;

    public static void load() {
        dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }

    public static String get(String key) {
        return dotenv.get(key);
    }

    public static String get(String key, String defaultValue) {
        String value = dotenv.get(key);
        return value != null ? value : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String value = dotenv.get(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
