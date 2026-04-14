package com.saucedemo.tests.base;

import com.saucedemo.tests.fixtures.AuthFixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Single Responsibility (SOLID - S): Manages browser lifecycle only.
 * Open/Closed (SOLID - O): Test classes extend this; never modify for new test types.
 * Reporting is fully delegated to TestListener (registered in testng.xml).
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected AuthFixture authFixture;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        authFixture = new AuthFixture();
        authFixture.setUp();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (authFixture != null) {
            authFixture.tearDown();
        }
    }
}
