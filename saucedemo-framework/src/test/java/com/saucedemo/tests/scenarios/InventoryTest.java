package com.saucedemo.tests.scenarios;

import com.saucedemo.framework.pages.InventoryPage;
import com.saucedemo.framework.utils.TestData;
import com.saucedemo.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Positive and negative test scenarios covering the Inventory (Products) page.
 *
 * Positive : page loads, correct product count, sorting works, add/remove single and multiple items
 * Negative : add then attempt to add again (button disappears), remove non-added item is not visible
 */
public class InventoryTest extends BaseTest {

    // ── Positive ──────────────────────────────────────────────────────────────

    @Test(description = "Inventory page loads correctly after login")
    public void testInventoryPageLoads() {
        InventoryPage page = authFixture.loginAsStandardUser();
        Assert.assertTrue(page.isPageLoaded(), "Inventory list must be visible after login");
    }

    @Test(description = "Inventory displays exactly 6 products")
    public void testInventoryHasSixProducts() {
        InventoryPage page = authFixture.loginAsStandardUser();
        Assert.assertEquals(page.getProductCount(), TestData.TOTAL_PRODUCTS,
                "There should be exactly 6 products on the inventory page");
    }

    @Test(description = "Default sort is Name A→Z; names are in ascending alphabetical order")
    public void testDefaultSortIsNameAZ() {
        InventoryPage page = authFixture.loginAsStandardUser();
        List<String> names = page.getProductNames();
        List<String> sorted = names.stream().sorted().collect(java.util.stream.Collectors.toList());
        Assert.assertEquals(names, sorted, "Products should be sorted A→Z by default");
    }

    @Test(description = "Sort by Name Z→A reverses the alphabetical order")
    public void testSortByNameZA() {
        InventoryPage page = authFixture.loginAsStandardUser();
        page.sortBy(TestData.SORT_NAME_ZA);
        List<String> names = page.getProductNames();
        List<String> expected = names.stream()
                .sorted(java.util.Comparator.reverseOrder())
                .collect(java.util.stream.Collectors.toList());
        Assert.assertEquals(names, expected, "Products should be sorted Z→A");
    }

    @Test(description = "Sort by Price low→high produces ascending price order")
    public void testSortByPriceLowToHigh() {
        InventoryPage page = authFixture.loginAsStandardUser();
        page.sortBy(TestData.SORT_PRICE_LH);
        List<Double> prices = page.getProductPrices();
        for (int i = 0; i < prices.size() - 1; i++) {
            Assert.assertTrue(prices.get(i) <= prices.get(i + 1),
                    "Price at index " + i + " (" + prices.get(i) + ") should be ≤ " + prices.get(i + 1));
        }
    }

    @Test(description = "Sort by Price high→low produces descending price order")
    public void testSortByPriceHighToLow() {
        InventoryPage page = authFixture.loginAsStandardUser();
        page.sortBy(TestData.SORT_PRICE_HL);
        List<Double> prices = page.getProductPrices();
        for (int i = 0; i < prices.size() - 1; i++) {
            Assert.assertTrue(prices.get(i) >= prices.get(i + 1),
                    "Price at index " + i + " (" + prices.get(i) + ") should be ≥ " + prices.get(i + 1));
        }
    }

    @Test(description = "Adding one item increments cart badge to 1")
    public void testAddItemToCart() {
        InventoryPage page = authFixture.loginAsStandardUser();
        Assert.assertTrue(page.isCartEmpty(), "Cart should be empty before adding any item");
        page.addItemToCartByName(TestData.BACKPACK);
        Assert.assertEquals(page.getCartCount(), 1, "Cart count should be 1 after adding one item");
    }

    @Test(description = "Removing a previously added item resets the cart to empty")
    public void testRemoveItemFromCart() {
        InventoryPage page = authFixture.loginAsStandardUser();
        page.addItemToCartByName(TestData.BACKPACK);
        Assert.assertEquals(page.getCartCount(), 1, "Precondition: cart should have 1 item");
        page.removeItemFromCartByName(TestData.BACKPACK);
        Assert.assertTrue(page.isCartEmpty(), "Cart should be empty after removing the item");
    }

    @Test(description = "Adding multiple different items reflects the correct cart badge count")
    public void testAddMultipleItemsToCart() {
        InventoryPage page = authFixture.loginAsStandardUser();
        page.addItemToCartByName(TestData.BACKPACK);
        page.addItemToCartByName(TestData.BIKE_LIGHT);
        page.addItemToCartByName(TestData.BOLT_TSHIRT);
        Assert.assertEquals(page.getCartCount(), 3, "Cart count should be 3 after adding 3 items");
    }

    @Test(description = "Full add-and-remove cycle leaves the cart empty")
    public void testAddAndRemoveItemFullScenario() {
        InventoryPage page = authFixture.loginAsStandardUser();
        Assert.assertTrue(page.isPageLoaded(), "Inventory page must be loaded");
        Assert.assertTrue(page.isCartEmpty(), "Cart should be empty at start");
        page.addItemToCartByName(TestData.BACKPACK);
        Assert.assertEquals(page.getCartCount(), 1, "Cart count must be 1 after add");
        page.removeItemFromCartByName(TestData.BACKPACK);
        Assert.assertTrue(page.isCartEmpty(), "Cart should be empty after remove");
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test(description = "After adding an item, the Add-to-cart button is no longer visible (replaced by Remove)")
    public void testAddToCartButtonDisappearsAfterAdd() {
        InventoryPage page = authFixture.loginAsStandardUser();
        page.addItemToCartByName(TestData.BACKPACK);
        String slug = "sauce-labs-backpack";
        boolean addButtonGone = page.getDriver()
                .findElements(org.openqa.selenium.By.cssSelector(
                        "[data-test='add-to-cart-" + slug + "']"))
                .isEmpty();
        Assert.assertTrue(addButtonGone,
                "The 'Add to cart' button should not be visible after the item is in the cart");
    }

    @Test(description = "Cart badge is absent when no items have been added")
    public void testCartBadgeAbsentOnEmptyCart() {
        InventoryPage page = authFixture.loginAsStandardUser();
        Assert.assertEquals(page.getCartCount(), 0,
                "Cart badge / count should be 0 on a fresh session");
    }
}
