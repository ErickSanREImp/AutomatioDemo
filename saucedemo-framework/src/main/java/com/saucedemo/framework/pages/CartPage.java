package com.saucedemo.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Single Responsibility (SOLID - S): Encapsulates all interactions with the Cart page only.
 */
public class CartPage extends BasePage {

    @FindBy(className = "cart_list")
    private WebElement cartList;

    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @Override
    public boolean isPageLoaded() {
        try {
            return waitHelper.waitForVisibility(By.className("cart_list")) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public int getCartItemCount() {
        return driver.findElements(By.cssSelector(".cart_item")).size();
    }

    public boolean isItemInCart(String itemName) {
        return driver.findElements(By.cssSelector(".inventory_item_name"))
                .stream()
                .anyMatch(el -> el.getText().equalsIgnoreCase(itemName));
    }

    public List<String> getCartItemNames() {
        return driver.findElements(By.cssSelector(".inventory_item_name"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public void removeItemByName(String itemName) {
        log.info("Removing '{}' from cart page", itemName);
        String slug = toSlug(itemName);
        By removeLocator = By.cssSelector("[data-test='remove-" + slug + "']");
        jsClick(waitHelper.waitForClickability(removeLocator));
        waitHelper.waitForInvisibility(removeLocator);
    }

    public InventoryPage clickContinueShopping() {
        log.info("Clicking Continue Shopping");
        jsClick(waitHelper.waitForClickability(By.id("continue-shopping")));
        waitHelper.waitForVisibility(By.className("inventory_list"));
        return new InventoryPage();
    }

    public CheckoutStepOnePage clickCheckout() {
        log.info("Clicking Checkout");
        jsClick(waitHelper.waitForClickability(By.id("checkout")));
        waitHelper.waitForVisibility(By.id("first-name"));
        return new CheckoutStepOnePage();
    }

    private String toSlug(String name) {
        return name.toLowerCase().replace(" ", "-");
    }
}
