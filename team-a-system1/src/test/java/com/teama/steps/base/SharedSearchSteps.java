package com.teama.steps.base;

import com.sharedframework.cucumber.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Shared search step definitions — packaged in team-a's test-jar.
 * Works the same way as SharedLoginSteps: reads provider from PageObjectRegistry.
 */
public class SharedSearchSteps {

    private final ScenarioContext scenarioContext;

    public SharedSearchSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    private PageObjectProvider pages() {
        return PageObjectRegistry.get();
    }

    @When("the user searches for {string}")
    public void theUserSearchesFor(String searchTerm) {
        pages().getSearchPage().enterSearchTerm(searchTerm);
        pages().getSearchPage().clickSearch();
        scenarioContext.set("lastSearchTerm", searchTerm);
        System.out.println("[" + pages().getSystemName() + "] Searched for: " + searchTerm);
    }

    @Then("search results should be displayed")
    public void searchResultsShouldBeDisplayed() {
        int count = pages().getSearchPage().getResultCount();
        if (count <= 0) {
            throw new AssertionError("Expected search results on " + pages().getSystemName() + " but got none");
        }
        scenarioContext.set("searchResultCount", count);
        System.out.println("[PASS] Search returned " + count + " results on: " + pages().getSystemName());
    }

    @Then("the results should contain {string}")
    public void theResultsShouldContain(String expectedText) {
        String firstResult = pages().getSearchPage().getFirstResultTitle();
        if (firstResult == null || !firstResult.toLowerCase().contains(expectedText.toLowerCase())) {
            throw new AssertionError("Expected results to contain '" + expectedText
                    + "' on " + pages().getSystemName() + " but first result was: " + firstResult);
        }
        System.out.println("[PASS] Results contain '" + expectedText + "' on: " + pages().getSystemName());
    }

    @Then("no results message should be displayed")
    public void noResultsMessageShouldBeDisplayed() {
        if (!pages().getSearchPage().isNoResultsDisplayed()) {
            throw new AssertionError("Expected no-results message on " + pages().getSystemName() + " but it was not shown");
        }
        System.out.println("[PASS] No results message shown on: " + pages().getSystemName());
    }

    @When("the user clears the search")
    public void theUserClearsTheSearch() {
        pages().getSearchPage().clearSearch();
        System.out.println("[" + pages().getSystemName() + "] Search cleared");
    }
}
