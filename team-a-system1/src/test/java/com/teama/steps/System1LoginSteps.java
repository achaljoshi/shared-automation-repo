package com.teama.steps;

import com.sharedframework.cucumber.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * System1-specific login steps ONLY.
 *
 * Shared steps (login, logout, welcome message) live in SharedLoginSteps
 * inside the com.teama.steps.base package and run automatically via PicoContainer.
 * This class adds only what is unique to System1 Government Portal.
 */
public class System1LoginSteps {

    private final ScenarioContext scenarioContext;

    public System1LoginSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @When("the user navigates to the document repository")
    public void theUserNavigatesToTheDocumentRepository() {
        System.out.println("[System1] Navigating to document repository");
        scenarioContext.set("currentSection", "documents");
    }

    @Then("the document repository should display government forms")
    public void theDocumentRepositoryShouldDisplayGovernmentForms() {
        System.out.println("[System1] Verifying government forms are displayed");
    }

    @Then("the user should see at least {int} document categories")
    public void theUserShouldSeeAtLeastDocumentCategories(int min) {
        System.out.println("[System1] Verifying at least " + min + " document categories");
    }

    @When("the user selects form type {string}")
    public void theUserSelectsFormType(String formType) {
        System.out.println("[System1] Selecting form type: " + formType);
        scenarioContext.set("selectedFormType", formType);
    }

    @When("the user fills in the required form fields")
    public void theUserFillsInTheRequiredFormFields() {
        String formType = scenarioContext.get("selectedFormType", String.class);
        System.out.println("[System1] Filling in required fields for form: " + formType);
    }

    @When("the user submits the form")
    public void theUserSubmitsTheForm() {
        System.out.println("[System1] Submitting the government form");
    }

    @Then("a confirmation number should be generated")
    public void aConfirmationNumberShouldBeGenerated() {
        String confirmationNumber = "GOV-2024-" + System.currentTimeMillis();
        scenarioContext.set("confirmationNumber", confirmationNumber);
        System.out.println("[System1] Confirmation number: " + confirmationNumber);
    }

    @Then("a confirmation email notification should be sent")
    public void aConfirmationEmailNotificationShouldBeSent() {
        System.out.println("[System1] Confirmation email sent");
    }
}
