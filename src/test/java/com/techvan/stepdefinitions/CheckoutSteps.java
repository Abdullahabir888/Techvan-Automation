package com.techvan.stepdefinitions;

import com.techvan.pages.CartPage;
import com.techvan.pages.CheckoutPage;
import com.techvan.pages.LoginPage;
import com.techvan.utils.ConfigReader;
import com.techvan.utils.Gate;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.testng.SkipException;

/** Checkout steps stop BEFORE payment: there is deliberately no step that places an order or pays. */
public class CheckoutSteps {

    private CheckoutPage checkout() { return new CheckoutPage(); }

    @When("I sign in if the site asks me to")
    public void signInIfAsked() {
        LoginPage login = new LoginPage();

        if (!login.isStillOnLoginPage()) {
            return;
        }

        String email = ConfigReader.get("loginEmail");
        String pass = ConfigReader.get("loginPassword");

        if (email == null || email.isBlank() || pass == null || pass.isBlank()) {
            throw new SkipException("Site requires login for checkout; pass -DloginEmail=... -DloginPassword=...");
        }

        login.login(email, pass);
        Assert.assertTrue(login.isLoggedIn(), "Login failed while heading to checkout");
    }

    @Then("the checkout page should be displayed")
    public void checkoutDisplayed() {
        if (!checkout().isOnCheckout()) {
            // e.g. login redirected us elsewhere: go back through the cart once
            new CartPage().open().proceedToCheckout();
        }
        checkout().waitForCheckout();
    }

    @Then("the order summary should list {string}")
    public void summaryLists(String name) {
        Assert.assertTrue(checkout().summaryContains(name), "Order summary does not list: " + name);
    }

    @Then("the checkout total should be a BDT amount")
    public void totalIsAmount() {
        Assert.assertTrue(checkout().totalAmount() > 0, "No BDT total found on the checkout page");
    }

    @Then("the checkout form should ask for name, phone and address")
    public void formFields() {
        CheckoutPage c = checkout();
        Assert.assertTrue(c.hasField("name"), "No name field");
        Assert.assertTrue(c.hasField("phone", "mobile", "tel"), "No phone field");
        Assert.assertTrue(c.hasField("address"), "No address field");
    }

    @Then("payment methods should be listed")
    public void paymentsListed() {
        Assert.assertTrue(checkout().paymentMethodsListed(), "No payment methods listed on the checkout page");
    }

    @When("I fill the delivery details with test data")
    public void fillDetails() {
        Gate.require("allowCheckoutProgress", "fills checkout with fake data");
        checkout().fillDeliveryDetails();
    }

    @When("I continue to the payment step")
    public void continueToPayment() {
        Gate.require("allowCheckoutProgress", "advances the checkout");
        checkout().continueToPayment();
    }

    @Then("the payment step should be displayed")
    public void paymentStep() {
        Assert.assertTrue(checkout().paymentMethodsListed(), "Payment step not reached");
    }
}
