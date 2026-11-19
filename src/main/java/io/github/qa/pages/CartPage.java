package io.github.qa.pages;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;

public class CartPage extends BasePage {

    private final Locator items = page.getByTestId("inventory-item-name");
    private final Locator checkout = page.getByTestId("checkout");

    public Locator itemNames() {
        return items;
    }

    @Step("Перейти к оформлению заказа")
    public CheckoutPage checkout() {
        checkout.click();
        return new CheckoutPage();
    }
}
