package com.techvan.stepdefinitions;

import com.techvan.pages.CategoryPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.List;

public class CategorySteps {

    private CategoryPage category() { return new CategoryPage(); }

    @Given("I open the {string} category")
    public void openCategory(String slug) { category().openCategory(slug); }

    @Given("I open the shop page")
    public void openShop() { category().openShop(); }

    @Then("the listing should show at least {int} product")
    public void listingCount(int min) {
        int n = category().productCount();
        Assert.assertTrue(n >= min, "Found " + n + " products");
    }

    @Then("every product card should show a BDT price")
    public void everyCardHasPrice() {
        List<String> bad = category().cardsWithoutPrice();
        Assert.assertTrue(bad.isEmpty(), "Cards without a BDT price: " + bad);
    }

    @Then("every product price should be greater than zero")
    public void pricesPositive() {
        List<String> bad = category().invalidPrices();
        Assert.assertTrue(bad.isEmpty(), "Cards with a zero/unreadable price: " + bad);
    }

    @Then("every product card should have a loaded image")
    public void everyCardHasImage() {
        List<String> bad = category().cardsWithoutImage();
        Assert.assertTrue(bad.isEmpty(), "Cards with a missing/broken image: " + bad);
    }

    @Then("every product name should contain {string}")
    public void everyNameContains(String term) {
        List<String> bad = category().productsNotMatching(term);
        Assert.assertTrue(bad.isEmpty(), "Products that do not match '" + term + "': " + bad);
    }

    @When("I apply the {string} filter")
    public void applyFilter(String option) { category().applyFilter(option); }

    @When("I open the first product in the listing")
    public void openFirst() { category().openFirstProduct(); }
}
