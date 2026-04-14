package com.saucedemo.tests.fixtures;

import com.saucedemo.framework.config.ConfigManager;
import com.saucedemo.framework.driver.DriverManager;
import com.saucedemo.framework.pages.InventoryPage;
import com.saucedemo.framework.pages.LoginPage;

/**
 * Single Responsibility (SOLID - S): Handles browser lifecycle and authentication setup only.
 * Implements IFixture (Dependency Inversion - SOLID - D).
 *
 * Fixture pattern: reusable setup/teardown block shared across test classes.
 * Tests call loginAsStandardUser() to arrive at a ready-to-use InventoryPage.
 */
public class AuthFixture implements IFixture {

    private static final String STANDARD_USER = "standard_user";
    private static final String PASSWORD       = "secret_sauce";

    @Override
    public void setUp() {
        DriverManager.initDriver();
        DriverManager.getDriver().get(ConfigManager.getInstance().getBaseUrl());
    }

    @Override
    public void tearDown() {
        DriverManager.quitDriver();
    }

    /** Logs in as the default standard_user and returns the InventoryPage. */
    public InventoryPage loginAsStandardUser() {
        return new LoginPage().login(STANDARD_USER, PASSWORD);
    }

    /** Logs in with arbitrary credentials and returns the InventoryPage. */
    public InventoryPage loginAs(String username, String password) {
        return new LoginPage().login(username, password);
    }

    /** Returns the LoginPage without performing a login (for negative-path tests). */
    public LoginPage getLoginPage() {
        return new LoginPage();
    }
}
