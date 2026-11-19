package io.github.qa.pages;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;

public class InventoryPage extends BasePage {

    private final Locator title = page.getByTestId("title");
    private final Locator cartBadge = page.getByTestId("shopping-cart-badge");
    private final Locator cartLink = page.getByTestId("shopping-cart-link");

    public Locator title() {
        return title;
    }

    public Locator cartBadge() {
        return cartBadge;
    }

    @Step("Добавить в корзину: '{productName}'")
    public InventoryPage addToCart(String productName) {
        page.getByTestId("inventory-item")
                .filter(new Locator.FilterOptions().setHasText(productName))
                .getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                        new Locator.GetByRoleOptions().setName("Add to cart"))
                .click();
        return this;
    }

    @Step("Открыть корзину")
    public CartPage openCart() {
        cartLink.click();
        return new CartPage();
    }
}
