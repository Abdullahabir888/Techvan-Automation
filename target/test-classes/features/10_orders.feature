@order @regression
Feature: Orders

  Scenario: Order tracking page opens
    Given I open the order tracking page
    Then the order tracking form should be displayed
    And the page should not be an error page

  Scenario: Empty order tracking request is rejected
    Given I open the order tracking page
    When I submit the order tracking form without an order number
    Then a validation or error message should be displayed on the tracking form

  Scenario: Unknown order number is reported as not found
    Given I open the order tracking page
    When I track the order "TV-000000000"
    Then the tracking result should say the order was not found
    And the page should not be an error page

  @account-orders
  Scenario: My orders page opens after login
    Given I open the login page
    When I log in with the configured valid credentials
    Then I should be logged in
    When I open my orders page
    Then the page should not be an error page
    And the URL should contain "order"
