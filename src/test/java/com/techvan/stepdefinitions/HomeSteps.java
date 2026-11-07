package com.techvan.stepdefinitions;

import com.techvan.pages.HomePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class HomeSteps {

    private HomePage home() { return new HomePage(); }

    @Given("I am on the TechVan home page")
    public void openHome() {
        home().open();
    }

    @Then("the TechVan logo should be displayed")
    public void logoDisplayed() {
        Assert.assertTrue(home().isLogoDisplayed());
    }

    @Then("the header should show Order Tracking")
    public void orderTracking() {
        Assert.assertTrue(home().isOrderTrackingDisplayed());
    }

    @Then("the header should show All Categories")
    public void allCategories() {
        Assert.assertTrue(home().isAllCategoriesDisplayed());
    }

    @Then("the header should show compare, wishlist and profile icons")
    public void headerIcons() {
        Assert.assertTrue(home().isCompareIconDisplayed(), "Compare icon missing");
        Assert.assertTrue(home().isWishlistIconDisplayed(), "Wishlist icon missing");
        Assert.assertTrue(home().isProfileIconDisplayed(), "Profile icon missing");
    }

    @Then("the hero slider should have at least {int} banner")
    public void heroBanners(int min) {
        Assert.assertTrue(home().heroSlideCount(min) >= min, "Hero slider banners not found (check locator)");
    }

    @Then("the {string} section should be displayed")
    public void sectionDisplayed(String name) {
        Assert.assertTrue(home().isSectionDisplayed(name), "Section missing: " + name);
    }

    @Then("at least {int} product cards should be displayed")
    public void productCards(int min) {
        int n = home().productCardCount(min);
        Assert.assertTrue(n >= min, "Found " + n + " product cards");
    }

    @Then("at least {int} payment method logos should be displayed")
    public void paymentLogos(int min) {
        int n = home().paymentLogoCount(min);
        Assert.assertTrue(n >= min, "Found " + n + " payment logos");
    }

    @When("I click the TechVan logo")
    public void clickLogo() { home().clickLogo(); }

    @When("I click the Shop link")
    public void clickShop() { home().clickShop(); }

    @When("I click the Recently Viewed link")
    public void clickRecentlyViewed() { home().clickRecentlyViewed(); }

    @When("I click the compare icon")
    public void clickCompare() { home().clickCompare(); }

    @When("I click the wishlist icon")
    public void clickWishlist() { home().clickWishlist(); }

    @When("I click View All under Budget Deals")
    public void clickBudgetViewAll() { home().clickBudgetViewAll(); }

    @When("I click the footer link {string}")
    public void clickFooter(String link) { home().clickFooterLink(link); }

    @When("I open the product {string} from the home page")
    public void openProductFromHome(String name) { home().openProduct(name); }
}
