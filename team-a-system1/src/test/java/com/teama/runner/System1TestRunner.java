package com.teama.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "src/test/resources/features",
                "../shared-features/src/test/resources/features"
        },
        glue = {
                "com.teama.steps",
                "com.sharedframework.cucumber"
        },
        tags = "@sys1 or @shared",
        plugin = {
                "pretty",
                "json:target/cucumber-reports/system1-cucumber.json",
                "html:target/cucumber-reports/system1-html",
                "junit:target/cucumber-reports/system1-junit.xml"
        },
        monochrome = true,
        publish = false
)
public class System1TestRunner {
    // Test runner - no additional code needed
}
