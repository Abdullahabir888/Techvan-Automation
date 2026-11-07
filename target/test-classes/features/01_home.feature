
@home @smoke
Feature: TechVan Homepage

  As a shopper
  I want the TechVan homepage to load correctly
  So that I can browse products and explore available services

  Background:
    Given I am on the TechVan home page


  @smoke
  Scenario: Verify homepage title and logo
    Then the page title should contain "TechVan"
    And the TechVan logo should be displayed


  @smoke
  Scenario: Verify homepage header elements
    Then the header should show Order Tracking
    And the header should show All Categories
    And the header should show compare, wishlist and profile icons


  @smoke
  Scenario: Verify hero slider banners
    Then the hero slider should have at least 1 banner


  @smoke
  Scenario Outline: Verify homepage sections
    Then the "<section>" section should be displayed

    Examples:
      | section         |
      | Budget Deals    |
      | Brand Deals     |
      | Stay essential. |


  @smoke
  Scenario: Verify homepage product listings
    Then at least 5 product cards should be displayed


  @regression
  Scenario: Verify payment methods in footer
    Then at least 5 payment method logos should be displayed


  @navigation
  Scenario: Navigate from shop back to homepage
    When I click the Shop link
    And I click the TechVan logo
    Then the URL should end with "/en"
