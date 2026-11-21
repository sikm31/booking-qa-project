package io.github.qa.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Config {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}:]+)(?::([^}]*))?}");

    private static final String ENV = resolveEnv();
    private static final JsonNode FILE = load(ENV);

    private Config() {
    }

    public static String env() {
        return ENV;
    }

    public static String get(String key) {
        String value = lookup(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Не задан параметр '" + key + "' для окружения '" + ENV + "'");
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        String value = lookup(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static boolean getBool(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static String uiBaseUrl()  { return get("ui.baseUrl"); }
    public static String apiBaseUrl() { return get("api.baseUrl"); }
    public static String browser()    { return get("browser.name"); }
    public static boolean headless()  { return getBool("browser.headless"); }
    public static int timeoutMs()     { return getInt("browser.timeoutMs"); }

    private static String lookup(String key) {
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        for (String name : envNames(key)) {
            value = System.getenv(name);
            if (value != null) {
                return value;
            }
        }
        JsonNode node = FILE.at("/" + key.replace('.', '/'));
        return node.isValueNode() ? resolvePlaceholders(node.asText()) : null;
    }

    private static Set<String> envNames(String key) {
        Set<String> names = new LinkedHashSet<>();
        names.add(key.replace("-", "").replace('.', '_').toUpperCase());
        names.add(key.replaceAll("([a-z0-9])([A-Z])", "$1_$2").replace('-', '_').replace('.', '_').toUpperCase());
        return names;
    }

    private static String resolvePlaceholders(String value) {
        Matcher m = PLACEHOLDER.matcher(value);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String name = m.group(1).trim();
            String resolved = System.getProperty(name);
            if (resolved == null) {
                resolved = System.getenv(name);
            }
            if (resolved == null) {
                resolved = m.group(2);
            }
            if (resolved == null) {
                throw new IllegalStateException("Не удалось разрешить плейсхолдер '${" + name + "}' в значении '" + value + "'");
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(resolved));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String resolveEnv() {
        String env = System.getProperty("env");
        if (env == null || env.isBlank()) {
            env = System.getenv("ENV");
        }
        if (env == null || env.isBlank()) {
            env = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        return env == null || env.isBlank() ? "dev" : env.trim();
    }

    private static JsonNode load(String env) {
        String path = "config/application-" + env + ".yaml";
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Файл конфигурации не найден: " + path);
            }
            return new ObjectMapper(new YAMLFactory()).readTree(in);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать " + path, e);
        }
    }
}
