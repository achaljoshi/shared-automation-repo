package com.teamb.runner;

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
                "com.teamb.steps",
                "com.sharedframework.cucumber"
        },
        tags = "@sys2 or @shared",
        plugin = {
                "pretty",
                "json:target/cucumber-reports/system2-cucumber.json",
                "html:target/cucumber-reports/system2-html",
                "junit:target/cucumber-reports/system2-junit.xml"
        },
        monochrome = true,
        publish = false
)
public class System2TestRunner {
    // Test runner - no additional code needed
}
