package com.teamb.pages;

import com.sharedframework.cucumber.SearchPageContract;
import com.sharedframework.selenium.BasePageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SearchPage extends BasePageObject implements SearchPageContract {

    // Financial dashboard search - searching for transactions, reports, accounts
    private static final By GLOBAL_SEARCH_BAR = By.cssSelector("input.global-search, #globalSearch, input[placeholder*='Search']");
    private static final By SEARCH_SUBMIT = By.cssSelector("button.search-submit, #searchSubmit, .search-icon-btn");
    private static final By SEARCH_RESULTS_PANEL = By.cssSelector(".search-results-panel, #searchResultsContainer, .results-drawer");
    private static final By RESULT_ROWS = By.cssSelector(".result-row, .search-result-card, li.result-item");
    private static final By EMPTY_STATE = By.cssSelector(".empty-state, .no-results-found, #emptySearchResults");
    private static final By RESULT_COUNT_BADGE = By.cssSelector(".result-badge, #resultCountBadge, .match-count");
    private static final By FIRST_RESULT_NAME = By.cssSelector(".result-row:first-child .result-name, .search-result-card:first-child .card-title");
    private static final By CLEAR_BUTTON = By.cssSelector("button.clear-btn, .search-clear-icon, #clearSearchInput");

    private final String baseUrl;

    public SearchPage(WebDriver driver) {
        super(driver);
        this.baseUrl = System.getProperty("base.url", "https://system2.fintech.example.com");
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
        sendKeys(GLOBAL_SEARCH_BAR, term);
    }

    @Override
    public void clickSearch() {
        click(SEARCH_SUBMIT);
        waitForPageLoad();
    }

    @Override
    public int getResultCount() {
        List<WebElement> items = findElements(RESULT_ROWS);
        if (!items.isEmpty()) return items.size();

        if (isDisplayed(RESULT_COUNT_BADGE, 3)) {
            String badgeText = getText(RESULT_COUNT_BADGE);
            try {
                return Integer.parseInt(badgeText.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public String getFirstResultTitle() {
        if (isDisplayed(FIRST_RESULT_NAME, 5)) {
            return getText(FIRST_RESULT_NAME);
        }
        List<WebElement> rows = findElements(RESULT_ROWS);
        return rows.isEmpty() ? "" : rows.get(0).getText();
    }

    @Override
    public boolean isNoResultsDisplayed() {
        return isDisplayed(EMPTY_STATE, 5);
    }

    @Override
    public void clearSearch() {
        if (isDisplayed(CLEAR_BUTTON, 3)) {
            click(CLEAR_BUTTON);
        } else {
            sendKeys(GLOBAL_SEARCH_BAR, "");
        }
        waitForPageLoad();
    }
}
