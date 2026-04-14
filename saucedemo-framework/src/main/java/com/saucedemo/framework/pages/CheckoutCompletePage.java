package com.saucedemo.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Single Responsibility (SOLID - S): Encapsulates all interactions with the Checkout Complete page only.
 */
public class CheckoutCompletePage extends BasePage {

    @FindBy(css = "h2.complete-header")
    private WebElement completeHeader;

    @FindBy(css = ".complete-text")
    private WebElement completeText;

    @FindBy(id = "back-to-products")
    private WebElement backHomeButton;

    @Override
    public boolean isPageLoaded() {
        try {
            return waitHelper.waitForVisibility(By.cssSelector("h2.complete-header")) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public String getCompleteHeader() {
        return waitHelper.waitForVisibility(By.cssSelector("h2.complete-header")).getText();
    }

    public String getCompleteText() {
        return waitHelper.waitForVisibility(By.cssSelector(".complete-text")).getText();
    }

    public InventoryPage clickBackHome() {
        log.info("Clicking Back Home on checkout complete page");
        jsClick(waitHelper.waitForClickability(backHomeButton));
        waitHelper.waitForVisibility(By.className("inventory_list"));
        return new InventoryPage();
    }
}
