package com.saucedemo.tests.fixtures;

/**
 * Interface Segregation (SOLID - I): Minimal contract that every fixture must implement.
 * Dependency Inversion (SOLID - D): BaseTest depends on this abstraction for lifecycle hooks.
 */
public interface IFixture {
    void setUp();
    void tearDown();
}
