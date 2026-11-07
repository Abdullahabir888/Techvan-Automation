package com.techvan.stepdefinitions;

import com.techvan.base.DriverFactory;
import com.techvan.pages.StaticPage;
import io.cucumber.java.en.Then;
import org.testng.Assert;

import java.util.Arrays;

public class CommonSteps {

    @Then("the page title should contain {string}")
    public void titleContains(String text) {
        Assert.assertTrue(DriverFactory.get().getTitle().contains(text),
                "Title was: " + DriverFactory.get().getTitle());
    }

    @Then("the URL should contain {string}")
    public void urlContains(String part) {
        String url = DriverFactory.get().getCurrentUrl();
        Assert.assertTrue(url.contains(part), "URL was: " + url);
    }

    @Then("the URL should contain one of {string}")
    public void urlContainsOneOf(String csv) {
        String url = DriverFactory.get().getCurrentUrl().toLowerCase();
        boolean ok = Arrays.stream(csv.split(",")).anyMatch(p -> url.contains(p.trim()));
        Assert.assertTrue(ok, "URL was: " + url);
    }

    @Then("the URL should end with {string}")
    public void urlEndsWith(String end) {
        String url = DriverFactory.get().getCurrentUrl().replaceAll("/$", "");
        Assert.assertTrue(url.endsWith(end), "URL was: " + url);
    }

    @Then("the page should not be an error page")
    public void notErrorPage() {
        Assert.assertTrue(new StaticPage().isNotErrorPage(), "Error page shown");
    }

    @Then("the page should contain {string}")
    public void pageContains(String text) {
        Assert.assertTrue(new StaticPage().bodyContains(text), "Text not found: " + text);
    }
}
