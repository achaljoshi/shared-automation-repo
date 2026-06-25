package com.teama;

import com.sharedframework.cucumber.LoginPageContract;
import com.sharedframework.cucumber.SearchPageContract;
import com.sharedframework.cucumber.SystemContext;
import com.sharedframework.data.PropertyLoader;
import com.sharedframework.selenium.DriverFactory;
import com.teama.pages.LoginPage;
import com.teama.pages.SearchPage;
import org.openqa.selenium.WebDriver;

public class System1Context implements SystemContext {

    private LoginPage loginPage;
    private SearchPage searchPage;
    private final String baseUrl;

    public System1Context() {
        this.baseUrl = PropertyLoader.get("system1.properties", "base.url",
                "https://system1.gov.example.com");
    }

    @Override
    public LoginPageContract getLoginPage() {
        if (loginPage == null) {
            WebDriver driver = DriverFactory.getDriver();
            loginPage = new LoginPage(driver, baseUrl);
        }
        return loginPage;
    }

    @Override
    public SearchPageContract getSearchPage() {
        if (searchPage == null) {
            WebDriver driver = DriverFactory.getDriver();
            searchPage = new SearchPage(driver, baseUrl);
        }
        return searchPage;
    }

    @Override
    public String getSystemName() {
        return "System1 - Government Portal";
    }

    @Override
    public void navigateToHome() {
        WebDriver driver = DriverFactory.getDriver();
        driver.get(baseUrl);
    }

    public void resetPageObjects() {
        this.loginPage = null;
        this.searchPage = null;
    }
}
