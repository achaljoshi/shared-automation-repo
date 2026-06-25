package com.sharedframework.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyLoader {

    private static final Map<String, Properties> cache = new ConcurrentHashMap<>();

    private PropertyLoader() {
        // Utility class
    }

    public static Properties load(String fileName) {
        return cache.computeIfAbsent(fileName, PropertyLoader::loadFromClasspath);
    }

    private static Properties loadFromClasspath(String fileName) {
        Properties properties = new Properties();

        // Try with and without "config/" prefix
        String[] paths = {fileName, "config/" + fileName};

        for (String path : paths) {
            try (InputStream is = PropertyLoader.class.getClassLoader().getResourceAsStream(path)) {
                if (is != null) {
                    properties.load(is);
                    System.out.println("Loaded properties from classpath: " + path);
                    return properties;
                }
            } catch (IOException e) {
                System.err.println("Error loading properties from " + path + ": " + e.getMessage());
            }
        }

        System.err.println("WARNING: Properties file not found: " + fileName);
        return properties;
    }

    public static String get(String fileName, String key) {
        return load(fileName).getProperty(key);
    }

    public static String get(String fileName, String key, String defaultValue) {
        return load(fileName).getProperty(key, defaultValue);
    }

    public static String getRequired(String fileName, String key) {
        String value = load(fileName).getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                    String.format("Required property '%s' is missing from '%s'", key, fileName));
        }
        return value.trim();
    }

    public static int getInt(String fileName, String key, int defaultValue) {
        String value = get(fileName, key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer value for key '" + key + "': " + value + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String fileName, String key, boolean defaultValue) {
        String value = get(fileName, key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    public static long getLong(String fileName, String key, long defaultValue) {
        String value = get(fileName, key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    public static void clearCache(String fileName) {
        cache.remove(fileName);
    }
}
