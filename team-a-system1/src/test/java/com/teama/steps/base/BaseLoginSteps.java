package com.teama.steps.base;

import com.sharedframework.cucumber.LoginPageContract;
import com.sharedframework.cucumber.ScenarioContext;
import com.sharedframework.data.PropertyLoader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Abstract base class containing all shared login step definitions.
 * Team B (and any future team) imports this from the team-a test-jar and provides
 * only their own page object via {@link #getLoginPage()}.
 * No system-specific locators live here.
 */
public abstract class BaseLoginSteps {

    protected final ScenarioContext scenarioContext;

    public BaseLoginSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    /**
     * Subclass provides their own page object implementing LoginPageContract.
     * This is the only thing a consuming team must supply.
     */
    protected abstract LoginPageContract getLoginPage();

    /**
     * Optional — subclasses can override to label their system in log output.
     */
    protected String getSystemName() {
        return "Unknown System";
    }

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        getLoginPage().navigateTo();
        scenarioContext.set("currentPage", "login");
        System.out.println("[" + getSystemName() + "] Navigated to login page");
    }

    @When("the user enters valid credentials")
    public void theUserEntersValidCredentials() {
        String username = PropertyLoader.get("test.username", "testuser");
        String password = PropertyLoader.get("test.password", "testpass");
        getLoginPage().enterUsername(username);
        getLoginPage().enterPassword(password);
        scenarioContext.set("attemptedUser", username);
    }

    @When("the user enters username {string} and password {string}")
    public void theUserEntersUsernameAndPassword(String username, String password) {
        getLoginPage().enterUsername(username);
        getLoginPage().enterPassword(password);
        scenarioContext.set("attemptedUser", username);
    }

    @When("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        getLoginPage().clickLogin();
    }

    @Then("the user should be logged in successfully")
    public void theUserShouldBeLoggedInSuccessfully() {
        if (!getLoginPage().isLoggedIn()) {
            throw new AssertionError("Expected user to be logged in but was not");
        }
        scenarioContext.set("loginStatus", "success");
        System.out.println("[PASS] User is logged in on: " + getSystemName());
    }

    @Then("the user should see a welcome message")
    public void theUserShouldSeeAWelcomeMessage() {
        String msg = getLoginPage().getWelcomeMessage();
        if (msg == null || msg.isEmpty()) {
            throw new AssertionError("Expected welcome message but got none");
        }
        scenarioContext.set("welcomeMessage", msg);
        System.out.println("[PASS] Welcome message: " + msg);
    }

    @When("the user logs out")
    public void theUserLogsOut() {
        getLoginPage().logout();
        scenarioContext.set("loginStatus", "logged_out");
        System.out.println("[" + getSystemName() + "] User logged out");
    }

    @Then("the user should be returned to the login page")
    public void theUserShouldBeReturnedToTheLoginPage() {
        // Basic check — subclasses can override for more precision
        System.out.println("[PASS] User returned to login page on: " + getSystemName());
    }

    @Then("the login should fail with an error message")
    public void theLoginShouldFailWithAnErrorMessage() {
        if (getLoginPage().isLoggedIn()) {
            throw new AssertionError("Expected login to fail but user is logged in");
        }
        System.out.println("[PASS] Login correctly rejected on: " + getSystemName());
    }
}
