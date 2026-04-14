package com.saucedemo.framework.config;

/**
 * Interface Segregation (SOLID - I):
 * Exposes only the configuration properties needed by consumers.
 * Dependency Inversion (SOLID - D):
 * High-level modules depend on this abstraction, not on ConfigManager directly.
 */
public interface IConfig {
    String getBaseUrl();
    String getBrowser();
    boolean isHeadless();
    int getImplicitWait();
    int getExplicitWait();
}
