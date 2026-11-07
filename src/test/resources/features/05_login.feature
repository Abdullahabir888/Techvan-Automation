@login @regression
Feature: TechVan login

  Scenario: Login form is displayed
    Given I open the login page
    Then the login form should be displayed

  Scenario: Submitting an empty form is rejected
    Given I open the login page
    When I submit the login form without entering anything
    Then I should still be on the login page
    And a validation or error message should be displayed

  Scenario: Invalid credentials are rejected
    Given I open the login page
    When I log in with email "wrong.user@example.com" and password "WrongPass#123"
    Then I should still be on the login page
    And a validation or error message should be displayed

  @valid-login
  Scenario: Valid credentials log the user in
    Given I open the login page
    When I log in with the configured valid credentials
    Then I should be logged in
