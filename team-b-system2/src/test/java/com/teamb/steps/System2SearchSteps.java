package com.teamb.steps;

import com.sharedframework.cucumber.ScenarioContext;
import com.teamb.pages.System2SearchPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/**
 * System2-specific search steps ONLY.
 *
 * Shared search steps (When user searches for, Then results should be displayed, etc.)
 * are provided by SharedSearchSteps from team-a's test-jar.
 * System2PageObjectProvider wires System2's search page into those shared steps.
 *
 * This class adds only what is unique to System2 Financial Dashboard.
 */
public class System2SearchSteps {

    private final ScenarioContext scenarioContext;
    private final System2SearchPage searchPage = new System2SearchPage();

    public System2SearchSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("the user is on the search page")
    public void theUserIsOnTheSearchPage() {
        searchPage.navigateTo();
        System.out.println("[System2] Navigated to Financial Instrument Search page");
    }

    @Then("the result count should be greater than zero")
    public void theResultCountShouldBeGreaterThanZero() {
        int count = searchPage.getResultCount();
        if (count <= 0) {
            throw new AssertionError("[System2] Expected search results but found: " + count);
        }
        scenarioContext.set("searchResultCount", count);
        System.out.println("[System2] Result count verified: " + count);
    }
}
