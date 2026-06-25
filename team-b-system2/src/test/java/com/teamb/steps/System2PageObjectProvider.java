package com.teamb.steps;

import com.sharedframework.cucumber.LoginPageContract;
import com.sharedframework.cucumber.SearchPageContract;
import com.teama.steps.base.PageObjectProvider;
import com.teamb.pages.System2LoginPage;
import com.teamb.pages.System2SearchPage;

/**
 * System2's implementation of PageObjectProvider.
 *
 * This is the ONLY thing team-b had to write to get all shared login and search
 * step definitions working on their system.
 *
 * PicoContainer finds this class in the glue path (com.teamb.steps) and injects
 * it into SharedLoginSteps and SharedSearchSteps (which live in team-a's test-jar).
 * No inheritance, no copy-paste, no Cucumber rule violations.
 */
public class System2PageObjectProvider implements PageObjectProvider {

    private final System2LoginPage loginPage = new System2LoginPage();
    private final System2SearchPage searchPage = new System2SearchPage();

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
        return "System2 - Financial Dashboard";
    }
}
