package com.teama.pages;

import com.sharedframework.cucumber.SearchPageContract;
import com.sharedframework.selenium.BasePageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SearchPage extends BasePageObject implements SearchPageContract {

    // Locators for Government Portal search page
    private static final By SEARCH_INPUT = By.cssSelector("input[type='search'], #searchInput, input[name='q']");
    private static final By SEARCH_BUTTON = By.cssSelector("button.search-btn, #searchButton, input[type='submit'][value='Search']");
    private static final By RESULTS_CONTAINER = By.cssSelector(".search-results, #searchResults, .results-list");
    private static final By RESULT_ITEMS = By.cssSelector(".search-result-item, .result-entry, li.search-item");
    private static final By NO_RESULTS_MESSAGE = By.cssSelector(".no-results, #noResults, .empty-results");
    private static final By RESULT_COUNT_LABEL = By.cssSelector(".result-count, #resultCount, .total-results");
    private static final By FIRST_RESULT_TITLE = By.cssSelector(".search-result-item:first-child .result-title, .result-entry:first-child h3");
    private static final By CLEAR_SEARCH_BUTTON = By.cssSelector("button.clear-search, #clearSearch, .search-clear");

    private final String baseUrl;

    public SearchPage(WebDriver driver) {
        super(driver);
        this.baseUrl = System.getProperty("base.url", "https://system1.gov.example.com");
    }

    public SearchPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    @Override
    public void navigateTo() {
        navigateTo(baseUrl + "/search");
    }

    @Override
    public void enterSearchTerm(String term) {
        sendKeys(SEARCH_INPUT, term);
    }

    @Override
    public void clickSearch() {
        click(SEARCH_BUTTON);
        waitForPageLoad();
    }

    @Override
    public int getResultCount() {
        List<WebElement> items = findElements(RESULT_ITEMS);
        if (!items.isEmpty()) {
            return items.size();
        }

        // Try to read from result count label
        if (isDisplayed(RESULT_COUNT_LABEL, 3)) {
            String countText = getText(RESULT_COUNT_LABEL);
            try {
                return Integer.parseInt(countText.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public String getFirstResultTitle() {
        if (isDisplayed(FIRST_RESULT_TITLE, 5)) {
            return getText(FIRST_RESULT_TITLE);
        }
        List<WebElement> items = findElements(RESULT_ITEMS);
        if (!items.isEmpty()) {
            return items.get(0).getText();
        }
        return "";
    }

    @Override
    public boolean isNoResultsDisplayed() {
        return isDisplayed(NO_RESULTS_MESSAGE, 5);
    }

    @Override
    public void clearSearch() {
        if (isDisplayed(CLEAR_SEARCH_BUTTON, 3)) {
            click(CLEAR_SEARCH_BUTTON);
        } else {
            sendKeys(SEARCH_INPUT, "");
        }
        waitForPageLoad();
    }

    public boolean hasResults() {
        return isDisplayed(RESULTS_CONTAINER, 5) && getResultCount() > 0;
    }
}
