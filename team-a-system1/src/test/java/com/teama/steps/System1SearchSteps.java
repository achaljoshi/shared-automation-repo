package com.teama.steps;

import com.sharedframework.cucumber.ScenarioContext;
import com.sharedframework.cucumber.SearchPageContract;
import com.teama.pages.System1SearchPage;
import com.teama.steps.base.BaseSearchSteps;
import io.cucumber.java.en.Given;

/**
 * System1-specific search steps.
 * Extends BaseSearchSteps — all shared search @When/@Then logic is inherited.
 * This class only provides the System1 search page object and System1-specific steps.
 */
public class System1SearchSteps extends BaseSearchSteps {

    private final System1SearchPage searchPage;

    public System1SearchSteps(ScenarioContext scenarioContext) {
        super(scenarioContext);
        this.searchPage = new System1SearchPage();
    }

    @Override
    protected SearchPageContract getSearchPage() {
        return searchPage;
    }

    @Override
    protected String getSystemName() {
        return "System1 - Government Portal";
    }

    // System1-ONLY steps below this line

    @Given("the user is on the search page")
    public void theUserIsOnTheSearchPage() {
        getSearchPage().navigateTo();
        System.out.println("[System1] Navigated to search page (Government Document Search)");
    }

    @io.cucumber.java.en.Then("the result count should be greater than zero")
    public void theResultCountShouldBeGreaterThanZero() {
        int count = getSearchPage().getResultCount();
        if (count <= 0) {
            throw new AssertionError("[System1] Expected search results but found: " + count);
        }
        scenarioContext.set("searchResultCount", count);
        System.out.println("[System1] Result count verified: " + count);
    }
}
