package com.sharedframework.cucumber.steps;

import com.sharedframework.cucumber.LoginPageContract;
import com.sharedframework.cucumber.ScenarioContext;
import com.sharedframework.cucumber.SystemContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class LoginSteps {

    protected final ScenarioContext scenarioContext;

    public LoginSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    protected abstract SystemContext getSystemContext();

    @Given("the application is configured for testing")
    public void theApplicationIsConfiguredForTesting() {
        System.out.println("System under test: " + getSystemContext().getSystemName());
    }

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        LoginPageContract loginPage = getSystemContext().getLoginPage();
        loginPage.navigateTo();
        System.out.println("Navigated to login page for: " + getSystemContext().getSystemName());
    }

    @When("the user enters valid credentials")
    public void theUserEntersValidCredentials() {
        Properties config = loadSystemConfig();
        String username = config.getProperty("username", "testuser@example.com");
        String password = config.getProperty("password", "TestPass@123");

        LoginPageContract loginPage = getSystemContext().getLoginPage();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        scenarioContext.set("loggedInUsername", username);
    }

    @When("the user enters username {string} and password {string}")
    public void theUserEntersUsernameAndPassword(String username, String password) {
        LoginPageContract loginPage = getSystemContext().getLoginPage();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        scenarioContext.set("loggedInUsername", username);
    }

    @Then("the user should be logged in successfully")
    public void theUserShouldBeLoggedInSuccessfully() {
        LoginPageContract loginPage = getSystemContext().getLoginPage();
        assertTrue("User should be logged in but isLoggedIn() returned false",
                loginPage.isLoggedIn());
        System.out.println("Successfully logged into: " + getSystemContext().getSystemName());
    }

    @Then("the welcome message should be displayed")
    public void theWelcomeMessageShouldBeDisplayed() {
        LoginPageContract loginPage = getSystemContext().getLoginPage();
        String welcomeMessage = loginPage.getWelcomeMessage();
        assertNotNull("Welcome message should not be null", welcomeMessage);
        assertFalse("Welcome message should not be empty", welcomeMessage.trim().isEmpty());
        System.out.println("Welcome message: " + welcomeMessage);
        scenarioContext.set("welcomeMessage", welcomeMessage);
    }

    @When("the user logs out")
    public void theUserLogsOut() {
        LoginPageContract loginPage = getSystemContext().getLoginPage();
        loginPage.logout();
        System.out.println("User logged out from: " + getSystemContext().getSystemName());
    }

    @Then("the user should be on the login page")
    public void theUserShouldBeOnTheLoginPage() {
        LoginPageContract loginPage = getSystemContext().getLoginPage();
        assertFalse("User should be on login page (not logged in) but isLoggedIn() returned true",
                loginPage.isLoggedIn());
    }

    private Properties loadSystemConfig() {
        Properties props = new Properties();
        String systemName = getSystemContext().getSystemName().toLowerCase()
                .replaceAll("[^a-z0-9]", "").replace("system1", "system1").replace("system2", "system2");

        // Try to load system-specific config
        String[] configFiles = {"system1.properties", "system2.properties", "config.properties"};
        for (String configFile : configFiles) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("config/" + configFile)) {
                if (is != null) {
                    props.load(is);
                    if (!props.isEmpty()) {
                        break;
                    }
                }
            } catch (IOException e) {
                // Try next file
            }
        }
        return props;
    }

    // Assertion helpers (avoid JUnit direct import issues in step classes)
    private void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private void assertFalse(String message, boolean condition) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    private void assertNotNull(String message, Object value) {
        if (value == null) {
            throw new AssertionError(message);
        }
    }
}
