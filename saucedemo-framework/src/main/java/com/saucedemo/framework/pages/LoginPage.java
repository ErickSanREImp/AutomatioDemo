package com.saucedemo.framework.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Single Responsibility (SOLID - S): Encapsulates all interactions with the Login page only.
 */
public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    @Override
    public boolean isPageLoaded() {
        try {
            return waitHelper.waitForVisibility(loginButton) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void enterUsername(String username) {
        log.info("Entering username: {}", username);
        waitHelper.waitForVisibility(usernameField).clear();
        usernameField.sendKeys(username);
    }

    public void enterPassword(String password) {
        log.info("Entering password");
        waitHelper.waitForVisibility(passwordField).clear();
        passwordField.sendKeys(password);
    }

    public void clickLogin() {
        log.info("Clicking login button");
        jsClick(waitHelper.waitForClickability(loginButton));
    }

    public InventoryPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        waitHelper.waitForUrlContains("inventory");
        return new InventoryPage();
    }

    public String getErrorMessage() {
        return waitHelper.waitForVisibility(errorMessage).getText();
    }

    public boolean hasError() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
