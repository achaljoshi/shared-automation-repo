package com.teamb.pages;

import com.sharedframework.cucumber.LoginPageContract;

/**
 * System2 Financial Dashboard login page.
 * Implements LoginPageContract with simulated logic (print statements + in-memory state)
 * so the demo compiles and runs without a real browser.
 * System2-specific locators: #fin-username, #fin-password, .dashboard-login-btn
 */
public class System2LoginPage implements LoginPageContract {

    private static final String BASE_URL = "https://system2.fintech.example.com";

    private boolean loggedIn = false;
    private String currentUser = null;

    @Override
    public void navigateTo() {
        System.out.println("[System2LoginPage] Navigating to: " + BASE_URL + "/auth/login");
    }

    @Override
    public void enterUsername(String username) {
        System.out.println("[System2LoginPage] Entering username into #fin-username: " + username);
        this.currentUser = username;
    }

    @Override
    public void enterPassword(String password) {
        System.out.println("[System2LoginPage] Entering password into #fin-password: [REDACTED]");
    }

    @Override
    public void clickLogin() {
        // Simulate successful login for any non-empty username
        this.loggedIn = (currentUser != null && !currentUser.isEmpty());
        System.out.println("[System2LoginPage] Clicked .dashboard-login-btn — loggedIn=" + loggedIn);
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public String getWelcomeMessage() {
        if (loggedIn) {
            return "Welcome to System2 Financial Dashboard, " + currentUser + "!";
        }
        return null;
    }

    @Override
    public void logout() {
        this.loggedIn = false;
        System.out.println("[System2LoginPage] User logged out from System2 Financial Dashboard");
        this.currentUser = null;
    }
}
