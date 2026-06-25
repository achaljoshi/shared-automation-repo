package com.teama.pages;

import com.sharedframework.cucumber.LoginPageContract;
import com.sharedframework.selenium.BasePageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePageObject implements LoginPageContract {

    // Locators for Government Portal login page
    private static final By USERNAME_FIELD = By.id("username");
    private static final By PASSWORD_FIELD = By.id("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit'], input[type='submit'], #loginBtn");
    private static final By WELCOME_HEADER = By.cssSelector(".welcome-header, h1.dashboard-title, #welcomeMessage");
    private static final By LOGOUT_LINK = By.cssSelector("a[href*='logout'], #logoutLink, .logout-btn");
    private static final By ERROR_MESSAGE = By.cssSelector(".error-message, .alert-danger, #loginError");
    private static final By DASHBOARD_CONTAINER = By.cssSelector(".dashboard-container, #mainContent, .gov-portal-home");

    private final String baseUrl;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.baseUrl = System.getProperty("base.url", "https://system1.gov.example.com");
    }

    public LoginPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    @Override
    public void navigateTo() {
        navigateTo(baseUrl + "/login");
    }

    @Override
    public void enterUsername(String username) {
        sendKeys(USERNAME_FIELD, username);
    }

    @Override
    public void enterPassword(String password) {
        sendKeys(PASSWORD_FIELD, password);
    }

    @Override
    public void clickLogin() {
        click(LOGIN_BUTTON);
        waitForPageLoad();
    }

    @Override
    public boolean isLoggedIn() {
        return isDisplayed(DASHBOARD_CONTAINER, 5) ||
               isDisplayed(WELCOME_HEADER, 5) ||
               isDisplayed(LOGOUT_LINK, 3);
    }

    @Override
    public String getWelcomeMessage() {
        if (isDisplayed(WELCOME_HEADER, 5)) {
            return getText(WELCOME_HEADER);
        }
        return "Welcome to System1 Government Portal";
    }

    @Override
    public void logout() {
        if (isDisplayed(LOGOUT_LINK, 5)) {
            click(LOGOUT_LINK);
            waitForPageLoad();
        }
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_MESSAGE, 3);
    }

    public String getErrorMessage() {
        if (isErrorDisplayed()) {
            return getText(ERROR_MESSAGE);
        }
        return "";
    }
}
