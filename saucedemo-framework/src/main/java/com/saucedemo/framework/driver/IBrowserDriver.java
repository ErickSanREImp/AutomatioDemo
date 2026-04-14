package com.saucedemo.framework.driver;

import org.openqa.selenium.WebDriver;

/**
 * Open/Closed (SOLID - O): Open for extension (new browsers) without modifying existing code.
 * Dependency Inversion (SOLID - D): DriverManager depends on this abstraction.
 * Liskov Substitution (SOLID - L): Any implementation is substitutable for another.
 */
public interface IBrowserDriver {
    WebDriver createDriver(boolean headless);
}
