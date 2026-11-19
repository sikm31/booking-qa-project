package io.github.qa.core;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;


@Tag("ui")
@ExtendWith(PlaywrightExtension.class)
public abstract class BaseUiTest {

    protected Page page() {
        return PageHolder.page();
    }
}
