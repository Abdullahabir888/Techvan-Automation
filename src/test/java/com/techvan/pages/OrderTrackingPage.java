package com.techvan.pages;

import com.techvan.base.BasePage;
import com.techvan.utils.ConfigReader;
import com.techvan.utils.Xp;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Locale;

/** "Order Tracking" page (header link) and the logged-in "My orders" page. */
public class OrderTrackingPage extends BasePage {

    private static final String L = Xp.lower("normalize-space()");

    private final By headerLink = By.xpath("//a[.//*[normalize-space()='Order Tracking'] or normalize-space()='Order Tracking']");
    private final By orderField = Xp.fieldLike("order", "tracking", "invoice", "phone", "email");
    private final By trackButton = By.xpath("(//button[not(ancestor::header) and not(ancestor::footer) and " +
            "(@type='submit' or contains(" + L + ",'track') or contains(" + L + ",'search') or contains(" + L + ",'find'))])[1]");

    public OrderTrackingPage open() {

        String path = ConfigReader.get("trackingPath", "");
        String base = ConfigReader.get("baseUrl").replaceAll("/+$", "");

        if (!path.isBlank()) {
            driver.get(base + path);
        } else {
            driver.get(base);
            click(headerLink);
        }

        visible(orderField);
        return this;
    }

    public void openMyOrders() {
        driver.get(ConfigReader.get("baseUrl").replaceAll("/+$", "") + ConfigReader.get("ordersPath", "/account/orders"));
        visible(By.tagName("body"));
    }

    public boolean isFormDisplayed() {
        return isDisplayed(orderField) && isDisplayed(trackButton);
    }

    public void submitEmpty() {
        click(trackButton);
    }

    public void track(String orderNumber) {
        type(orderField, orderNumber);
        click(trackButton);
    }

    public boolean isErrorOrValidationShown() {

        if (isDisplayed(By.xpath(Xp.ERROR_MESSAGE))) {
            return true;
        }

        List<WebElement> fields = driver.findElements(orderField);
        if (fields.isEmpty()) {
            return false;
        }

        Object valid = ((JavascriptExecutor) driver).executeScript(
                "var f = arguments[0].form; return f ? f.checkValidity() : true;", fields.get(0));

        return Boolean.FALSE.equals(valid);
    }

    public boolean saysOrderNotFound() {
        try {
            return shortWait.until(d -> {
                String t = d.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT);
                return t.contains("not found") || t.contains("no order") || t.contains("invalid")
                        || t.contains("could not") || t.contains("does not exist") || t.contains("doesn't exist");
            });
        } catch (Exception e) {
            return isDisplayed(By.xpath(Xp.ERROR_MESSAGE));
        }
    }
}
