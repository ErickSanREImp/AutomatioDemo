package com.saucedemo.framework.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Single Responsibility (SOLID - S): Only handles Firefox WebDriver creation.
 * Liskov Substitution (SOLID - L): Substitutable for any other IBrowserDriver impl.
 */
public class FirefoxDriverSetup implements IBrowserDriver {

    @Override
    public WebDriver createDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        return new FirefoxDriver(options);
    }
}
