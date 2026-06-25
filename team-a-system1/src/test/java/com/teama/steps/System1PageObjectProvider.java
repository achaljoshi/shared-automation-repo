package com.teama.steps;

import com.sharedframework.cucumber.LoginPageContract;
import com.sharedframework.cucumber.SearchPageContract;
import com.teama.pages.System1LoginPage;
import com.teama.pages.System1SearchPage;
import com.teama.steps.base.PageObjectProvider;

/**
 * System1's implementation of PageObjectProvider.
 * PicoContainer finds this in the glue path and injects it into SharedLoginSteps
 * and SharedSearchSteps when team-a runs its own tests.
 */
public class System1PageObjectProvider implements PageObjectProvider {

    private final System1LoginPage loginPage = new System1LoginPage();
    private final System1SearchPage searchPage = new System1SearchPage();

    @Override
    public LoginPageContract getLoginPage() {
        return loginPage;
    }

    @Override
    public SearchPageContract getSearchPage() {
        return searchPage;
    }

    @Override
    public String getSystemName() {
        return "System1 - Government Portal";
    }
}
