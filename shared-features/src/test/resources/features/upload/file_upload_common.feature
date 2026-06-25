@shared @upload
Feature: Common File Upload Functionality
  As a user of any system in the shared framework
  I want to be able to upload files
  So that I can submit documents and attachments

  Background:
    Given the user is on the login page
    And the user enters valid credentials
    And the user should be logged in successfully

  Scenario: Upload a valid PDF document
    Given the user is on the file upload page
    When the user selects file "sample-document.pdf" for upload
    And the user clicks the upload button
    Then the upload should complete successfully
    And a success confirmation should be displayed

  Scenario: Upload is rejected for an unsupported file type
    Given the user is on the file upload page
    When the user selects file "invalid-file.exe" for upload
    And the user clicks the upload button
    Then an error message about unsupported file type should be displayed

  Scenario: Upload is rejected when file exceeds size limit
    Given the user is on the file upload page
    When the user selects file "large-file-over-limit.pdf" for upload
    And the user clicks the upload button
    Then an error message about file size limit should be displayed

  Scenario: Multiple files can be uploaded in sequence
    Given the user is on the file upload page
    When the user selects file "document1.pdf" for upload
    And the user clicks the upload button
    Then the upload should complete successfully
    When the user selects file "document2.pdf" for upload
    And the user clicks the upload button
    Then the upload should complete successfully
    And the uploaded files list should contain 2 items
