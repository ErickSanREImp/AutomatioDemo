package com.saucedemo.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Single Responsibility (SOLID - S): Encapsulates all interactions with Checkout Step 1 only.
 */
public class CheckoutStepOnePage extends BasePage {

    @FindBy(id = "first-name")
    private WebElement firstNameField;

    @FindBy(id = "last-name")
    private WebElement lastNameField;

    @FindBy(id = "postal-code")
    private WebElement postalCodeField;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    @Override
    public boolean isPageLoaded() {
        try {
            return waitHelper.waitForVisibility(firstNameField) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void enterFirstName(String firstName) {
        log.info("Entering first name: {}", firstName);
        fillInput(firstNameField, firstName);
    }

    public void enterLastName(String lastName) {
        log.info("Entering last name: {}", lastName);
        fillInput(lastNameField, lastName);
    }

    public void enterPostalCode(String postalCode) {
        log.info("Entering postal code: {}", postalCode);
        fillInput(postalCodeField, postalCode);
    }

    public void clickContinue() {
        log.info("Clicking Continue on checkout step 1");
        jsClick(waitHelper.waitForClickability(continueButton));
    }

    public CartPage clickCancel() {
        log.info("Clicking Cancel on checkout step 1");
        jsClick(waitHelper.waitForClickability(cancelButton));
        waitHelper.waitForVisibility(By.className("cart_list"));
        return new CartPage();
    }

    public String getErrorMessage() {
        return waitHelper.waitForVisibility(By.cssSelector("[data-test='error']")).getText();
    }

    public boolean hasError() {
        try {
            return waitHelper.waitForVisibility(errorMessage) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public CheckoutStepTwoPage fillAndContinue(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        jsClick(waitHelper.waitForClickability(continueButton));
        waitHelper.waitForVisibility(By.cssSelector(".summary_info"));
        return new CheckoutStepTwoPage();
    }
}
