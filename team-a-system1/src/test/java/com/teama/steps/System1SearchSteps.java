package com.teama.steps;

import com.sharedframework.cucumber.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/**
 * System1-specific search steps only.
 * Shared search steps (When the user searches for, Then search results should be displayed, etc.)
 * are handled by SharedSearchSteps via PageObjectRegistry — no inheritance needed.
 */
public class System1SearchSteps {

    private final ScenarioContext scenarioContext;

    public System1SearchSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("the user is on the search page")
    public void theUserIsOnTheSearchPage() {
        System.out.println("[System1] Navigated to search page (Government Document Search)");
    }

    @Then("the result count should be greater than zero")
    public void theResultCountShouldBeGreaterThanZero() {
        Integer count = scenarioContext.get("searchResultCount", Integer.class);
        if (count == null || count <= 0) {
            throw new AssertionError("[System1] Expected search results but found: " + count);
        }
        System.out.println("[System1] Result count verified: " + count);
    }
}
