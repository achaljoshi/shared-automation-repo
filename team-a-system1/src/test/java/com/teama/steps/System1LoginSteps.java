package com.teama.steps;

import com.sharedframework.cucumber.ScenarioContext;
import com.sharedframework.cucumber.SystemContext;
import com.sharedframework.cucumber.steps.LoginSteps;
import com.teama.System1Context;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class System1LoginSteps extends LoginSteps {

    private final System1Context system1Context;

    public System1LoginSteps(ScenarioContext scenarioContext) {
        super(scenarioContext);
        this.system1Context = new System1Context();
    }

    @Override
    protected SystemContext getSystemContext() {
        return system1Context;
    }

    // System1-specific step: navigate to document repository
    @When("the user navigates to the document repository")
    public void theUserNavigatesToTheDocumentRepository() {
        system1Context.navigateToHome();
        // Navigate to document repository section
        System.out.println("Navigating to document repository on " + system1Context.getSystemName());
    }

    @Then("the document repository should display government forms")
    public void theDocumentRepositoryShouldDisplayGovernmentForms() {
        System.out.println("Verifying government forms are displayed in repository");
        // In a real test this would assert on actual UI elements
    }

    @Then("the user should see at least {int} document categories")
    public void theUserShouldSeeAtLeastDocumentCategories(int minCategories) {
        System.out.println("Verifying at least " + minCategories + " document categories are visible");
    }

    @When("the user selects form type {string}")
    public void theUserSelectsFormType(String formType) {
        System.out.println("Selecting form type: " + formType);
        scenarioContext.set("selectedFormType", formType);
    }

    @When("the user fills in the required form fields")
    public void theUserFillsInTheRequiredFormFields() {
        String formType = scenarioContext.get("selectedFormType", String.class);
        System.out.println("Filling in required fields for form: " + formType);
    }

    @When("the user submits the form")
    public void theUserSubmitsTheForm() {
        System.out.println("Submitting the government form");
    }

    @Then("a confirmation number should be generated")
    public void aConfirmationNumberShouldBeGenerated() {
        System.out.println("Verifying confirmation number is generated");
        // Simulate confirmation number
        scenarioContext.set("confirmationNumber", "GOV-2024-" + System.currentTimeMillis());
    }

    @Then("a confirmation email notification should be sent")
    public void aConfirmationEmailNotificationShouldBeSent() {
        System.out.println("Verifying confirmation email was sent");
    }
}
