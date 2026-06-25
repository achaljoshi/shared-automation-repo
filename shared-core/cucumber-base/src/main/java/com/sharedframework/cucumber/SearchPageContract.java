package com.sharedframework.cucumber;

public interface SearchPageContract {
    void navigateTo();
    void enterSearchTerm(String term);
    void clickSearch();
    int getResultCount();
    String getFirstResultTitle();
    boolean isNoResultsDisplayed();
    void clearSearch();
}
