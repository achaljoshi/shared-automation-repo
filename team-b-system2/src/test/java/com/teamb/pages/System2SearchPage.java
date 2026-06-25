package com.teamb.pages;

import com.sharedframework.cucumber.SearchPageContract;

/**
 * System2 Financial Dashboard search page.
 * Implements SearchPageContract with simulated logic (print statements + in-memory state)
 * so the demo compiles and runs without a real browser.
 * System2-specific locators: .fin-search-input, .fin-search-submit, .fin-result-item
 */
public class System2SearchPage implements SearchPageContract {

    private static final String BASE_URL = "https://system2.fintech.example.com";

    private String lastSearchTerm = null;
    private boolean searchExecuted = false;

    @Override
    public void navigateTo() {
        System.out.println("[System2SearchPage] Navigating to: " + BASE_URL + "/search");
    }

    @Override
    public void enterSearchTerm(String term) {
        System.out.println("[System2SearchPage] Entering search term into .fin-search-input: " + term);
        this.lastSearchTerm = term;
        this.searchExecuted = false;
    }

    @Override
    public void clickSearch() {
        System.out.println("[System2SearchPage] Clicking .fin-search-submit");
        this.searchExecuted = true;
    }

    @Override
    public int getResultCount() {
        if (!searchExecuted || lastSearchTerm == null) {
            return 0;
        }
        // Simulate: non-existent terms return 0, everything else returns 3
        if (lastSearchTerm.startsWith("xyz") || lastSearchTerm.contains("nonexistent")) {
            return 0;
        }
        return 3;
    }

    @Override
    public String getFirstResultTitle() {
        if (getResultCount() == 0) {
            return null;
        }
        return "[FIN-REPORT] " + lastSearchTerm + " — Financial Analysis Report";
    }

    @Override
    public boolean isNoResultsDisplayed() {
        return searchExecuted && getResultCount() == 0;
    }

    @Override
    public void clearSearch() {
        System.out.println("[System2SearchPage] Clearing .fin-search-input");
        this.lastSearchTerm = null;
        this.searchExecuted = false;
    }
}
