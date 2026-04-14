package com.saucedemo.tests.scenarios;

import com.saucedemo.framework.pages.*;
import com.saucedemo.framework.utils.TestData;
import com.saucedemo.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Positive and negative test scenarios covering the full Checkout flow.
 *
 * Positive : complete checkout (step1 → step2 → complete), overview totals, back-home navigation
 * Negative : empty first/last name, empty zip, cancel returns to correct page
 */
public class CheckoutTest extends BaseTest {

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CartPage cartWithBackpack() {
        InventoryPage inv = authFixture.loginAsStandardUser();
        inv.addItemToCartByName(TestData.BACKPACK);
        return inv.navigateToCart();
    }

    // ── Positive ──────────────────────────────────────────────────────────────

    @Test(description = "Complete happy-path checkout shows the order-confirmation page")
    public void testCompleteCheckoutFlow() {
        CheckoutCompletePage completePage = cartWithBackpack()
                .clickCheckout()
                .fillAndContinue(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.ZIP_CODE)
                .clickFinish();

        Assert.assertTrue(completePage.isPageLoaded(),
                "Checkout complete page should load after finishing checkout");
        Assert.assertEquals(completePage.getCompleteHeader(), TestData.CHECKOUT_COMPLETE_HEADER,
                "Complete header should read 'Thank you for your order!'");
    }

    @Test(description = "Checkout Step 1 page loads with all required fields")
    public void testCheckoutStep1PageLoads() {
        CheckoutStepOnePage step1 = cartWithBackpack().clickCheckout();
        Assert.assertTrue(step1.isPageLoaded(),
                "Checkout Step 1 page should be visible after clicking Checkout");
    }

    @Test(description = "Checkout Step 2 shows the correct item in the order summary")
    public void testCheckoutStep2ShowsOrderSummary() {
        CheckoutStepTwoPage step2 = cartWithBackpack()
                .clickCheckout()
                .fillAndContinue(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.ZIP_CODE);

        Assert.assertTrue(step2.isPageLoaded(), "Checkout Step 2 page should load");
        Assert.assertEquals(step2.getOrderItemCount(), 1,
                "Order summary should list exactly 1 item");
    }

    @Test(description = "Checkout Step 2 total includes item price plus tax")
    public void testCheckoutOverviewTotalIncludesTax() {
        CheckoutStepTwoPage step2 = cartWithBackpack()
                .clickCheckout()
                .fillAndContinue(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.ZIP_CODE);

        String itemTotal = step2.getItemTotal();
        String tax       = step2.getTax();
        String total     = step2.getTotal();

        Assert.assertTrue(itemTotal.contains("29.99"), "Item total should include $29.99");
        Assert.assertTrue(tax.contains("2.40"),        "Tax should be $2.40 for this item");
        Assert.assertTrue(total.contains("32.39"),     "Grand total should be $32.39");
    }

    @Test(description = "Back Home button on complete page navigates back to inventory")
    public void testBackHomeReturnsToInventory() {
        InventoryPage inventoryPage = cartWithBackpack()
                .clickCheckout()
                .fillAndContinue(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.ZIP_CODE)
                .clickFinish()
                .clickBackHome();

        Assert.assertTrue(inventoryPage.isPageLoaded(),
                "Back Home should navigate to the inventory page");
    }

    @Test(description = "Checkout cancel on Step 2 returns to the inventory page")
    public void testCheckoutStep2CancelReturnsToInventory() {
        InventoryPage inventoryPage = cartWithBackpack()
                .clickCheckout()
                .fillAndContinue(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.ZIP_CODE)
                .clickCancel();

        Assert.assertTrue(inventoryPage.isPageLoaded(),
                "Cancelling on Step 2 should return to the inventory page");
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test(description = "Submitting checkout Step 1 without First Name shows validation error")
    public void testCheckoutMissingFirstNameShowsError() {
        CheckoutStepOnePage step1 = cartWithBackpack().clickCheckout();
        step1.enterLastName(TestData.LAST_NAME);
        step1.enterPostalCode(TestData.ZIP_CODE);
        step1.clickContinue();

        Assert.assertTrue(step1.hasError(), "Error should appear when First Name is empty");
        Assert.assertEquals(step1.getErrorMessage(), TestData.ERR_FIRST_NAME_REQUIRED,
                "Error message mismatch: " + step1.getErrorMessage());
    }

    @Test(description = "Submitting checkout Step 1 without Last Name shows validation error")
    public void testCheckoutMissingLastNameShowsError() {
        CheckoutStepOnePage step1 = cartWithBackpack().clickCheckout();
        step1.enterFirstName(TestData.FIRST_NAME);
        step1.enterPostalCode(TestData.ZIP_CODE);
        step1.clickContinue();

        Assert.assertTrue(step1.hasError(), "Error should appear when Last Name is empty");
        Assert.assertEquals(step1.getErrorMessage(), TestData.ERR_LAST_NAME_REQUIRED,
                "Error message mismatch: " + step1.getErrorMessage());
    }

    @Test(description = "Submitting checkout Step 1 without Postal Code shows validation error")
    public void testCheckoutMissingPostalCodeShowsError() {
        CheckoutStepOnePage step1 = cartWithBackpack().clickCheckout();
        step1.enterFirstName(TestData.FIRST_NAME);
        step1.enterLastName(TestData.LAST_NAME);
        step1.clickContinue();

        Assert.assertTrue(step1.hasError(), "Error should appear when Postal Code is empty");
        Assert.assertEquals(step1.getErrorMessage(), TestData.ERR_POSTAL_CODE_REQUIRED,
                "Error message mismatch: " + step1.getErrorMessage());
    }

    @Test(description = "Submitting all empty fields on checkout Step 1 shows First Name required error")
    public void testCheckoutAllFieldsEmptyShowsError() {
        CheckoutStepOnePage step1 = cartWithBackpack().clickCheckout();
        step1.clickContinue();

        Assert.assertTrue(step1.hasError(), "Error should appear when all checkout fields are empty");
        Assert.assertEquals(step1.getErrorMessage(), TestData.ERR_FIRST_NAME_REQUIRED,
                "Error message mismatch: " + step1.getErrorMessage());
    }

    @Test(description = "Cancel button on checkout Step 1 returns to the cart page")
    public void testCheckoutStep1CancelReturnsToCart() {
        CartPage cartPage = cartWithBackpack()
                .clickCheckout()
                .clickCancel();

        Assert.assertTrue(cartPage.isPageLoaded(),
                "Cancelling on Step 1 should return to the cart page");
    }
}
