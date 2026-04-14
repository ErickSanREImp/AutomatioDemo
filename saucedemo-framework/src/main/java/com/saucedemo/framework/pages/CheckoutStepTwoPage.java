package com.saucedemo.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Single Responsibility (SOLID - S): Encapsulates all interactions with Checkout Step 2 only.
 */
public class CheckoutStepTwoPage extends BasePage {

    @FindBy(css = ".summary_info")
    private WebElement summaryInfo;

    @FindBy(css = ".summary_subtotal_label")
    private WebElement itemTotalLabel;

    @FindBy(css = ".summary_tax_label")
    private WebElement taxLabel;

    @FindBy(css = ".summary_total_label")
    private WebElement totalLabel;

    @FindBy(css = ".cart_item")
    private List<WebElement> orderItems;

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    @Override
    public boolean isPageLoaded() {
        try {
            return waitHelper.waitForVisibility(By.cssSelector(".summary_info")) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public int getOrderItemCount() {
        return driver.findElements(By.cssSelector(".cart_item")).size();
    }

    public String getItemTotal() {
        return waitHelper.waitForVisibility(By.cssSelector(".summary_subtotal_label")).getText();
    }

    public String getTax() {
        return waitHelper.waitForVisibility(By.cssSelector(".summary_tax_label")).getText();
    }

    public String getTotal() {
        return waitHelper.waitForVisibility(By.cssSelector(".summary_total_label")).getText();
    }

    public CheckoutCompletePage clickFinish() {
        log.info("Clicking Finish on checkout step 2");
        jsClick(waitHelper.waitForClickability(finishButton));
        waitHelper.waitForVisibility(By.cssSelector("h2.complete-header"));
        return new CheckoutCompletePage();
    }

    public InventoryPage clickCancel() {
        log.info("Clicking Cancel on checkout step 2");
        jsClick(waitHelper.waitForClickability(cancelButton));
        waitHelper.waitForVisibility(By.className("inventory_list"));
        return new InventoryPage();
    }
}
