package com.teama.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "src/test/resources/features",          // System1-specific features
                "classpath:features/shared"             // Shared features from main JAR resources
        },
        glue = {
                "com.teama.steps.base",                 // SharedLoginSteps, SharedSearchSteps, PageObjectProvider
                "com.teama.steps",                      // System1PageObjectProvider, System1LoginSteps, System1SearchSteps
                "com.sharedframework.cucumber"          // Hooks (Before/After screenshot, ScenarioContext)
        },
        tags = "@sys1 or @shared",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/system1-report.html",
                "json:target/cucumber-reports/system1.json",
                "junit:target/cucumber-reports/system1-junit.xml"
        },
        monochrome = true,
        publish = false
)
public class System1TestRunner {
    // PicoContainer wires: System1PageObjectProvider → SharedLoginSteps + SharedSearchSteps
}
