package com.saucedemo.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Single Responsibility (SOLID - S): Solely responsible for loading and exposing configuration.
 * Implements IConfig (Dependency Inversion - SOLID - D).
 * Thread-safe Singleton via double-checked locking.
 */
public class ConfigManager implements IConfig {

    private static volatile ConfigManager instance;
    private final Properties properties;

    private ConfigManager() {
        properties = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new RuntimeException("config.properties not found on classpath");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    @Override
    public String getBaseUrl() {
        return System.getProperty("base.url", properties.getProperty("base.url", "https://www.saucedemo.com"));
    }

    @Override
    public String getBrowser() {
        return System.getProperty("browser", properties.getProperty("browser", "chrome"));
    }

    @Override
    public boolean isHeadless() {
        return Boolean.parseBoolean(
                System.getProperty("headless", properties.getProperty("headless", "false")));
    }

    @Override
    public int getImplicitWait() {
        return Integer.parseInt(properties.getProperty("implicit.wait", "10"));
    }

    @Override
    public int getExplicitWait() {
        return Integer.parseInt(properties.getProperty("explicit.wait", "15"));
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
