
@navigation @regression
Feature: TechVan Header and Footer Navigation

  Background:
    Given I am on the TechVan home page

  @smoke
  Scenario: Shop link opens the shop page
    When I click the Shop link
    Then the URL should contain "/en/shop"
    And the page should not be an error page

  Scenario: Recently Viewed navigation
    When I click the Recently Viewed link
    Then the URL should contain one of "/recently-viewed,login,sign-in,signin"
    And the page should not be an error page

  Scenario: Compare icon navigation
    When I click the compare icon
    Then the URL should contain "/compare"
    And the page should not be an error page

  Scenario: Wishlist authentication for guest users
    When I click the wishlist icon
    Then the URL should contain one of "wishlist,login,sign-in,signin"
    And the page should not be an error page

  Scenario: Budget Deals View All navigation
    When I click View All under Budget Deals
    Then the URL should contain "/categories/phones/products"
    And the page should not be an error page

  Scenario Outline: Footer links open the correct pages
    When I click the footer link "<link>"
    Then the URL should contain "<path>"
    And the page should not be an error page

    Examples:
      | link               | path              |
      | TechVan            | /about-techvan    |
      | Contact Us         | /contact-us       |
      | Terms & Conditions | /terms-conditions |
      | Privacy Policy     | /privacy-policy   |
      | Payment Methods    | /payment-methods  |
