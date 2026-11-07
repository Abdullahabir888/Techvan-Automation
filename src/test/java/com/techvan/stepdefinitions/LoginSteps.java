package com.techvan.stepdefinitions;

import com.techvan.pages.LoginPage;
import com.techvan.utils.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.testng.SkipException;

public class LoginSteps {

    private LoginPage login() { return new LoginPage(); }

    @Given("I open the login page")
    public void openLogin() { login().open(); }

    @Then("the login form should be displayed")
    public void formDisplayed() {
        Assert.assertTrue(login().isFormDisplayed(), "Login form (identity, password, submit) not fully displayed");
    }

    @When("I submit the login form without entering anything")
    public void submitEmpty() { login().submitEmpty(); }

    @When("I log in with email {string} and password {string}")
    public void loginWith(String email, String password) { login().login(email, password); }

    @When("I log in with the configured valid credentials")
    public void loginValid() {
        String email = ConfigReader.get("loginEmail");
        String pass = ConfigReader.get("loginPassword");
        if (email == null || email.isBlank() || pass == null || pass.isBlank()) {
            throw new SkipException("No credentials: pass -DloginEmail=... -DloginPassword=... "
                    + "(or env TECHVAN_LOGIN_EMAIL / TECHVAN_LOGIN_PASSWORD).");
        }
        login().login(email, pass);
    }

    @Then("I should still be on the login page")
    public void stillOnLogin() {
        Assert.assertTrue(login().isStillOnLoginPage(), "Login form disappeared - login unexpectedly succeeded");
    }

    @Then("a validation or error message should be displayed")
    public void errorShown() {
        Assert.assertTrue(login().isErrorOrValidationShown(), "No error message or validation feedback shown");
    }

    @Then("I should be logged in")
    public void loggedIn() {
        Assert.assertTrue(login().isLoggedIn(), "Still on the login form after submitting valid credentials");
    }
}
