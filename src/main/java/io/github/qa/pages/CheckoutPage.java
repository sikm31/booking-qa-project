package io.github.qa.pages;

import com.microsoft.playwright.Locator;
import io.github.qa.data.Customer;
import io.qameta.allure.Step;

public class CheckoutPage extends BasePage {

    private final Locator firstName = page.getByTestId("firstName");
    private final Locator lastName = page.getByTestId("lastName");
    private final Locator postalCode = page.getByTestId("postalCode");
    private final Locator continueButton = page.getByTestId("continue");
    private final Locator finishButton = page.getByTestId("finish");
    private final Locator totalLabel = page.getByTestId("total-label");
    private final Locator completeHeader = page.getByTestId("complete-header");

    @Step("Заполнить данные покупателя: {customer.firstName} {customer.lastName}")
    public CheckoutPage fillCustomer(Customer customer) {
        firstName.fill(customer.firstName());
        lastName.fill(customer.lastName());
        postalCode.fill(customer.postalCode());
        continueButton.click();
        return this;
    }

    @Step("Подтвердить заказ")
    public CheckoutPage finish() {
        finishButton.click();
        return this;
    }

    public Locator total() {
        return totalLabel;
    }

    public Locator completeHeader() {
        return completeHeader;
    }
}
