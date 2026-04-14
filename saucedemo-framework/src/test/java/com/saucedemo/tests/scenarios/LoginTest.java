package com.saucedemo.tests.scenarios;

import com.saucedemo.framework.pages.InventoryPage;
import com.saucedemo.framework.pages.LoginPage;
import com.saucedemo.framework.utils.TestData;
import com.saucedemo.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Positive and negative test scenarios covering the Login page.
 *
 * Positive : successful login with standard_user
 * Negative : locked-out user, invalid credentials, empty username, empty password, both empty
 */
public class LoginTest extends BaseTest {

    // ── Positive ──────────────────────────────────────────────────────────────

    @Test(description = "Successful login with standard_user redirects to inventory page")
    public void testSuccessfulLogin() {
        InventoryPage inventoryPage = authFixture.loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isPageLoaded(),
                "Inventory page should load after a successful login");
        Assert.assertEquals(inventoryPage.getPageTitle(), "Swag Labs",
                "Page title should be 'Swag Labs'");
    }

    @Test(description = "Login page is accessible and displays the login form")
    public void testLoginPageIsLoaded() {
        LoginPage loginPage = authFixture.getLoginPage();
        Assert.assertTrue(loginPage.isPageLoaded(),
                "Login page should display the login form");
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test(description = "Locked-out user receives the locked-out error message")
    public void testLockedOutUserShowsError() {
        LoginPage loginPage = authFixture.getLoginPage();
        loginPage.enterUsername(TestData.LOCKED_OUT_USER);
        loginPage.enterPassword(TestData.VALID_PASSWORD);
        loginPage.clickLogin();
        Assert.assertTrue(loginPage.hasError(), "Error banner should appear for locked-out user");
        Assert.assertTrue(loginPage.getErrorMessage().contains("locked out"),
                "Error should mention 'locked out', got: " + loginPage.getErrorMessage());
    }

    @Test(description = "Invalid username and password shows credentials-mismatch error")
    public void testInvalidCredentialsShowError() {
        LoginPage loginPage = authFixture.getLoginPage();
        loginPage.enterUsername(TestData.INVALID_USER);
        loginPage.enterPassword(TestData.INVALID_PASSWORD);
        loginPage.clickLogin();
        Assert.assertTrue(loginPage.hasError(), "Error banner should appear for invalid credentials");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username and password do not match"),
                "Unexpected error text: " + loginPage.getErrorMessage());
    }

    @Test(description = "Submitting with empty username shows 'Username is required' error")
    public void testEmptyUsernameShowsError() {
        LoginPage loginPage = authFixture.getLoginPage();
        loginPage.enterPassword(TestData.VALID_PASSWORD);
        loginPage.clickLogin();
        Assert.assertTrue(loginPage.hasError(), "Error banner should appear for empty username");
        Assert.assertEquals(loginPage.getErrorMessage(), TestData.ERR_MISSING_USERNAME,
                "Unexpected error text: " + loginPage.getErrorMessage());
    }

    @Test(description = "Submitting with empty password shows 'Password is required' error")
    public void testEmptyPasswordShowsError() {
        LoginPage loginPage = authFixture.getLoginPage();
        loginPage.enterUsername(TestData.STANDARD_USER);
        loginPage.clickLogin();
        Assert.assertTrue(loginPage.hasError(), "Error banner should appear for empty password");
        Assert.assertEquals(loginPage.getErrorMessage(), TestData.ERR_MISSING_PASSWORD,
                "Unexpected error text: " + loginPage.getErrorMessage());
    }

    @Test(description = "Submitting with both fields empty shows 'Username is required' error")
    public void testBothFieldsEmptyShowsError() {
        LoginPage loginPage = authFixture.getLoginPage();
        loginPage.clickLogin();
        Assert.assertTrue(loginPage.hasError(), "Error banner should appear when both fields are empty");
        Assert.assertEquals(loginPage.getErrorMessage(), TestData.ERR_MISSING_USERNAME,
                "Unexpected error text: " + loginPage.getErrorMessage());
    }

    @Test(description = "Valid username with wrong password shows credentials-mismatch error")
    public void testValidUsernameWrongPasswordShowsError() {
        LoginPage loginPage = authFixture.getLoginPage();
        loginPage.enterUsername(TestData.STANDARD_USER);
        loginPage.enterPassword(TestData.INVALID_PASSWORD);
        loginPage.clickLogin();
        Assert.assertTrue(loginPage.hasError(), "Error banner should appear for wrong password");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username and password do not match"),
                "Unexpected error text: " + loginPage.getErrorMessage());
    }
}
