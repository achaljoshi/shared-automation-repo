package com.sharedframework.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public abstract class BasePageObject {

    protected WebDriver driver;

    public BasePageObject(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    protected void click(By locator) {
        WebElement element = WaitUtils.waitForClickable(driver, locator, 30);
        element.click();
    }

    protected void sendKeys(By locator, String text) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 30);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 30);
        return element.getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isDisplayed(By locator, int timeoutSeconds) {
        try {
            WaitUtils.waitForVisible(driver, locator, timeoutSeconds);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected WebElement waitForElement(By locator) {
        return WaitUtils.waitForVisible(driver, locator, 30);
    }

    protected WebElement waitForElement(By locator, int timeoutSeconds) {
        return WaitUtils.waitForVisible(driver, locator, timeoutSeconds);
    }

    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    protected int countElements(By locator) {
        return driver.findElements(locator).size();
    }

    protected void selectByVisibleText(By locator, String text) {
        WebElement element = waitForElement(locator);
        new Select(element).selectByVisibleText(text);
    }

    protected void selectByValue(By locator, String value) {
        WebElement element = waitForElement(locator);
        new Select(element).selectByValue(value);
    }

    protected void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void jsClick(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected String getAttributeValue(By locator, String attribute) {
        WebElement element = waitForElement(locator);
        return element.getAttribute(attribute);
    }

    protected void waitForPageLoad() {
        WaitUtils.waitForPageLoad(driver, 30);
    }

    protected void navigateTo(String url) {
        driver.get(url);
        waitForPageLoad();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
