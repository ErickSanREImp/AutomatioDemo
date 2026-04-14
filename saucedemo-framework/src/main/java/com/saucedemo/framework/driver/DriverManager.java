package com.saucedemo.framework.driver;

import com.saucedemo.framework.config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Single Responsibility (SOLID - S): Solely manages WebDriver lifecycle.
 * Dependency Inversion (SOLID - D): Depends on IBrowserDriver abstraction, not concrete drivers.
 * Open/Closed (SOLID - O): New browsers added via BROWSER_MAP without touching this class.
 * Uses ThreadLocal for safe parallel test execution.
 */
public class DriverManager {

    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private static final Map<String, IBrowserDriver> BROWSER_MAP = new HashMap<>();

    static {
        BROWSER_MAP.put("chrome", new ChromeDriverSetup());
        BROWSER_MAP.put("firefox", new FirefoxDriverSetup());
    }

    private DriverManager() {}

    public static void initDriver() {
        ConfigManager config = ConfigManager.getInstance();
        String browser = config.getBrowser().toLowerCase();
        boolean headless = config.isHeadless();

        IBrowserDriver browserDriver = BROWSER_MAP.get(browser);
        if (browserDriver == null) {
            throw new IllegalArgumentException("Unsupported browser: " + browser
                    + ". Supported: " + BROWSER_MAP.keySet());
        }

        WebDriver driver = browserDriver.createDriver(headless);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().window().maximize();
        driverThreadLocal.set(driver);
        log.info("Browser '{}' initialized (headless={})", browser, headless);
    }

    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("Driver not initialized. Call DriverManager.initDriver() first.");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            log.info("Browser session closed.");
        }
    }
}
