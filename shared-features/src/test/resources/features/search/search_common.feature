@shared @search
Feature: Common Search Functionality
  As a user of any system in the shared framework
  I want to be able to search for content
  So that I can quickly find what I need

  Background:
    Given the user is on the login page
    And the user enters valid credentials
    And the user should be logged in successfully

  Scenario: Search returns results for a valid keyword
    Given the user is on the search page
    When the user searches for "report"
    Then search results should be displayed
    And the result count should be greater than zero

  Scenario: Search shows no results for a nonsense keyword
    Given the user is on the search page
    When the user searches for "xyzzy123nosuchrecord"
    Then no results message should be displayed

  Scenario: User can clear a search
    Given the user is on the search page
    When the user searches for "report"
    And the user clears the search
    Then search results should be displayed
