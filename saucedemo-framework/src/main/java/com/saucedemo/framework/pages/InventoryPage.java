package com.saucedemo.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Single Responsibility (SOLID - S): Encapsulates all interactions with the Inventory page only.
 */
public class InventoryPage extends BasePage {

    @FindBy(className = "inventory_list")
    private WebElement inventoryList;

    @FindBy(css = ".inventory_item")
    private List<WebElement> inventoryItems;

    @FindBy(id = "shopping_cart_container")
    private WebElement cartIcon;

    @Override
    public boolean isPageLoaded() {
        try {
            return waitHelper.waitForVisibility(By.className("inventory_list")) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Adds a product to the cart by its display name.
     * Example: "Sauce Labs Backpack" -> data-test="add-to-cart-sauce-labs-backpack"
     */
    public void addItemToCartByName(String itemName) {
        log.info("Adding item to cart: {}", itemName);
        String slug = toSlug(itemName);
        jsClick(waitHelper.waitForClickability(
                By.cssSelector("[data-test='add-to-cart-" + slug + "']")));
        waitHelper.waitForVisibility(
                By.cssSelector("[data-test='remove-" + slug + "']"));
    }

    /**
     * Removes a product from the cart by its display name.
     * Example: "Sauce Labs Backpack" -> data-test="remove-sauce-labs-backpack"
     */
    public void removeItemFromCartByName(String itemName) {
        log.info("Removing item from cart: {}", itemName);
        String slug = toSlug(itemName);
        jsClick(waitHelper.waitForClickability(
                By.cssSelector("[data-test='remove-" + slug + "']")));
        waitHelper.waitForVisibility(
                By.cssSelector("[data-test='add-to-cart-" + slug + "']"));
    }

    public int getCartCount() {
        List<WebElement> badges = driver.findElements(By.cssSelector(".shopping_cart_badge"));
        if (badges.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(waitHelper.waitForVisibility(badges.get(0)).getText());
    }

    public boolean isCartEmpty() {
        return getCartCount() == 0;
    }

    public int getProductCount() {
        return driver.findElements(By.cssSelector(".inventory_item")).size();
    }

    public List<String> getProductNames() {
        return driver.findElements(By.cssSelector(".inventory_item_name"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<Double> getProductPrices() {
        return driver.findElements(By.cssSelector(".inventory_item_price"))
                .stream()
                .map(el -> Double.parseDouble(el.getText().replace("$", "")))
                .collect(Collectors.toList());
    }

    /**
     * Sorts the product list using the visible text of the sort option.
     * Use constants from TestData: SORT_NAME_AZ, SORT_NAME_ZA, SORT_PRICE_LH, SORT_PRICE_HL.
     */
    public void sortBy(String visibleText) {
        log.info("Sorting by: {}", visibleText);
        WebElement sortDropdown = waitHelper.waitForVisibility(
                By.cssSelector("[data-test='product-sort-container']"));
        new Select(sortDropdown).selectByVisibleText(visibleText);
    }

    /**
     * Navigates to the Cart page by clicking the cart icon.
     */
    public CartPage navigateToCart() {
        log.info("Navigating to cart");
        jsClick(waitHelper.waitForClickability(By.cssSelector("a.shopping_cart_link")));
        waitHelper.waitForVisibility(By.className("cart_list"));
        return new CartPage();
    }

    public List<WebElement> getInventoryItems() {
        return inventoryItems;
    }

    private String toSlug(String name) {
        return name.toLowerCase().replace(" ", "-");
    }
}
