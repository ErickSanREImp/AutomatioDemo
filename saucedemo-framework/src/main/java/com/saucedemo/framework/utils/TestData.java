package com.saucedemo.framework.utils;

/**
 * Central repository of all test constants.
 * Single Responsibility (SOLID - S): Only holds static test data — no logic.
 */
public final class TestData {

    private TestData() {}

    // ── Users ────────────────────────────────────────────────────────────────
    public static final String STANDARD_USER             = "standard_user";
    public static final String LOCKED_OUT_USER           = "locked_out_user";
    public static final String PROBLEM_USER              = "problem_user";
    public static final String PERFORMANCE_GLITCH_USER   = "performance_glitch_user";
    public static final String ERROR_USER                = "error_user";
    public static final String VISUAL_USER               = "visual_user";
    public static final String INVALID_USER              = "invalid_user";

    public static final String VALID_PASSWORD   = "secret_sauce";
    public static final String INVALID_PASSWORD = "wrong_password";

    // ── Products ─────────────────────────────────────────────────────────────
    public static final String BACKPACK      = "Sauce Labs Backpack";
    public static final String BIKE_LIGHT    = "Sauce Labs Bike Light";
    public static final String BOLT_TSHIRT   = "Sauce Labs Bolt T-Shirt";
    public static final String FLEECE_JACKET = "Sauce Labs Fleece Jacket";
    public static final String ONESIE        = "Sauce Labs Onesie";
    public static final String TSHIRT_RED    = "Test.allTheThings() T-Shirt (Red)";

    public static final int TOTAL_PRODUCTS = 6;

    // ── Sort options (visible text in the dropdown) ───────────────────────────
    public static final String SORT_NAME_AZ    = "Name (A to Z)";
    public static final String SORT_NAME_ZA    = "Name (Z to A)";
    public static final String SORT_PRICE_LH   = "Price (low to high)";
    public static final String SORT_PRICE_HL   = "Price (high to low)";

    // ── Checkout info ─────────────────────────────────────────────────────────
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME  = "Doe";
    public static final String ZIP_CODE   = "12345";

    // ── Login error messages ──────────────────────────────────────────────────
    public static final String ERR_INVALID_CREDENTIALS =
            "Epic sadface: Username and password do not match any user in this service";
    public static final String ERR_LOCKED_OUT =
            "Epic sadface: Sorry, this user has been locked out.";
    public static final String ERR_MISSING_USERNAME =
            "Epic sadface: Username is required";
    public static final String ERR_MISSING_PASSWORD =
            "Epic sadface: Password is required";

    // ── Checkout error messages ───────────────────────────────────────────────
    public static final String ERR_FIRST_NAME_REQUIRED  = "Error: First Name is required";
    public static final String ERR_LAST_NAME_REQUIRED   = "Error: Last Name is required";
    public static final String ERR_POSTAL_CODE_REQUIRED = "Error: Postal Code is required";

    // ── Checkout complete ─────────────────────────────────────────────────────
    public static final String CHECKOUT_COMPLETE_HEADER = "Thank you for your order!";
}
