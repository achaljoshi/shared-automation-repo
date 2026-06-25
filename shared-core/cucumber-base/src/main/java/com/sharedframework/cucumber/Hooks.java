package com.sharedframework.cucumber;

import com.sharedframework.selenium.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    private final ScenarioContext scenarioContext;
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public Hooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(order = 0)
    public void beforeScenario(Scenario scenario) {
        System.out.println("==========================================================");
        System.out.println("Starting Scenario: " + scenario.getName());
        System.out.println("Tags: " + scenario.getSourceTagNames());
        System.out.println("==========================================================");

        if (isUiScenario(scenario)) {
            System.out.println("UI scenario detected - initializing WebDriver");
            DriverFactory.createDriver();
        }
    }

    @Before(order = 100)
    public void clearScenarioContext() {
        scenarioContext.clear();
    }

    @After(order = 0)
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverFactory.isDriverActive()) {
                takeScreenshot(scenario);
            }
        } finally {
            if (DriverFactory.isDriverActive()) {
                DriverFactory.quitDriver();
            }
        }

        String status = scenario.getStatus().toString();
        System.out.println("==========================================================");
        System.out.println("Scenario: " + scenario.getName());
        System.out.println("Status: " + status);
        System.out.println("==========================================================");
    }

    private void takeScreenshot(Scenario scenario) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

                // Attach to Cucumber report
                scenario.attach(screenshot, "image/png", "Screenshot on failure");

                // Save to file system
                String screenshotDir = "target/screenshots";
                Path dirPath = Paths.get(screenshotDir);
                Files.createDirectories(dirPath);

                String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
                String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_");
                String fileName = screenshotDir + "/" + safeName + "_" + timestamp + ".png";

                try (FileOutputStream fos = new FileOutputStream(new File(fileName))) {
                    fos.write(screenshot);
                }
                System.out.println("Screenshot saved: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }

    private boolean isUiScenario(Scenario scenario) {
        // If not explicitly tagged as @api, assume it might need UI
        // Teams can override by tagging @api for pure API scenarios
        return !scenario.getSourceTagNames().contains("@api");
    }
}
