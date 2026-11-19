package io.github.qa.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.ReducedMotion;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class PlaywrightExtension implements BeforeEachCallback, AfterTestExecutionCallback, AfterEachCallback {

    private static final Path TRACES = Paths.get("target", "traces");

    @Override
    public void beforeEach(ExtensionContext ctx) {
        Browser browser = BrowserManager.browser();
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setBaseURL(Config.uiBaseUrl())
                .setViewportSize(1366, 768)
                .setReducedMotion(ReducedMotion.REDUCE)); // меньше анимаций — меньше flaky
        context.setDefaultTimeout(Config.timeoutMs());
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));

        Page page = context.newPage();
        PageHolder.set(context, page);
    }

    @Override
    public void afterTestExecution(ExtensionContext ctx) {
        BrowserContext context = PageHolder.context();
        if (context == null) {
            return;
        }
        boolean failed = ctx.getExecutionException().isPresent();
        if (!failed) {
            context.tracing().stop();
            return;
        }
        Page page = PageHolder.page();
        Allure.addAttachment("Скриншот при падении", "image/png",
                new ByteArrayInputStream(page.screenshot(new Page.ScreenshotOptions().setFullPage(true))), "png");
        Allure.addAttachment("URL", page.url());

        Path trace = TRACES.resolve(ctx.getUniqueId().hashCode() + ".zip");
        context.tracing().stop(new Tracing.StopOptions().setPath(trace));
        try {
            Allure.addAttachment("Playwright trace (npx playwright show-trace)", "application/zip",
                    Files.newInputStream(trace), "zip");
        } catch (IOException e) {
            Allure.addAttachment("Trace недоступен", e.getMessage());
        }
    }

    @Override
    public void afterEach(ExtensionContext ctx) {
        BrowserContext context = PageHolder.context();
        if (context != null) {
            context.close();
        }
        PageHolder.clear();
    }
}
