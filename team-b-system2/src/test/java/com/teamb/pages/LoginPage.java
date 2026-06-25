package com.teamb.pages;

import com.sharedframework.cucumber.LoginPageContract;
import com.sharedframework.selenium.BasePageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePageObject implements LoginPageContract {

    // Financial dashboard login page - uses email + password + potential MFA
    private static final By EMAIL_FIELD = By.cssSelector("input[type='email'], input[name='email'], #emailInput");
    private static final By PASSWORD_FIELD = By.cssSelector("input[type='password'], #passwordInput, input[name='password']");
    private static final By SIGN_IN_BUTTON = By.cssSelector("button.signin-btn, #signInButton, button[data-action='login']");
    private static final By DASHBOARD_HEADER = By.cssSelector(".portfolio-header, #dashboardTitle, .fin-dashboard-home");
    private static final By USER_MENU = By.cssSelector(".user-profile-menu, #userMenu, .account-avatar");
    private static final By SIGN_OUT_OPTION = By.cssSelector("a.sign-out, #signOut, .logout-option");
    private static final By MFA_HINT = By.cssSelector(".mfa-prompt, #mfaSection, .two-factor-notice");
    private static final By LOGIN_ERROR = By.cssSelector(".login-error, .auth-error, #loginErrorMsg");

    private final String baseUrl;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.baseUrl = System.getProperty("base.url", "https://system2.fintech.example.com");
    }

    public LoginPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    @Override
    public void navigateTo() {
        navigateTo(baseUrl + "/auth/login");
    }

    @Override
    public void enterUsername(String username) {
        // Financial system uses email field
        sendKeys(EMAIL_FIELD, username);
    }

    @Override
    public void enterPassword(String password) {
        sendKeys(PASSWORD_FIELD, password);
    }

    @Override
    public void clickLogin() {
        click(SIGN_IN_BUTTON);
        waitForPageLoad();
    }

    @Override
    public boolean isLoggedIn() {
        return isDisplayed(DASHBOARD_HEADER, 5) ||
               isDisplayed(USER_MENU, 5);
    }

    @Override
    public String getWelcomeMessage() {
        if (isDisplayed(DASHBOARD_HEADER, 5)) {
            return getText(DASHBOARD_HEADER);
        }
        return "Welcome to System2 Financial Dashboard";
    }

    @Override
    public void logout() {
        if (isDisplayed(USER_MENU, 5)) {
            click(USER_MENU);
            if (isDisplayed(SIGN_OUT_OPTION, 3)) {
                click(SIGN_OUT_OPTION);
                waitForPageLoad();
            }
        }
    }

    public boolean isMfaPromptVisible() {
        return isDisplayed(MFA_HINT, 3);
    }

    public boolean isLoginErrorDisplayed() {
        return isDisplayed(LOGIN_ERROR, 3);
    }

    public String getLoginError() {
        return isLoginErrorDisplayed() ? getText(LOGIN_ERROR) : "";
    }
}
