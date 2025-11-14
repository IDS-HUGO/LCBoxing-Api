package com.lcboxing.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv;

    static {
        Dotenv temp;
        try {
            temp = Dotenv.configure()
                    .directory(System.getProperty("user.dir") + "/src/main/resources")
                    .ignoreIfMissing()
                    .load();
        } catch (Exception e) {
            temp = null;
        }
        dotenv = temp;
    }

    // Server
    public static final int SERVER_PORT = getInt("SERVER_PORT", 7000);
    public static final String SERVER_HOST = get("SERVER_HOST", "0.0.0.0");

    // Database
    public static final String DB_HOST = get("DB_HOST", "localhost");
    public static final int DB_PORT = getInt("DB_PORT", 3306);
    public static final String DB_NAME = get("DB_NAME", "lcboxing_normalizada");
    public static final String DB_USER = get("DB_USER", "root");
    public static final String DB_PASSWORD = get("DB_PASSWORD", "");
    public static final int DB_POOL_SIZE = getInt("DB_POOL_SIZE", 10);
    public static final int DB_CONNECTION_TIMEOUT = getInt("DB_CONNECTION_TIMEOUT", 30000);

    // JWT
    public static final String JWT_SECRET = get("JWT_SECRET", "default_secret_change_in_production");
    public static final int JWT_EXPIRATION_HOURS = getInt("JWT_EXPIRATION_HOURS", 24);

    // Email
    public static final String EMAIL_HOST = get("EMAIL_HOST", "smtp.gmail.com");
    public static final int EMAIL_PORT = getInt("EMAIL_PORT", 587);
    public static final String EMAIL_USERNAME = get("EMAIL_USERNAME", "");
    public static final String EMAIL_PASSWORD = get("EMAIL_PASSWORD", "");
    public static final String EMAIL_FROM = get("EMAIL_FROM", "");
    public static final String EMAIL_FROM_NAME = get("EMAIL_FROM_NAME", "LC Boxing Gym");

    // Application
    public static final String APP_ENV = get("APP_ENV", "development");
    public static final String APP_NAME = get("APP_NAME", "LC Boxing API");
    public static final String APP_VERSION = get("APP_VERSION", "1.0.0");

    // CORS
    public static final String CORS_ORIGINS = get("CORS_ORIGINS", "*");

    // Logging
    public static final String LOG_LEVEL = get("LOG_LEVEL", "INFO");

    private static String get(String key, String defaultValue) {
        if (dotenv == null) return defaultValue;
        String value = dotenv.get(key);
        return value != null ? value : defaultValue;
    }


    private static int getInt(String key, int defaultValue) {
        if (dotenv == null) return defaultValue;
        String value = dotenv.get(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public static String getDatabaseUrl() {
        return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                DB_HOST, DB_PORT, DB_NAME);
    }
}