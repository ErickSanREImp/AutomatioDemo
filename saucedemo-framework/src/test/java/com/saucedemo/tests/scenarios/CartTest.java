package com.saucedemo.tests.scenarios;

import com.saucedemo.framework.pages.InventoryPage;
import com.saucedemo.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test scenarios covering the shopping cart on the Inventory page.
 *
 * Scenario 1: Adding an item increases the cart badge count to 1
 * Scenario 2: Removing an added item empties the cart
 * Scenario 3: Full add → remove cycle (mirrors the Playwright automation scenario)
 */
public class CartTest extends BaseTest {

    private static final String ITEM_NAME = "Sauce Labs Backpack";

    @Test(description = "Adding an item to the cart shows a badge count of 1")
    public void testAddItemToCart() {
        log.info("► testAddItemToCart");
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();

        Assert.assertTrue(inventoryPage.isCartEmpty(),
                "Cart should be empty before adding any item");

        inventoryPage.addItemToCartByName(ITEM_NAME);

        Assert.assertEquals(inventoryPage.getCartCount(), 1,
                "Cart count should be 1 after adding one item");
    }

    @Test(description = "Removing an item from the cart empties the cart badge")
    public void testRemoveItemFromCart() {
        log.info("► testRemoveItemFromCart");
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();

        inventoryPage.addItemToCartByName(ITEM_NAME);
        Assert.assertEquals(inventoryPage.getCartCount(), 1,
                "Precondition: cart should have 1 item");

        inventoryPage.removeItemFromCartByName(ITEM_NAME);

        Assert.assertTrue(inventoryPage.isCartEmpty(),
                "Cart should be empty after removing the item");
    }

    @Test(description = "Full add-then-remove scenario matches the automated Playwright run")
    public void testAddAndRemoveItemFullScenario() {
        log.info("► testAddAndRemoveItemFullScenario");
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();

        Assert.assertTrue(inventoryPage.isPageLoaded(),
                "Inventory page should be loaded after login");
        Assert.assertTrue(inventoryPage.isCartEmpty(),
                "Cart should start empty");

        inventoryPage.addItemToCartByName(ITEM_NAME);
        Assert.assertEquals(inventoryPage.getCartCount(), 1,
                "Cart should show 1 item after adding " + ITEM_NAME);

        inventoryPage.removeItemFromCartByName(ITEM_NAME);
        Assert.assertTrue(inventoryPage.isCartEmpty(),
                "Cart should be empty after removing " + ITEM_NAME);
    }
}
