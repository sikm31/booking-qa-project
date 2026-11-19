package io.github.qa.pages;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;

public class LoginPage extends BasePage {

    private final Locator username = page.getByTestId("username");
    private final Locator password = page.getByTestId("password");
    private final Locator loginButton = page.getByTestId("login-button");
    private final Locator error = page.getByTestId("error");

    @Step("Открыть страницу входа")
    public LoginPage open() {
        page.navigate("/");
        return this;
    }

    @Step("Войти как '{user}'")
    public InventoryPage loginAs(String user, String pass) {
        submit(user, pass);
        return new InventoryPage();
    }

    @Step("Отправить форму входа: '{user}'")
    public LoginPage submit(String user, String pass) {
        username.fill(user);
        password.fill(pass);
        loginButton.click();
        return this;
    }

    public Locator error() {
        return error;
    }
}
