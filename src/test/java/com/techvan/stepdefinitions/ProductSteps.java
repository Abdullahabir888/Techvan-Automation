package com.techvan.stepdefinitions;

import com.techvan.base.DriverFactory;
import com.techvan.pages.ProductPage;
import com.techvan.utils.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class ProductSteps {

    private ProductPage product() { return new ProductPage(); }
    private int cartBefore;

    @Given("I open the product {string}")
    public void openProduct(String path) {
        product().openByPath(path);
    }

    @Then("the product name should contain {string}")
    public void nameContains(String text) {
        Assert.assertTrue(product().getName().toUpperCase().contains(text.toUpperCase()),
                "Name was: " + product().getName());
    }

    @Then("the product price should be greater than {int}")
    public void priceGreaterThan(int min) {
        Assert.assertTrue(product().getPriceValue() > min, "Price was: " + product().getPrice());
    }

    @Then("the product page should show a name and price")
    public void nameAndPrice() {
        Assert.assertFalse(product().getName().isBlank(), "Name empty");
        Assert.assertTrue(product().getPrice().contains("BDT"), "Price was: " + product().getPrice());
    }
    

    @Then("the Add to Cart button should be displayed")
    public void addToCartShown() { Assert.assertTrue(product().isAddToCartShown()); }

    @Then("the Buy Now button should be displayed")
    public void buyNowShown() { Assert.assertTrue(product().isBuyNowShown()); }

    @Then("the Add to Compare button should be displayed")
    public void compareShown() { Assert.assertTrue(product().isCompareShown()); }

    @Then("the quantity should be {string}")
    public void quantity(String qty) { Assert.assertEquals(product().getQuantity(), qty); }

    @Then("breadcrumbs should be displayed")
    public void breadcrumbs() { Assert.assertTrue(product().breadcrumbCount() >= 1); }

    @Then("the {string} tab should be displayed")
    public void tabShown(String tab) { Assert.assertTrue(product().isTabDisplayed(tab), "Tab missing: " + tab); }

    @When("I open the {string} tab")
    public void openTab(String tab) { product().openTab(tab); }

    @When("I select the color {string}")
    public void selectColor(String color) { product().selectColor(color); }

    @When("I note the cart count")
    public void noteCart() {
        cartBefore = Math.max(product().getCartCount(), 0);
    }

    @When("I click Add to Cart")
    public void addToCart() { product().clickAddToCart(); }

    @Then("the cart count should increase")
    public void cartIncreased() {
        new WebDriverWait(DriverFactory.get(), Duration.ofSeconds(10))
                .until(d -> product().getCartCount() > cartBefore);
        Assert.assertTrue(product().getCartCount() > cartBefore);
    }

    @When("I click Add to Compare")
    public void clickCompare() { product().clickCompare(); }

    @When("I go to the compare page")
    public void goToCompare() {
        DriverFactory.get().get(ConfigReader.get("baseUrl") + "/compare");
    }
}
