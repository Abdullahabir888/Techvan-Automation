package com.techvan.pages;

import com.techvan.base.BasePage;
import com.techvan.utils.ConfigReader;
import com.techvan.utils.Xp;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Locale;

/**
 * Create-account page. The registration form is the form with a password field AND either two
 * password fields (confirm) or two+ other text inputs - so it is never confused with the login form.
 */
public class RegisterPage extends BasePage {

    private static final String L = Xp.lower("normalize-space()");
    private static final String FORM =
            "//form[.//input[@type='password']]" +
            "[count(.//input[@type='password'])>=2 or " +
            "count(.//input[not(@type='hidden') and not(@type='checkbox') and not(@type='radio') and not(@type='password')])>=2]";

    private final By form = By.xpath(FORM);
    private final By pw1 = By.xpath("(" + FORM + "//input[@type='password'])[1]");
    private final By pw2 = By.xpath("(" + FORM + "//input[@type='password'])[2]");
    private final By submit = By.xpath("(" + FORM + "//button[@type='submit' or contains(" + L + ",'register') " +
            "or contains(" + L + ",'sign up') or contains(" + L + ",'create')])[1]");
    private final By registerLink = By.xpath("//a[contains(" + L + ",'create account') or contains(" + L + ",'sign up') " +
            "or contains(" + L + ",'register') or contains(" + L + ",'create an account')]");

    public RegisterPage open() {

        String path = ConfigReader.get("registerPath", "");

        if (!path.isBlank()) {
            driver.get(ConfigReader.get("baseUrl").replaceAll("/+$", "") + path);
        } else {
            new LoginPage().open();
            click(registerLink);
        }

        visible(pw1);
        return this;
    }

    public boolean isFormDisplayed() {
        return isDisplayed(pw1) && isDisplayed(submit);
    }

    public boolean hasConfirmPasswordField() {
        return !driver.findElements(pw2).isEmpty();
    }

    public boolean isStillOnRegisterPage() {
        return isDisplayed(pw1);
    }

    public void fill(String email, String password, String confirm) {

        typeIfPresent(Xp.fieldLike("firstname", "first_name", "first name", "fullname", "full name", "username", "name"), "QA");
        typeIfPresent(Xp.fieldLike("lastname", "last_name", "last name", "surname", "family"), "Tester");
        typeIfPresent(Xp.fieldLike("phone", "mobile", "tel"), "01000000000");

        if (!typeIfPresent(Xp.fieldLike("email"), email)) {
            throw new AssertionError("No e-mail field found on the registration form");
        }

        type(pw1, password);

        if (hasConfirmPasswordField()) {
            type(pw2, confirm);
        }
    }

    public void submit() {
        click(submit);
    }

    public void submitEmpty() {
        click(submit);
    }

    /** A message is shown, or the browser's own validation is blocking the form. */
    public boolean isErrorOrValidationShown() {

        if (isDisplayed(By.xpath(Xp.ERROR_MESSAGE))) {
            return true;
        }

        List<WebElement> forms = driver.findElements(form);
        if (forms.isEmpty()) {
            return false;
        }

        Object valid = ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", forms.get(0));

        return Boolean.FALSE.equals(valid);
    }

    /** Form gone, or a success / verification message shown. */
    public boolean isRegistered() {
        try {
            return wait.until(d -> {
                if (d.findElements(pw1).isEmpty()) return true;
                String t = d.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT);
                return t.contains("success") || t.contains("verify") || t.contains("otp") || t.contains("welcome");
            });
        } catch (Exception e) {
            return false;
        }
    }
}
