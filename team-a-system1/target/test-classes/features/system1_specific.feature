@sys1
Feature: System1 Government Portal Specific Features
  As a citizen using the government portal
  I want to access government-specific features
  So that I can complete official government transactions

  Background:
    Given the user is on the login page
    And the user enters valid credentials
    And the user should be logged in successfully

  Scenario: Access government document repository
    When the user navigates to the document repository
    Then the document repository should display government forms
    And the user should see at least 5 document categories

  Scenario: Submit a government form online
    When the user selects form type "Tax Declaration"
    And the user fills in the required form fields
    And the user submits the form
    Then a confirmation number should be generated
    And a confirmation email notification should be sent
