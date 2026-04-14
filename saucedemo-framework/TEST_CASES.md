# SauceDemo Test Cases

Automated end-to-end test suite for [https://www.saucedemo.com](https://www.saucedemo.com) built with **Selenium WebDriver 4 + TestNG 7**.

---

## Quick Stats

| Test Class | Positive | Negative | Total |
|---|---|---|---|
| LoginTest | 2 | 6 | **8** |
| InventoryTest | 10 | 2 | **12** |
| CartPageTest | 5 | 3 | **8** |
| CheckoutTest | 6 | 5 | **11** |
| **Grand Total** | **23** | **16** | **39** |

---

## How to Run

```bash
mvn test
```

Reports are written to:
- `reports/html/` — interactive HTML report (ExtentReports)
- `reports/pdf/` — PDF summary report
- `reports/screenshots/` — failure screenshots (captured automatically on test failure)

---

## Test Credentials

| User | Password | Used for |
|---|---|---|
| `standard_user` | `secret_sauce` | All passing-flow tests |
| `locked_out_user` | `secret_sauce` | Locked-out error test |
| `invalid_user` | `wrong_password` | Invalid credentials test |

---

## 1. Login Page (`LoginTest`)

Tests that cover authentication entry points — valid login, account restrictions, and form validation.

### ✅ Positive

| # | Test Method | What it verifies |
|---|---|---|
| 1 | `testSuccessfulLogin` | Standard user logs in and lands on the Inventory page with title **"Swag Labs"** |
| 2 | `testLoginPageIsLoaded` | The login form (username, password, login button) is visible when the site is opened |

### ❌ Negative

| # | Test Method | Scenario | Expected error |
|---|---|---|---|
| 3 | `testLockedOutUserShowsError` | `locked_out_user` with correct password | *"Sorry, this user has been locked out."* |
| 4 | `testInvalidCredentialsShowError` | Completely invalid username + password | *"Username and password do not match…"* |
| 5 | `testValidUsernameWrongPasswordShowsError` | Valid username + wrong password | *"Username and password do not match…"* |
| 6 | `testEmptyUsernameShowsError` | Password filled, username blank | *"Epic sadface: Username is required"* |
| 7 | `testEmptyPasswordShowsError` | Username filled, password blank | *"Epic sadface: Password is required"* |
| 8 | `testBothFieldsEmptyShowsError` | Both fields blank, click Login | *"Epic sadface: Username is required"* |

---

## 2. Inventory / Products Page (`InventoryTest`)

Tests that cover product listing, sort order, and cart badge interactions from the inventory page.

### ✅ Positive

| # | Test Method | What it verifies |
|---|---|---|
| 1 | `testInventoryPageLoads` | Inventory list is visible immediately after login |
| 2 | `testInventoryHasSixProducts` | Exactly **6** products are shown |
| 3 | `testDefaultSortIsNameAZ` | Default sort order is **Name A → Z** |
| 4 | `testSortByNameZA` | Selecting **Name Z → A** reverses alphabetical order |
| 5 | `testSortByPriceLowToHigh` | Selecting **Price (low → high)** produces ascending price order |
| 6 | `testSortByPriceHighToLow` | Selecting **Price (high → low)** produces descending price order |
| 7 | `testAddItemToCart` | Adding one item increments the cart badge from **0 → 1** |
| 8 | `testRemoveItemFromCart` | Removing a previously added item resets the badge back to **0** |
| 9 | `testAddMultipleItemsToCart` | Adding 3 different items shows badge count **3** |
| 10 | `testAddAndRemoveItemFullScenario` | Full add → verify count → remove → verify empty cart cycle |

### ❌ Negative

| # | Test Method | Scenario | Expected result |
|---|---|---|---|
| 11 | `testAddToCartButtonDisappearsAfterAdd` | After adding an item, check the "Add to cart" button for that item | Button is **gone** (replaced by "Remove") |
| 12 | `testCartBadgeAbsentOnEmptyCart` | Fresh session with no items added | Cart badge count is **0** (badge not shown) |

---

## 3. Shopping Cart Page (`CartPageTest`)

Tests that cover the cart page content, item counts, navigation back to inventory, and item removal.

### ✅ Positive

| # | Test Method | What it verifies |
|---|---|---|
| 1 | `testCartPageShowsAddedItem` | Cart page loads and displays the item added from the inventory |
| 2 | `testCartCountMatchesAddedItems` | Adding 2 items and opening cart shows **2** cart rows |
| 3 | `testAllAddedItemsAppearInCart` | All 3 items added (Backpack, Fleece Jacket, Onesie) appear in the cart |
| 4 | `testContinueShoppingReturnsToInventory` | "Continue Shopping" button navigates back to the inventory page |
| 5 | `testCheckoutButtonNavigatesToCheckoutStep1` | "Checkout" button navigates to **Checkout: Your Information** (Step 1) |

### ❌ Negative

| # | Test Method | Scenario | Expected result |
|---|---|---|---|
| 6 | `testEmptyCartHasNoItems` | Navigate to cart without adding any items | Cart shows **0** item rows |
| 7 | `testRemoveItemFromCartPage` | Add 1 item, open cart, click its "Remove" button | Cart item count drops to **0** |
| 8 | `testRemovedItemNotListedInCart` | Add 2 items, remove one from the cart page | Removed item is **absent**; remaining item is still **present** |

---

## 4. Checkout Flow (`CheckoutTest`)

Tests that cover the full 3-step checkout process (Step 1: Info → Step 2: Overview → Complete) and its validation rules.

### ✅ Positive

| # | Test Method | What it verifies |
|---|---|---|
| 1 | `testCheckoutStep1PageLoads` | After clicking Checkout, the **Your Information** form is visible |
| 2 | `testCompleteCheckoutFlow` | Happy path: fill info → review → finish → **"Thank you for your order!"** is shown |
| 3 | `testCheckoutStep2ShowsOrderSummary` | Step 2 shows the correct item (Backpack) in the order summary |
| 4 | `testCheckoutOverviewTotalIncludesTax` | Step 2 totals: item **$29.99** + tax **$2.40** = grand total **$32.39** |
| 5 | `testCheckoutStep2CancelReturnsToInventory` | Cancel on Step 2 returns to the **Inventory** page |
| 6 | `testBackHomeReturnsToInventory` | "Back Home" button on the Order Complete page returns to the **Inventory** page |

### ❌ Negative

| # | Test Method | Scenario | Expected error |
|---|---|---|---|
| 7 | `testCheckoutAllFieldsEmptyShowsError` | Click Continue with all fields empty | *"Error: First Name is required"* |
| 8 | `testCheckoutMissingFirstNameShowsError` | Fill Last Name + Postal Code only | *"Error: First Name is required"* |
| 9 | `testCheckoutMissingLastNameShowsError` | Fill First Name + Postal Code only | *"Error: Last Name is required"* |
| 10 | `testCheckoutMissingPostalCodeShowsError` | Fill First Name + Last Name only | *"Error: Postal Code is required"* |
| 11 | `testCheckoutStep1CancelReturnsToCart` | Click Cancel on Step 1 | Returns to the **Cart** page |

---

## Page Object Model

```
src/main/java/com/saucedemo/framework/
├── pages/
│   ├── BasePage.java              # Shared driver, waits, jsClick, fillInput helpers
│   ├── LoginPage.java
│   ├── InventoryPage.java
│   ├── CartPage.java
│   ├── CheckoutStepOnePage.java
│   ├── CheckoutStepTwoPage.java
│   └── CheckoutCompletePage.java
├── driver/
│   └── DriverManager.java         # ThreadLocal WebDriver (Chrome / Firefox)
├── utils/
│   ├── WaitHelper.java            # Explicit waits only (implicit wait = 0)
│   └── TestData.java              # All test constants in one place
└── config/
    └── ConfigManager.java         # config.properties reader

src/test/java/com/saucedemo/tests/
├── scenarios/
│   ├── LoginTest.java
│   ├── InventoryTest.java
│   ├── CartPageTest.java
│   └── CheckoutTest.java
├── base/
│   └── BaseTest.java              # @BeforeMethod / @AfterMethod browser lifecycle
├── fixtures/
│   └── AuthFixture.java           # Login helpers used by all test classes
└── listeners/
    └── TestListener.java          # ExtentReports + PDF report + screenshot on failure
```

---

## Key Design Decisions

| Decision | Reason |
|---|---|
| **JS executor for all button clicks** (`jsClick`) | React SPA attaches event handlers slightly after the DOM node becomes "clickable"; native Selenium clicks fired too early were silently ignored |
| **Element-based navigation waits** (not URL waits) | `waitForUrlContains` returns the moment the address bar updates — before React finishes rendering the destination page. Waiting for a visible destination element guarantees the page is interactive |
| **Native `HTMLInputElement` setter for form fields** (`fillInput`) | React overrides the `value` setter; using the native prototype setter + dispatching `input`/`change` events is the only reliable way to update React controlled inputs via WebDriver |
| **Zero implicit wait** | Prevents unpredictable mixed-wait behaviour; all synchronisation is done with explicit `WebDriverWait` |
| **ThreadLocal `WebDriver`** | Ensures each test gets an isolated browser instance; safe for parallel execution |
