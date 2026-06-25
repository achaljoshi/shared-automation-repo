package com.teama.steps.base;

import com.sharedframework.cucumber.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Shared login step definitions — packaged in team-a's test-jar.
 *
 * Any team that:
 *   1. Imports team-a-system1-*-tests.jar as a Maven dependency
 *   2. Adds "com.teama.steps.base" to their runner's glue path
 *   3. Registers their PageObjectProvider via PageObjectRegistry.set() in a @Before hook
 *
 * ...gets ALL these steps for free. No copy-paste. No inheritance.
 *
 * The active PageObjectProvider is fetched from PageObjectRegistry (ThreadLocal),
 * which each team populates in their own @Before(order=0) setup hook.
 */
public class SharedLoginSteps {

    private final ScenarioContext scenarioContext;

    public SharedLoginSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    private PageObjectProvider pages() {
        return PageObjectRegistry.get();
    }

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        pages().getLoginPage().navigateTo();
        scenarioContext.set("currentPage", "login");
        System.out.println("[" + pages().getSystemName() + "] Navigated to login page");
    }

    @When("the user enters valid credentials")
    public void theUserEntersValidCredentials() {
        String username = System.getProperty("test.username", "testuser");
        String password = System.getProperty("test.password", "testpass");
        pages().getLoginPage().enterUsername(username);
        pages().getLoginPage().enterPassword(password);
        scenarioContext.set("attemptedUser", username);
    }

    @When("the user enters username {string} and password {string}")
    public void theUserEntersUsernameAndPassword(String username, String password) {
        pages().getLoginPage().enterUsername(username);
        pages().getLoginPage().enterPassword(password);
        scenarioContext.set("attemptedUser", username);
    }

    @When("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        pages().getLoginPage().clickLogin();
    }

    @Then("the user should be logged in successfully")
    public void theUserShouldBeLoggedInSuccessfully() {
        if (!pages().getLoginPage().isLoggedIn()) {
            throw new AssertionError("Expected login success on " + pages().getSystemName() + " but was not logged in");
        }
        scenarioContext.set("loginStatus", "success");
        System.out.println("[PASS] Logged in on: " + pages().getSystemName());
    }

    @Then("the user should see a welcome message")
    public void theUserShouldSeeAWelcomeMessage() {
        String msg = pages().getLoginPage().getWelcomeMessage();
        if (msg == null || msg.isEmpty()) {
            throw new AssertionError("Expected welcome message on " + pages().getSystemName() + " but got none");
        }
        scenarioContext.set("welcomeMessage", msg);
        System.out.println("[PASS] Welcome message: " + msg);
    }

    @When("the user logs out")
    public void theUserLogsOut() {
        pages().getLoginPage().logout();
        scenarioContext.set("loginStatus", "logged_out");
        System.out.println("[" + pages().getSystemName() + "] Logged out");
    }

    @Then("the user should be returned to the login page")
    public void theUserShouldBeReturnedToTheLoginPage() {
        System.out.println("[PASS] Returned to login page on: " + pages().getSystemName());
    }

    @Then("the login should fail with an error message")
    public void theLoginShouldFailWithAnErrorMessage() {
        if (pages().getLoginPage().isLoggedIn()) {
            throw new AssertionError("Expected login to fail on " + pages().getSystemName() + " but user is logged in");
        }
        System.out.println("[PASS] Login correctly rejected on: " + pages().getSystemName());
    }
}
