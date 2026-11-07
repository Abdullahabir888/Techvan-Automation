@category @regression
Feature: Category, filter and shop listing

  Scenario Outline: Brand category lists only that brand's products with price and image
    Given I open the "<slug>" category
    Then the listing should show at least 1 product
    And every product name should contain "<brand>"
    And every product card should show a BDT price
    And every product price should be greater than zero
    And every product card should have a loaded image

    Examples:
      | slug     | brand    |
      | honor    | Honor    |
      | redmi    | Redmi    |
      | infinix  | Infinix  |
      | itel     | iTel     |

  Scenario: Phones category lists products with prices
    Given I open the "phones" category
    Then the listing should show at least 1 product
    And every product card should show a BDT price

  @filter
  Scenario Outline: Filtering the phones listing narrows it to the chosen option
    Given I open the "phones" category
    When I apply the "<option>" filter
    Then the listing should show at least 1 product
    And every product name should contain "<option>"
    And every product card should show a BDT price

    Examples:
      | option  |
      | Honor   |
      | Infinix |

  Scenario: Shop page lists products
    Given I open the shop page
    Then the listing should show at least 1 product
    And every product price should be greater than zero

  Scenario: Open a product from a category
    Given I open the "phones" category
    When I open the first product in the listing
    Then the product page should show a name and price
