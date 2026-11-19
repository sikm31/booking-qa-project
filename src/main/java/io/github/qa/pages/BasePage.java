package io.github.qa.pages;

import com.microsoft.playwright.Page;
import io.github.qa.core.PageHolder;


public abstract class BasePage {

    protected final Page page;

    protected BasePage() {
        this.page = PageHolder.page();
    }
}
