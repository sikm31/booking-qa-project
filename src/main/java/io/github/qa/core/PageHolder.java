package io.github.qa.core;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;


public final class PageHolder {

    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PageHolder() {
    }

    public static Page page() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("Страница не создана. Тест должен наследоваться от BaseUiTest");
        }
        return page;
    }

    static BrowserContext context() {
        return CONTEXT.get();
    }

    static void set(BrowserContext context, Page page) {
        CONTEXT.set(context);
        PAGE.set(page);
    }

    static void clear() {
        CONTEXT.remove();
        PAGE.remove();
    }
}
