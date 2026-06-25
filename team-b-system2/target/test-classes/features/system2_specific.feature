@sys2
Feature: System2 Financial Dashboard Specific Features
  As a financial system user
  I want to access financial dashboard features
  So that I can manage my portfolio and generate financial reports
  Note: Login steps (Background) are inherited from BaseLoginSteps via team-a test-jar.
  Only the portfolio/report steps below are System2-specific.

  Background:
    Given the user is on the login page
    When the user enters valid credentials
    And the user clicks the login button
    Then the user should be logged in successfully

  Scenario: View portfolio summary dashboard
    When the user navigates to the portfolio dashboard
    Then the portfolio summary should be displayed
    And the total assets value should be visible

  Scenario: Generate a quarterly financial report
    When the user selects report type "Quarterly Statement"
    And the user sets the date range from "2024-01-01" to "2024-03-31"
    And the user generates the report
    Then the report should be generated successfully
    And the report should be available for download

  Scenario: Generate an annual financial report
    When the user selects report type "Annual Summary"
    And the user sets the date range from "2024-01-01" to "2024-12-31"
    And the user generates the report
    Then the report should be generated successfully
    And the report should be available for download
