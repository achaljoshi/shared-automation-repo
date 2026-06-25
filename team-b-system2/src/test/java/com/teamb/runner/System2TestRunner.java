package com.teamb.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "src/test/resources/features",      // System2-specific feature files
                "classpath:features/shared"         // Shared features loaded from team-a-system1.jar on classpath
        },
        glue = {
                "com.teama.steps.base",             // SharedLoginSteps + SharedSearchSteps (from team-a test-jar)
                "com.teamb.steps",                  // System2PageObjectProvider + System2-specific steps
                "com.sharedframework.cucumber"      // Hooks (Before/After), ScenarioContext
        },
        tags = "@sys2 or @shared",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/system2-report.html",
                "json:target/cucumber-reports/system2.json",
                "junit:target/cucumber-reports/system2-junit.xml"
        },
        monochrome = true,
        publish = false
)
public class System2TestRunner {
    /*
     * How reuse works here (no inheritance):
     *
     *  1. "com.teama.steps.base" glue loads SharedLoginSteps & SharedSearchSteps
     *     from team-a's test-jar. These have all the @Given/@When/@Then.
     *
     *  2. Both classes need a PageObjectProvider in their constructor.
     *
     *  3. PicoContainer scans all glue packages, finds System2PageObjectProvider
     *     (in com.teamb.steps) implementing PageObjectProvider, and injects it.
     *
     *  4. Now SharedLoginSteps calls System2LoginPage.clickLogin(), etc.
     *     Same step logic — System2 page objects — zero code duplication.
     */
}
