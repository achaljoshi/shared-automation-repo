@shared @search
Feature: Common Search Functionality
  As a user of any system
  I want to search for records
  So that I can find relevant information

  Scenario: Search returns results
    Given the user is on the login page
    When the user enters valid credentials
    And the user clicks the login button
    Then the user should be logged in successfully
    When the user searches for "annual report"
    Then search results should be displayed
    And the results should contain "annual report"

  Scenario: Search with no results
    Given the user is on the login page
    When the user enters valid credentials
    And the user clicks the login button
    When the user searches for "xyznonexistentrecord999"
    Then no results message should be displayed
