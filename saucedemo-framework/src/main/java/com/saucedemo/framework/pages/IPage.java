package com.saucedemo.framework.pages;

/**
 * Interface Segregation (SOLID - I): Exposes only the minimal contract every page must fulfill.
 * Dependency Inversion (SOLID - D): Callers depend on this interface, not concrete page classes.
 */
public interface IPage {
    boolean isPageLoaded();
    String getPageTitle();
}
