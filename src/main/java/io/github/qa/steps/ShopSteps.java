package io.github.qa.steps;

import io.github.qa.core.Config;
import io.github.qa.data.Customer;
import io.github.qa.pages.CheckoutPage;
import io.github.qa.pages.InventoryPage;
import io.github.qa.pages.LoginPage;
import io.qameta.allure.Step;

public class ShopSteps {

    @Step("Войти стандартным пользователем")
    public InventoryPage loginAsStandardUser() {
        return new LoginPage().open().loginAs(Config.get("ui.user"), Config.get("ui.password"));
    }

    @Step("Купить товар '{product}'")
    public CheckoutPage buy(String product, Customer customer) {
        return loginAsStandardUser()
                .addToCart(product)
                .openCart()
                .checkout()
                .fillCustomer(customer)
                .finish();
    }
}
