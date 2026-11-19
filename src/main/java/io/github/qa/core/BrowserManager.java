package io.github.qa.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public final class BrowserManager {

    private static final Map<Thread, Browser> BROWSERS = new ConcurrentHashMap<>();
    private static final Map<Thread, Playwright> PLAYWRIGHTS = new ConcurrentHashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(BrowserManager::closeAll));
    }

    private BrowserManager() {
    }

    public static Browser browser() {
        return BROWSERS.computeIfAbsent(Thread.currentThread(), t -> launch());
    }

    private static Browser launch() {
        // Для системных Chrome/Edge свои браузеры Playwright не нужны: отключаем их проверку и скачивание
        boolean systemBrowser = Set.of("chrome", "msedge").contains(Config.browser());
        Playwright playwright = systemBrowser
                ? Playwright.create(new Playwright.CreateOptions().setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1")))
                : Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test"); // SauceDemo размечает элементы атрибутом data-test
        PLAYWRIGHTS.put(Thread.currentThread(), playwright);
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(Config.headless());
        return switch (Config.browser()) {
            case "chrome" -> playwright.chromium().launch(options.setChannel("chrome"));
            case "msedge" -> playwright.chromium().launch(options.setChannel("msedge"));
            case "firefox" -> playwright.firefox().launch(options);
            case "webkit" -> playwright.webkit().launch(options);
            default -> playwright.chromium().launch(options);
        };
    }

    private static void closeAll() {
        BROWSERS.values().forEach(Browser::close);
        PLAYWRIGHTS.values().forEach(Playwright::close);
    }
}
