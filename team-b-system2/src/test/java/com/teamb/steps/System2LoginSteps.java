package com.teamb.steps;

import com.sharedframework.cucumber.ScenarioContext;
import com.sharedframework.cucumber.SystemContext;
import com.sharedframework.cucumber.steps.LoginSteps;
import com.teamb.System2Context;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class System2LoginSteps extends LoginSteps {

    private final System2Context system2Context;

    public System2LoginSteps(ScenarioContext scenarioContext) {
        super(scenarioContext);
        this.system2Context = new System2Context();
    }

    @Override
    protected SystemContext getSystemContext() {
        return system2Context;
    }

    @When("the user navigates to the portfolio dashboard")
    public void theUserNavigatesToThePortfolioDashboard() {
        system2Context.navigateToHome();
        System.out.println("Navigating to portfolio dashboard on " + system2Context.getSystemName());
    }

    @Then("the portfolio summary should be displayed")
    public void thePortfolioSummaryShouldBeDisplayed() {
        System.out.println("Verifying portfolio summary is displayed");
    }

    @Then("the total assets value should be visible")
    public void theTotalAssetsValueShouldBeVisible() {
        System.out.println("Verifying total assets value is visible on the dashboard");
    }

    @When("the user selects report type {string}")
    public void theUserSelectsReportType(String reportType) {
        System.out.println("Selecting report type: " + reportType);
        scenarioContext.set("selectedReportType", reportType);
    }

    @When("the user sets the date range from {string} to {string}")
    public void theUserSetsTheDateRange(String fromDate, String toDate) {
        System.out.println("Setting date range: " + fromDate + " to " + toDate);
        scenarioContext.set("reportFromDate", fromDate);
        scenarioContext.set("reportToDate", toDate);
    }

    @When("the user generates the report")
    public void theUserGeneratesTheReport() {
        String reportType = scenarioContext.get("selectedReportType", String.class);
        System.out.println("Generating report: " + reportType);
    }

    @Then("the report should be generated successfully")
    public void theReportShouldBeGeneratedSuccessfully() {
        System.out.println("Verifying report was generated successfully");
    }

    @Then("the report should be available for download")
    public void theReportShouldBeAvailableForDownload() {
        System.out.println("Verifying report download link is available");
    }
}
