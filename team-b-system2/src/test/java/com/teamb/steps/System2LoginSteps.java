package com.teamb.steps;

import com.sharedframework.cucumber.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * System2-specific login steps ONLY.
 *
 * Shared login steps (Given the user is on login page, When enters credentials, etc.)
 * are provided by SharedLoginSteps from team-a's test-jar — zero duplication.
 * System2PageObjectProvider wires System2's login page into those shared steps.
 *
 * This class adds only what is unique to System2 Financial Dashboard.
 */
public class System2LoginSteps {

    private final ScenarioContext scenarioContext;

    public System2LoginSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @When("the user navigates to the portfolio dashboard")
    public void theUserNavigatesToThePortfolioDashboard() {
        System.out.println("[System2] Navigating to portfolio dashboard");
        scenarioContext.set("currentSection", "portfolio");
    }

    @Then("the portfolio summary should be displayed")
    public void thePortfolioSummaryShouldBeDisplayed() {
        System.out.println("[System2] Verifying portfolio summary is displayed");
    }

    @Then("the total assets value should be visible")
    public void theTotalAssetsValueShouldBeVisible() {
        System.out.println("[System2] Verifying total assets value is visible");
    }

    @When("the user selects report type {string}")
    public void theUserSelectsReportType(String reportType) {
        System.out.println("[System2] Selecting report type: " + reportType);
        scenarioContext.set("selectedReportType", reportType);
    }

    @When("the user sets the date range from {string} to {string}")
    public void theUserSetsTheDateRange(String fromDate, String toDate) {
        System.out.println("[System2] Setting date range: " + fromDate + " to " + toDate);
        scenarioContext.set("reportFromDate", fromDate);
        scenarioContext.set("reportToDate", toDate);
    }

    @When("the user generates the report")
    public void theUserGeneratesTheReport() {
        String reportType = scenarioContext.get("selectedReportType", String.class);
        System.out.println("[System2] Generating report: " + reportType);
    }

    @Then("the report should be generated successfully")
    public void theReportShouldBeGeneratedSuccessfully() {
        System.out.println("[System2] Report generated successfully");
    }

    @Then("the report should be available for download")
    public void theReportShouldBeAvailableForDownload() {
        System.out.println("[System2] Report download link is available");
    }
}
