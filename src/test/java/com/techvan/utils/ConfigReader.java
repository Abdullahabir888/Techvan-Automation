package com.techvan.utils;

import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            PROPS.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load config.properties", e);
        }
    }

    private ConfigReader() {}

    /** Order: -Dkey=value, then env TECHVAN_KEY_NAME (loginEmail -> TECHVAN_LOGIN_EMAIL), then the file. */
    public static String get(String key) {
        String v = System.getProperty(key);
        if (v != null && !v.isBlank()) return v;
        String env = System.getenv("TECHVAN_" + key.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase());
        if (env != null && !env.isBlank()) return env;
        return PROPS.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String v = get(key);
        return (v == null || v.isBlank()) ? defaultValue : v.trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key).trim());
    }

    public static int getInt(String key, int defaultValue) {
        String v = get(key);
        return (v == null || v.isBlank()) ? defaultValue : Integer.parseInt(v.trim());
    }

    public static boolean getBool(String key) {
        return Boolean.parseBoolean(get(key, "false"));
    }
}
