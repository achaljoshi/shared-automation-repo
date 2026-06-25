package com.teamb;

import com.sharedframework.cucumber.LoginPageContract;
import com.sharedframework.cucumber.SearchPageContract;
import com.sharedframework.cucumber.SystemContext;
import com.sharedframework.data.PropertyLoader;
import com.sharedframework.selenium.DriverFactory;
import com.teamb.pages.LoginPage;
import com.teamb.pages.SearchPage;
import org.openqa.selenium.WebDriver;

public class System2Context implements SystemContext {

    private LoginPage loginPage;
    private SearchPage searchPage;
    private final String baseUrl;

    public System2Context() {
        this.baseUrl = PropertyLoader.get("system2.properties", "base.url",
                "https://system2.fintech.example.com");
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
        return "System2 - Financial Dashboard";
    }

    @Override
    public void navigateToHome() {
        WebDriver driver = DriverFactory.getDriver();
        driver.get(baseUrl + "/dashboard");
    }

    public void resetPageObjects() {
        this.loginPage = null;
        this.searchPage = null;
    }
}
