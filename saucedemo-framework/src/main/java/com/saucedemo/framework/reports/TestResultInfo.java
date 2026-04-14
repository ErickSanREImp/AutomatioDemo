package com.saucedemo.framework.reports;

/**
 * Single Responsibility (SOLID - S): Plain data object holding the outcome of a single test method.
 */
public class TestResultInfo {

    private final String className;
    private final String testName;
    private final String status;
    private final long durationMs;
    private final String errorMessage;
    private final String screenshotPath;

    public TestResultInfo(String className,
                          String testName,
                          String status,
                          long durationMs,
                          String errorMessage,
                          String screenshotPath) {
        this.className     = className;
        this.testName      = testName;
        this.status        = status;
        this.durationMs    = durationMs;
        this.errorMessage  = errorMessage;
        this.screenshotPath = screenshotPath;
    }

    public String getClassName()      { return className; }
    public String getTestName()       { return testName; }
    public String getStatus()         { return status; }
    public long   getDurationMs()     { return durationMs; }
    public String getErrorMessage()   { return errorMessage; }
    public String getScreenshotPath() { return screenshotPath; }
}
