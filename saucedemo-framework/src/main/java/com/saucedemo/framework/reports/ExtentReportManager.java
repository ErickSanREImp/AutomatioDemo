package com.saucedemo.framework.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Single Responsibility (SOLID - S): Manages the ExtentReports HTML report lifecycle only.
 * Thread-safe singleton with a ThreadLocal for per-test ExtentTest nodes.
 */
public class ExtentReportManager {

    private static final String REPORT_DIR = "reports/html/";
    private static volatile ExtentReportManager instance;
    private ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    private ExtentReportManager() {}

    public static ExtentReportManager getInstance() {
        if (instance == null) {
            synchronized (ExtentReportManager.class) {
                if (instance == null) {
                    instance = new ExtentReportManager();
                    instance.init();
                }
            }
        }
        return instance;
    }

    private void init() {
        new File(REPORT_DIR).mkdirs();
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String reportPath = REPORT_DIR + "ExtentReport-" + timestamp + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("SauceDemo Test Execution Report");
        spark.config().setReportName("SauceDemo – Full Suite");
        spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        extentReports = new ExtentReports();
        extentReports.attachReporter(spark);
        extentReports.setSystemInfo("Application", "SauceDemo (saucedemo.com)");
        extentReports.setSystemInfo("Browser", "Chrome");
        extentReports.setSystemInfo("Environment", "QA");
    }

    public ExtentTest createTest(String testName, String description) {
        ExtentTest test = extentReports.createTest(testName, description);
        testThreadLocal.set(test);
        return test;
    }

    public ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    public static void reset() {
        instance = null;
    }
}
