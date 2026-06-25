package com.teama.pages;

import com.sharedframework.cucumber.LoginPageContract;

/**
 * System1 Government Portal login page.
 * Implements LoginPageContract with simulated logic (print statements + boolean returns)
 * so the demo compiles and runs without a real browser.
 */
public class System1LoginPage implements LoginPageContract {

    private static final String BASE_URL = "https://system1.gov.example.com";

    private boolean loggedIn = false;
    private String currentUser = null;

    @Override
    public void navigateTo() {
        System.out.println("[System1LoginPage] Navigating to: " + BASE_URL + "/login");
    }

    @Override
    public void enterUsername(String username) {
        System.out.println("[System1LoginPage] Entering username: " + username);
        this.currentUser = username;
    }

    @Override
    public void enterPassword(String password) {
        System.out.println("[System1LoginPage] Entering password: [REDACTED]");
    }

    @Override
    public void clickLogin() {
        // Simulate successful login for any non-empty username
        this.loggedIn = (currentUser != null && !currentUser.isEmpty());
        System.out.println("[System1LoginPage] Clicked login button — loggedIn=" + loggedIn);
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public String getWelcomeMessage() {
        if (loggedIn) {
            return "Welcome to the Government Portal, " + currentUser + "!";
        }
        return null;
    }

    @Override
    public void logout() {
        this.loggedIn = false;
        System.out.println("[System1LoginPage] User logged out from System1 Government Portal");
        this.currentUser = null;
    }
}
