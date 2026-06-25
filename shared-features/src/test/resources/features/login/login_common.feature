@shared @login
Feature: Common Login Functionality
  As a user of any system in the shared framework
  I want to be able to log in and log out reliably
  So that I can access the system features securely

  Scenario: Successful login with valid credentials
    Given the user is on the login page
    When the user enters valid credentials
    Then the user should be logged in successfully
    And the welcome message should be displayed

  Scenario: Login with explicit username and password
    Given the user is on the login page
    When the user enters username "admin@example.com" and password "AdminPass@99"
    Then the user should be logged in successfully

  Scenario: User can log out after logging in
    Given the user is on the login page
    When the user enters valid credentials
    Then the user should be logged in successfully
    When the user logs out
    Then the user should be on the login page
