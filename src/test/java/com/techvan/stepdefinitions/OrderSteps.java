package com.techvan.stepdefinitions;

import com.techvan.pages.OrderTrackingPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class OrderSteps {

    private final OrderTrackingPage orders = new OrderTrackingPage();

    @Given("I open the order tracking page")
    public void open() { orders.open(); }

    @Then("the order tracking form should be displayed")
    public void formDisplayed() {
        Assert.assertTrue(orders.isFormDisplayed(), "Order tracking field + button not displayed");
    }

    @When("I submit the order tracking form without an order number")
    public void submitEmpty() { orders.submitEmpty(); }

    @When("I track the order {string}")
    public void track(String number) { orders.track(number); }

    @Then("a validation or error message should be displayed on the tracking form")
    public void validation() {
        Assert.assertTrue(orders.isErrorOrValidationShown(), "No validation/error feedback on the tracking form");
    }

    @Then("the tracking result should say the order was not found")
    public void notFound() {
        Assert.assertTrue(orders.saysOrderNotFound(), "Unknown order number gave no 'not found' feedback");
    }

    @When("I open my orders page")
    public void openMyOrders() { orders.openMyOrders(); }
}
