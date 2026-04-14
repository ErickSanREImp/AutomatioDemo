package com.saucedemo.tests.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.saucedemo.framework.driver.DriverManager;
import com.saucedemo.framework.reports.ExtentReportManager;
import com.saucedemo.framework.reports.PDFReportManager;
import com.saucedemo.framework.reports.TestResultInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Single Responsibility (SOLID - S): Bridges TestNG lifecycle events to ExtentReports and PDF generation.
 * Register this class in testng.xml under <listeners>.
 */
public class TestListener implements ITestListener, ISuiteListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);
    private static final String SCREENSHOT_DIR = "reports/screenshots/";

    private final List<TestResultInfo> results = Collections.synchronizedList(new ArrayList<>());

    // ── ISuiteListener ────────────────────────────────────────────────────────

    @Override
    public void onStart(ISuite suite) {
        log.info("=== Suite '{}' starting ===", suite.getName());
        ExtentReportManager.getInstance();
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("=== Suite '{}' finished – flushing reports ===", suite.getName());
        ExtentReportManager.getInstance().flush();
        new PDFReportManager().generateReport(results);
    }

    // ── ITestListener ─────────────────────────────────────────────────────────

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String desc     = result.getMethod().getDescription();
        String description = (desc != null && !desc.isEmpty()) ? desc : testName;
        ExtentReportManager.getInstance().createTest(
                result.getTestClass().getName().replaceAll(".*\\.", "") + " → " + testName,
                description);
        log.info("TEST STARTED: {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        ExtentTest test = ExtentReportManager.getInstance().getTest();
        if (test != null) {
            test.log(Status.PASS, "Test passed in " + duration + " ms");
        }
        results.add(new TestResultInfo(
                simpleClass(result), result.getMethod().getMethodName(),
                "PASSED", duration, null, null));
        log.info("TEST PASSED: {} ({}ms)", result.getMethod().getMethodName(), duration);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration  = result.getEndMillis() - result.getStartMillis();
        String errMsg  = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown";
        String ssPath  = takeScreenshot(result.getMethod().getMethodName());

        ExtentTest test = ExtentReportManager.getInstance().getTest();
        if (test != null) {
            test.log(Status.FAIL, "Test failed in " + duration + " ms: " + errMsg);
            if (ssPath != null) {
                try {
                    test.fail("Screenshot",
                            MediaEntityBuilder.createScreenCaptureFromPath(
                                    "../screenshots/" + new File(ssPath).getName()).build());
                } catch (Exception ignored) {}
            }
        }
        results.add(new TestResultInfo(
                simpleClass(result), result.getMethod().getMethodName(),
                "FAILED", duration, errMsg, ssPath));
        log.error("TEST FAILED: {} – {}", result.getMethod().getMethodName(), errMsg);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        ExtentTest test = ExtentReportManager.getInstance().getTest();
        if (test != null) {
            test.log(Status.SKIP, "Test skipped");
        }
        results.add(new TestResultInfo(
                simpleClass(result), result.getMethod().getMethodName(),
                "SKIPPED", duration, null, null));
        log.warn("TEST SKIPPED: {}", result.getMethod().getMethodName());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String takeScreenshot(String testName) {
        try {
            WebDriver driver = DriverManager.getDriver();
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            new File(SCREENSHOT_DIR).mkdirs();
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String path = SCREENSHOT_DIR + testName + "_" + timestamp + ".png";
            try (FileOutputStream fos = new FileOutputStream(path)) {
                fos.write(bytes);
            }
            return path;
        } catch (Exception e) {
            log.warn("Could not capture screenshot for '{}': {}", testName, e.getMessage());
            return null;
        }
    }

    private String simpleClass(ITestResult result) {
        String full = result.getTestClass().getName();
        return full.contains(".") ? full.substring(full.lastIndexOf('.') + 1) : full;
    }
}
