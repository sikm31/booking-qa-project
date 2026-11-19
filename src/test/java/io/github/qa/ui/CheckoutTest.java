package io.github.qa.ui;

import io.github.qa.core.BaseUiTest;
import io.github.qa.data.TestData;
import io.github.qa.pages.CartPage;
import io.github.qa.pages.CheckoutPage;
import io.github.qa.pages.InventoryPage;
import io.github.qa.steps.ShopSteps;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Epic("Магазин")
@Feature("Покупка")
class CheckoutTest extends BaseUiTest {

    private final ShopSteps shop = new ShopSteps();

    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("E2E: покупка товара от входа до подтверждения заказа")
    void buyProduct() {
        CheckoutPage checkout = shop.buy("Sauce Labs Backpack", TestData.customer());

        assertThat(checkout.completeHeader()).hasText("Thank you for your order!");
    }

    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Счётчик корзины и состав корзины после добавления двух товаров")
    void cartContainsAddedProducts() {
        InventoryPage inventory = shop.loginAsStandardUser()
                .addToCart("Sauce Labs Backpack")
                .addToCart("Sauce Labs Bike Light");

        assertThat(inventory.cartBadge()).hasText("2");

        CartPage cart = inventory.openCart();
        assertThat(cart.itemNames()).containsText(new String[]{"Sauce Labs Backpack", "Sauce Labs Bike Light"});
    }
}
