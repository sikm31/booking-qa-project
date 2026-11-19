package io.github.qa.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Конфигурация по окружениям из config/application-{env}.yaml.
 * Ключи пишутся через точку по вложенности YAML: "ui.baseUrl", "browser.headless".
 * Приоритет: системное свойство (-Dui.baseUrl=...) → переменная окружения (UI_BASEURL) → YAML-файл.
 * Окружение выбирается через -Denv=dev|stage (по умолчанию dev).
 */
public final class Config {

    private static final String ENV = System.getProperty("env", "dev");
    private static final JsonNode FILE = load(ENV);

    private Config() {
    }

    public static String env() {
        return ENV;
    }

    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key.toUpperCase().replace('.', '_'));
        }
        if (value == null) {
            JsonNode node = FILE.at("/" + key.replace('.', '/'));
            value = node.isValueNode() ? node.asText() : null;
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Не задан параметр '" + key + "' для окружения '" + ENV + "'");
        }
        return value;
    }

    public static boolean getBool(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    // Удобные геттеры для часто используемых параметров
    public static String uiBaseUrl()  { return get("ui.baseUrl"); }
    public static String apiBaseUrl() { return get("api.baseUrl"); }
    public static String browser()    { return get("browser.name"); }
    public static boolean headless()  { return getBool("browser.headless"); }
    public static int timeoutMs()     { return getInt("browser.timeoutMs"); }

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
