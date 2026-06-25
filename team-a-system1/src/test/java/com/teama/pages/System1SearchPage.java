package com.teama.pages;

import com.sharedframework.cucumber.SearchPageContract;

/**
 * System1 Government Portal search page.
 * Implements SearchPageContract with simulated logic (print statements + in-memory state)
 * so the demo compiles and runs without a real browser.
 */
public class System1SearchPage implements SearchPageContract {

    private static final String BASE_URL = "https://system1.gov.example.com";

    private String lastSearchTerm = null;
    private boolean searchExecuted = false;

    @Override
    public void navigateTo() {
        System.out.println("[System1SearchPage] Navigating to: " + BASE_URL + "/search");
    }

    @Override
    public void enterSearchTerm(String term) {
        System.out.println("[System1SearchPage] Entering search term: " + term);
        this.lastSearchTerm = term;
        this.searchExecuted = false;
    }

    @Override
    public void clickSearch() {
        System.out.println("[System1SearchPage] Clicking search button");
        this.searchExecuted = true;
    }

    @Override
    public int getResultCount() {
        if (!searchExecuted || lastSearchTerm == null) {
            return 0;
        }
        // Simulate: non-existent terms return 0, everything else returns 5
        if (lastSearchTerm.startsWith("xyz") || lastSearchTerm.contains("nonexistent")) {
            return 0;
        }
        return 5;
    }

    @Override
    public String getFirstResultTitle() {
        if (getResultCount() == 0) {
            return null;
        }
        return "[GOV-DOC] " + lastSearchTerm + " — Official Government Document";
    }

    @Override
    public boolean isNoResultsDisplayed() {
        return searchExecuted && getResultCount() == 0;
    }

    @Override
    public void clearSearch() {
        System.out.println("[System1SearchPage] Clearing search field");
        this.lastSearchTerm = null;
        this.searchExecuted = false;
    }
}
