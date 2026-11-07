package com.techvan.stepdefinitions;

import com.techvan.pages.CartPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class CartSteps {

    private final CartPage cart = new CartPage();
    private int totalBefore;

    @Given("I open the cart page")
    public void open() { cart.open(); }

    @Then("the cart should contain the product {string}")
    public void contains(String name) {
        Assert.assertTrue(cart.contains(name), "Cart does not contain: " + name);
    }

    @Then("the cart should show a BDT price")
    public void price() { Assert.assertTrue(cart.showsBdtPrice(), "No BDT price on the cart page"); }

    @Then("the cart quantity should be {string}")
    public void quantity(String qty) { Assert.assertEquals(cart.quantityValue(), qty); }

    @When("I note the cart total")
    public void noteTotal() { totalBefore = cart.total(); }

    @When("I increase the cart quantity")
    public void increase() { cart.increaseQuantity(); }

    @Then("the cart total should increase")
    public void totalIncreased() {
        new org.openqa.selenium.support.ui.WebDriverWait(com.techvan.base.DriverFactory.get(), java.time.Duration.ofSeconds(10))
                .ignoring(AssertionError.class, org.openqa.selenium.WebDriverException.class)
                .until(d -> cart.total() > totalBefore);
        Assert.assertTrue(cart.total() > totalBefore, "Total did not increase from " + totalBefore);
    }

    @When("I remove the item from the cart")
    public void remove() { cart.removeFirstItem(); }

    @Then("the cart should be empty")
    public void empty() { Assert.assertTrue(cart.waitUntilEmpty(), "Cart is not showing an empty state"); }

    @Then("the checkout button should be displayed")
    public void checkoutButton() { Assert.assertTrue(cart.isCheckoutButtonDisplayed(), "No checkout button on the cart page"); }

    @When("I proceed to checkout")
    public void proceed() { cart.proceedToCheckout(); }
}
