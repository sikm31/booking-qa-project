package io.github.qa.ui;

import io.github.qa.core.BaseUiTest;
import io.github.qa.core.Config;
import io.github.qa.pages.InventoryPage;
import io.github.qa.pages.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Epic("Магазин")
@Feature("Авторизация")
class LoginTest extends BaseUiTest {

    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Успешный вход стандартным пользователем")
    void successfulLogin() {
        InventoryPage inventory = new LoginPage().open()
                .loginAs(Config.get("ui.user"), Config.get("ui.password"));

        assertThat(page()).hasURL(Config.uiBaseUrl() + "/inventory.html");
        assertThat(inventory.title()).hasText("Products");
    }

    @ParameterizedTest(name = "[{index}] user=''{0}'' → ''{2}''")
    @CsvFileSource(resources = "/data/login_negative.csv", numLinesToSkip = 1)
    @Tag("regression")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Вход с невалидными данными показывает ошибку")
    void loginWithInvalidData(String username, String password, String expectedError) {
        LoginPage login = new LoginPage().open().submit(username, password);

        assertThat(login.error()).containsText(expectedError);
        assertThat(page()).hasURL(Config.uiBaseUrl() + "/");
    }
}
