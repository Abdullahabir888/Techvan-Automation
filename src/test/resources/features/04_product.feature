@product @regression
Feature: Product details page

  Background:
    Given I open the product "/en/shop/xiaomi-redmi-a7"

  @smoke
  Scenario: Product name and price
    Then the product name should contain "REDMI A7"
    And the product price should be greater than 0

  Scenario: Purchase buttons are visible
    Then the Add to Cart button should be displayed
    And the Buy Now button should be displayed
    And the Add to Compare button should be displayed

  Scenario: Default quantity is 1
    Then the quantity should be "1"

  Scenario: Breadcrumbs are shown
    Then breadcrumbs should be displayed

  Scenario Outline: Product info tabs
    Then the "<tab>" tab should be displayed
    When I open the "<tab>" tab
    Examples:
      | tab                    |
      | Description            |
      | Additional Info        |
      | Warranty & Support     |
      | Product Specifications |

  Scenario: Select a color variant
    When I select the color "Sky Blue"
    Then the product name should contain "REDMI A7"

  @cart
  Scenario: Add to cart increases cart count
    When I note the cart count
    And I click Add to Cart
    Then the cart count should increase

  Scenario: Add to compare
    When I click Add to Compare
    And I go to the compare page
    Then the page should contain "REDMI A7"

  Scenario: Open product from home page
    Given I am on the TechVan home page
    When I open the product "iTel A100C" from the home page
    Then the product name should contain "A100C"
