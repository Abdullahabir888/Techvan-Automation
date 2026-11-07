@cart @regression
Feature: Shopping cart

  Background:
    Given I open the product "/en/shop/xiaomi-redmi-a7"
    When I click Add to Cart
    And I open the cart page

  Scenario: Added product appears in the cart
    Then the cart should contain the product "REDMI A7"
    And the cart should show a BDT price
    And the cart quantity should be "1"

  Scenario: Increasing the quantity raises the total
    When I note the cart total
    And I increase the cart quantity
    Then the cart total should increase

  Scenario: Removing the item empties the cart
    When I remove the item from the cart
    Then the cart should be empty

  Scenario: Cart offers a way to check out
    Then the checkout button should be displayed
