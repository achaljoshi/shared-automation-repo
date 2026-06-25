package com.sharedframework.cucumber;

public interface LoginPageContract {
    void navigateTo();
    void enterUsername(String username);
    void enterPassword(String password);
    void clickLogin();
    boolean isLoggedIn();
    String getWelcomeMessage();
    void logout();
}
