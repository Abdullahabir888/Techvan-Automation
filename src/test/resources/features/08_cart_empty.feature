@cart @regression
Feature: Empty cart

  Scenario: A fresh visitor sees an empty cart
    Given I open the cart page
    Then the cart should be empty
