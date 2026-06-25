@shared @login
Feature: Common Login Functionality
  As a user of any system
  I want to log in with valid credentials
  So that I can access the system

  Background:
    Given the user is on the login page

  Scenario: Successful login with valid credentials
    When the user enters valid credentials
    And the user clicks the login button
    Then the user should be logged in successfully
    And the user should see a welcome message

  Scenario: Login and logout flow
    When the user enters valid credentials
    And the user clicks the login button
    Then the user should be logged in successfully
    When the user logs out
    Then the user should be returned to the login page

  Scenario: Login with specific username and password
    When the user enters username "admin" and password "admin123"
    And the user clicks the login button
    Then the user should be logged in successfully
