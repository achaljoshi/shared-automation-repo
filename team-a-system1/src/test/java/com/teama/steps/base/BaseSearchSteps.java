package com.teama.steps.base;

import com.sharedframework.cucumber.ScenarioContext;
import com.sharedframework.cucumber.SearchPageContract;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Abstract base class containing all shared search step definitions.
 * Team B imports this from the team-a test-jar and provides only their own
 * page object via {@link #getSearchPage()}.
 * No system-specific locators live here.
 */
public abstract class BaseSearchSteps {

    protected final ScenarioContext scenarioContext;

    public BaseSearchSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    /**
     * Subclass provides their own page object implementing SearchPageContract.
     */
    protected abstract SearchPageContract getSearchPage();

    /**
     * Optional — subclasses can override to label their system in log output.
     */
    protected String getSystemName() {
        return "Unknown System";
    }

    @When("the user searches for {string}")
    public void theUserSearchesFor(String searchTerm) {
        getSearchPage().enterSearchTerm(searchTerm);
        getSearchPage().clickSearch();
        scenarioContext.set("lastSearchTerm", searchTerm);
        System.out.println("[" + getSystemName() + "] Searched for: " + searchTerm);
    }

    @Then("search results should be displayed")
    public void searchResultsShouldBeDisplayed() {
        int count = getSearchPage().getResultCount();
        scenarioContext.set("searchResultCount", count);
        System.out.println("[PASS] Search returned " + count + " results on: " + getSystemName());
    }

    @Then("the results should contain {string}")
    public void theResultsShouldContain(String expectedText) {
        String firstResult = getSearchPage().getFirstResultTitle();
        if (firstResult == null || !firstResult.toLowerCase().contains(expectedText.toLowerCase())) {
            throw new AssertionError(
                    "Expected results to contain '" + expectedText + "' but first result was: " + firstResult);
        }
        System.out.println("[PASS] Results contain '" + expectedText + "' on: " + getSystemName());
    }

    @Then("no results message should be displayed")
    public void noResultsMessageShouldBeDisplayed() {
        if (!getSearchPage().isNoResultsDisplayed()) {
            throw new AssertionError("Expected 'no results' message but it was not displayed on: " + getSystemName());
        }
        System.out.println("[PASS] No results message displayed correctly on: " + getSystemName());
    }

    @When("the user clears the search")
    public void theUserClearsTheSearch() {
        getSearchPage().clearSearch();
        System.out.println("[" + getSystemName() + "] Search cleared");
    }
}
