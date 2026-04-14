package com.saucedemo.tests.scenarios;

import com.saucedemo.framework.pages.CartPage;
import com.saucedemo.framework.pages.CheckoutStepOnePage;
import com.saucedemo.framework.pages.InventoryPage;
import com.saucedemo.framework.utils.TestData;
import com.saucedemo.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Positive and negative test scenarios covering the Shopping Cart page.
 *
 * Positive : cart shows added items, count matches, continue shopping, proceed to checkout
 * Negative : empty cart has no items, removed item disappears from cart
 */
public class CartPageTest extends BaseTest {

    // ── Positive ──────────────────────────────────────────────────────────────

    @Test(description = "Cart page loads and shows the item that was added from inventory")
    public void testCartPageShowsAddedItem() {
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();
        inventoryPage.addItemToCartByName(TestData.BACKPACK);
        CartPage cartPage = inventoryPage.navigateToCart();

        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should load correctly");
        Assert.assertTrue(cartPage.isItemInCart(TestData.BACKPACK),
                "The Backpack should appear in the cart");
    }

    @Test(description = "Cart item count matches the number of items added from inventory")
    public void testCartCountMatchesAddedItems() {
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();
        inventoryPage.addItemToCartByName(TestData.BACKPACK);
        inventoryPage.addItemToCartByName(TestData.BIKE_LIGHT);
        CartPage cartPage = inventoryPage.navigateToCart();

        Assert.assertEquals(cartPage.getCartItemCount(), 2,
                "Cart page should list 2 items after adding 2 from inventory");
    }

    @Test(description = "Continue Shopping button navigates back to the inventory page")
    public void testContinueShoppingReturnsToInventory() {
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();
        inventoryPage.addItemToCartByName(TestData.BACKPACK);
        CartPage cartPage = inventoryPage.navigateToCart();

        InventoryPage backToInventory = cartPage.clickContinueShopping();
        Assert.assertTrue(backToInventory.isPageLoaded(),
                "Should return to the inventory page after clicking Continue Shopping");
    }

    @Test(description = "Checkout button from cart navigates to Checkout Step 1 page")
    public void testCheckoutButtonNavigatesToCheckoutStep1() {
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();
        inventoryPage.addItemToCartByName(TestData.BACKPACK);
        CartPage cartPage = inventoryPage.navigateToCart();

        CheckoutStepOnePage step1 = cartPage.clickCheckout();
        Assert.assertTrue(step1.isPageLoaded(),
                "Checkout Step 1 page should load after clicking Checkout");
    }

    @Test(description = "Multiple items added to cart are all listed on the cart page")
    public void testAllAddedItemsAppearInCart() {
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();
        inventoryPage.addItemToCartByName(TestData.BACKPACK);
        inventoryPage.addItemToCartByName(TestData.FLEECE_JACKET);
        inventoryPage.addItemToCartByName(TestData.ONESIE);
        CartPage cartPage = inventoryPage.navigateToCart();

        Assert.assertTrue(cartPage.isItemInCart(TestData.BACKPACK),    "Backpack should be in cart");
        Assert.assertTrue(cartPage.isItemInCart(TestData.FLEECE_JACKET), "Fleece Jacket should be in cart");
        Assert.assertTrue(cartPage.isItemInCart(TestData.ONESIE),      "Onesie should be in cart");
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test(description = "Cart page with no items added shows 0 cart items")
    public void testEmptyCartHasNoItems() {
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();
        CartPage cartPage = inventoryPage.navigateToCart();

        Assert.assertEquals(cartPage.getCartItemCount(), 0,
                "Cart should show 0 items when nothing was added");
    }

    @Test(description = "Removing an item directly from the cart page makes it disappear")
    public void testRemoveItemFromCartPage() {
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();
        inventoryPage.addItemToCartByName(TestData.BACKPACK);
        CartPage cartPage = inventoryPage.navigateToCart();

        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Precondition: 1 item in cart");
        cartPage.removeItemByName(TestData.BACKPACK);
        Assert.assertEquals(cartPage.getCartItemCount(), 0,
                "Cart should be empty after removing the item from cart page");
    }

    @Test(description = "After removing an item from the cart page, that item no longer appears in the list")
    public void testRemovedItemNotListedInCart() {
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();
        inventoryPage.addItemToCartByName(TestData.BACKPACK);
        inventoryPage.addItemToCartByName(TestData.BIKE_LIGHT);
        CartPage cartPage = inventoryPage.navigateToCart();

        cartPage.removeItemByName(TestData.BACKPACK);

        Assert.assertFalse(cartPage.isItemInCart(TestData.BACKPACK),
                "Backpack should no longer appear in cart after removal");
        Assert.assertTrue(cartPage.isItemInCart(TestData.BIKE_LIGHT),
                "Bike Light should still be in the cart");
    }
}
