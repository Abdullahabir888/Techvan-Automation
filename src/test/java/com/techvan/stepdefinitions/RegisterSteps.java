package com.techvan.stepdefinitions;

import com.techvan.pages.RegisterPage;
import com.techvan.utils.Gate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.testng.SkipException;

public class RegisterSteps {

    private final RegisterPage register = new RegisterPage();

    @Given("I open the registration page")
    public void open() { register.open(); }

    @Then("the registration form should be displayed")
    public void formDisplayed() {
        Assert.assertTrue(register.isFormDisplayed(), "Registration form (password + submit) not displayed");
    }

    @When("I submit the registration form without entering anything")
    public void submitEmpty() { register.submitEmpty(); }

    @When("I fill the registration form with email {string} and password {string} and confirm password {string}")
    public void fill(String email, String password, String confirm) {
        if (!register.hasConfirmPasswordField() && !password.equals(confirm)) {
            throw new SkipException("Form has no confirm-password field, so a mismatch cannot be tested");
        }
        register.fill(email, password, confirm);
    }

    @When("I submit the registration form")
    public void submit() { register.submit(); }

    @Then("I should still be on the registration page")
    public void stillOnRegister() {
        Assert.assertTrue(register.isStillOnRegisterPage(), "Registration form disappeared - it was unexpectedly accepted");
    }

    @When("I register a new unique account")
    public void registerUnique() {
        Gate.require("allowRegistration", "creates a REAL account on the live site");
        String email = "qa.test+" + System.currentTimeMillis() + "@example.com";
        register.fill(email, "Test@12345Aa", "Test@12345Aa");
        register.submit();
    }

    @Then("the account should be created")
    public void created() {
        Assert.assertTrue(register.isRegistered(), "No success/verification state after registering");
    }
}
