@register @regression
Feature: Create account

  Scenario: Registration form is displayed
    Given I open the registration page
    Then the registration form should be displayed

  Scenario: Empty registration is rejected
    Given I open the registration page
    When I submit the registration form without entering anything
    Then I should still be on the registration page
    And a validation or error message should be displayed

  Scenario: Invalid email is rejected
    Given I open the registration page
    When I fill the registration form with email "not-an-email" and password "Test@12345" and confirm password "Test@12345"
    And I submit the registration form
    Then I should still be on the registration page
    And a validation or error message should be displayed

  Scenario: Mismatched passwords are rejected
    Given I open the registration page
    When I fill the registration form with email "qa.user@example.com" and password "Test@12345" and confirm password "Different@123"
    And I submit the registration form
    Then I should still be on the registration page
    And a validation or error message should be displayed

  @valid-register
  Scenario: A new account can be created (creates a real account - off by default)
    Given I open the registration page
    When I register a new unique account
    Then the account should be created
