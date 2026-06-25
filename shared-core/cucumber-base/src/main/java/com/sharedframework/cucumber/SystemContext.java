package com.sharedframework.cucumber;

public interface SystemContext {
    LoginPageContract getLoginPage();
    SearchPageContract getSearchPage();
    String getSystemName();
    void navigateToHome();
}
