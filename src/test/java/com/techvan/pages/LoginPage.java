package com.techvan.pages;

import com.techvan.base.BasePage;
import com.techvan.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Login page. Locators are anchored on "the form that contains a password field", so the
 * newsletter form in the footer can never be mistaken for the login form.
 * Reached via -DloginPath (if set) or by clicking the profile icon as a guest.
 */
public class LoginPage extends BasePage {

    private static final String LOWER =
            "translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
    private static final String FORM = "//form[.//input[@type='password']]";

    private final By profileIcon = By.cssSelector("a[href$='/en/account/profile']");
    private final By identity = By.xpath(
            "(" + FORM + "//input[not(@type='password') and not(@type='hidden') " +
            "and not(@type='checkbox') and not(@type='radio')])[1]");
    private final By password = By.xpath(FORM + "//input[@type='password']");
    private final By submit = By.xpath(
            "(" + FORM + "//button[@type='submit' or contains(" + LOWER + ",'log in') " +
            "or contains(" + LOWER + ",'login') or contains(" + LOWER + ",'sign in')])[1]");
    private final By errorMessage = By.xpath(
            "//*[@role='alert' or contains(@class,'error') or contains(@class,'toast') " +
            "or contains(@class,'Toastify') or contains(@class,'invalid')][string-length(normalize-space())>0]");

    public LoginPage open() {

        String path = ConfigReader.get("loginPath", "");
        String base = ConfigReader.get("baseUrl").replaceAll("/+$", "");

        if (!path.isBlank()) {
            driver.get(base + path);
        } else {
            driver.get(base);
            click(profileIcon);
        }

        visible(password);
        return this;
    }

    public boolean isFormDisplayed() {
        return isDisplayed(identity) && isDisplayed(password) && isDisplayed(submit);
    }

    public void submitEmpty() {
        click(submit);
    }

    public void login(String user, String pass) {
        type(identity, user);
        type(password, pass);
        click(submit);
    }

    public boolean isStillOnLoginPage() {
        return isDisplayed(password);
    }

    /** True when a message is shown OR the browser's own validation is blocking the form. */
    public boolean isErrorOrValidationShown() {

        if (isDisplayed(errorMessage)) {
            return true;
        }

        List<WebElement> forms = driver.findElements(By.xpath(FORM));
        if (forms.isEmpty()) {
            return false;
        }

        Object valid = ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", forms.get(0));

        return Boolean.FALSE.equals(valid);
    }

    /** After a successful login the password field must disappear. */
    public boolean isLoggedIn() {
        try {
            return wait.until(d -> d.findElements(password).isEmpty());
        } catch (Exception e) {
            return false;
        }
    }
}
