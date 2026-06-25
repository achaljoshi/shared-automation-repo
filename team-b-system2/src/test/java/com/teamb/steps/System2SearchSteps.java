package com.teamb.steps;

import com.sharedframework.cucumber.ScenarioContext;
import com.sharedframework.cucumber.SearchPageContract;
import com.teamb.System2Context;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class System2SearchSteps {

    private final ScenarioContext scenarioContext;
    private final System2Context system2Context;

    public System2SearchSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        this.system2Context = new System2Context();
    }

    @Given("the user is on the search page")
    public void theUserIsOnTheSearchPage() {
        SearchPageContract searchPage = system2Context.getSearchPage();
        searchPage.navigateTo();
        System.out.println("Navigated to search page for: " + system2Context.getSystemName());
    }

    @When("the user searches for {string}")
    public void theUserSearchesFor(String searchTerm) {
        SearchPageContract searchPage = system2Context.getSearchPage();
        searchPage.enterSearchTerm(searchTerm);
        searchPage.clickSearch();
        scenarioContext.set("lastSearchTerm", searchTerm);
        System.out.println("Searched for: " + searchTerm);
    }

    @Then("search results should be displayed")
    public void searchResultsShouldBeDisplayed() {
        SearchPageContract searchPage = system2Context.getSearchPage();
        int count = searchPage.getResultCount();
        System.out.println("Search returned " + count + " results");
    }

    @Then("the result count should be greater than zero")
    public void theResultCountShouldBeGreaterThanZero() {
        SearchPageContract searchPage = system2Context.getSearchPage();
        int count = searchPage.getResultCount();
        if (count <= 0) {
            throw new AssertionError("Expected search results but found: " + count);
        }
        scenarioContext.set("searchResultCount", count);
    }

    @Then("no results message should be displayed")
    public void noResultsMessageShouldBeDisplayed() {
        SearchPageContract searchPage = system2Context.getSearchPage();
        if (!searchPage.isNoResultsDisplayed()) {
            throw new AssertionError("Expected 'no results' message but it was not displayed");
        }
    }

    @When("the user clears the search")
    public void theUserClearsTheSearch() {
        SearchPageContract searchPage = system2Context.getSearchPage();
        searchPage.clearSearch();
        System.out.println("Search cleared");
    }
}
