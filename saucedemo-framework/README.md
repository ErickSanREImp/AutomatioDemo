# SauceDemo Automation Framework

Selenium + Java automation framework for [saucedemo.com](https://www.saucedemo.com),
covering the scenarios previously run with Playwright-CLI:

1. **Login** with `standard_user` / `secret_sauce`
2. **Add** *Sauce Labs Backpack* to the cart
3. **Remove** *Sauce Labs Backpack* from the cart

---

## Tech Stack

| Layer | Technology |
|---|---|
| Browser automation | Selenium WebDriver 4.18 |
| Driver management | WebDriverManager 5.7 |
| Test runner | TestNG 7.9 |
| Build tool | Maven |
| Logging | SLF4J + Logback |
| Reporting | ExtentReports 5 |
| Language | Java 11 |

---

## Project Structure

```
saucedemo-framework/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/saucedemo/framework/
    │   │   ├── config/
    │   │   │   ├── IConfig.java            ← interface (SOLID-I, SOLID-D)
    │   │   │   └── ConfigManager.java      ← singleton config loader (SOLID-S)
    │   │   ├── driver/
    │   │   │   ├── IBrowserDriver.java     ← interface (SOLID-O, SOLID-D, SOLID-L)
    │   │   │   ├── ChromeDriverSetup.java  ← Chrome impl (SOLID-S)
    │   │   │   ├── FirefoxDriverSetup.java ← Firefox impl (SOLID-S)
    │   │   │   └── DriverManager.java      ← ThreadLocal lifecycle mgr (SOLID-S, SOLID-D)
    │   │   ├── pages/
    │   │   │   ├── IPage.java              ← page contract (SOLID-I)
    │   │   │   ├── BasePage.java           ← shared wiring, PageFactory (SOLID-O)
    │   │   │   ├── LoginPage.java          ← login POM
    │   │   │   └── InventoryPage.java      ← inventory POM
    │   │   └── utils/
    │   │       └── WaitHelper.java         ← explicit wait wrapper (SOLID-S)
    │   └── resources/
    │       └── logback.xml
    └── test/
        ├── java/com/saucedemo/tests/
        │   ├── fixtures/
        │   │   ├── IFixture.java           ← fixture contract (SOLID-I)
        │   │   └── AuthFixture.java        ← browser + login fixture (SOLID-S)
        │   ├── base/
        │   │   └── BaseTest.java           ← @BeforeMethod/@AfterMethod wiring
        │   └── scenarios/
        │       ├── LoginTest.java          ← login test scenarios
        │       └── CartTest.java           ← add/remove cart scenarios
        └── resources/
            ├── config.properties
            └── testng.xml
```

---

## SOLID Principles Applied

| Principle | Where |
|---|---|
| **S** – Single Responsibility | Each class has exactly one job: `LoginPage` only touches the login page, `DriverManager` only manages driver lifecycle, `WaitHelper` only wraps waits |
| **O** – Open/Closed | `IBrowserDriver` lets you add Edge/Safari support without touching `DriverManager` |
| **L** – Liskov Substitution | `ChromeDriverSetup` and `FirefoxDriverSetup` are fully interchangeable via `IBrowserDriver` |
| **I** – Interface Segregation | `IConfig`, `IPage`, `IBrowserDriver`, `IFixture` each expose only the methods their consumers need |
| **D** – Dependency Inversion | `DriverManager` depends on `IBrowserDriver`; `BaseTest` depends on `IFixture`; high-level modules never depend on concrete implementations |

---

## Prerequisites

- **Java 11+** (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **Google Chrome** or **Firefox** installed

---

## Running Tests

```bash
# Run the full suite
mvn test

# Run with Firefox instead of Chrome
mvn test -Dbrowser=firefox

# Run headless (CI-friendly)
mvn test -Dheadless=true

# Run a single test class
mvn test -Dtest=CartTest

# Run a single test method
mvn test -Dtest=CartTest#testAddAndRemoveItemFullScenario

# Override base URL
mvn test -Dbase.url=https://www.saucedemo.com
```

---

## Configuration

All defaults live in `src/test/resources/config.properties`.
Any property can be overridden at runtime with a `-D` JVM argument.

| Property | Default | Description |
|---|---|---|
| `base.url` | `https://www.saucedemo.com` | Application URL |
| `browser` | `chrome` | `chrome` or `firefox` |
| `headless` | `false` | Run browser in headless mode |
| `implicit.wait` | `10` | Implicit wait (seconds) |
| `explicit.wait` | `15` | Explicit wait timeout (seconds) |

---

## Test Scenarios

### LoginTest
| Test | Description |
|---|---|
| `testSuccessfulLogin` | `standard_user` + `secret_sauce` → lands on inventory page |
| `testLoginPageIsLoaded` | Login form elements are visible |
| `testInvalidCredentialsShowError` | Wrong credentials → error banner |

### CartTest
| Test | Description |
|---|---|
| `testAddItemToCart` | Adding *Sauce Labs Backpack* sets cart badge to 1 |
| `testRemoveItemFromCart` | Removing the item empties the cart |
| `testAddAndRemoveItemFullScenario` | Complete add → remove cycle (mirrors the Playwright run) |
