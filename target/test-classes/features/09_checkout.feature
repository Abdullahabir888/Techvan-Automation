@checkout @regression
Feature: Checkout (stops before payment - never places an order)

  Background:
    Given I open the product "/en/shop/xiaomi-redmi-a7"
    When I click Add to Cart
    And I open the cart page
    And I proceed to checkout
    And I sign in if the site asks me to

  Scenario: Checkout shows the order summary and total
    Then the checkout page should be displayed
    And the order summary should list "REDMI A7"
    And the checkout total should be a BDT amount

  Scenario: Checkout asks for delivery details
    Then the checkout page should be displayed
    And the checkout form should ask for name, phone and address

  Scenario: Payment methods are listed before paying
    Then the checkout page should be displayed
    And payment methods should be listed

  @checkout-progress
  Scenario: Delivery details lead to the payment step (off by default, never pays)
    Then the checkout page should be displayed
    When I fill the delivery details with test data
    And I continue to the payment step
    Then the payment step should be displayed
