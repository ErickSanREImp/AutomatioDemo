package com.saucedemo.framework.pages;

import com.saucedemo.framework.driver.DriverManager;
import com.saucedemo.framework.utils.WaitHelper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Open/Closed (SOLID - O): Extended by page classes; never modified for new pages.
 * Single Responsibility (SOLID - S): Provides shared driver/wait wiring for all pages.
 * Implements IPage contract (Liskov Substitution - SOLID - L).
 */
public abstract class BasePage implements IPage {

    protected final WebDriver driver;
    protected final WaitHelper waitHelper;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.waitHelper = new WaitHelper(driver);
        PageFactory.initElements(driver, this);
    }

    @Override
    public String getPageTitle() {
        return driver.getTitle();
    }

    public WebDriver getDriver() {
        return driver;
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
    }

    protected void fillInput(WebElement field, String value) {
        WebElement el = waitHelper.waitForVisibility(field);
        ((JavascriptExecutor) driver).executeScript(
            "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
            "setter.call(arguments[0], arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            el, value
        );
    }
}
