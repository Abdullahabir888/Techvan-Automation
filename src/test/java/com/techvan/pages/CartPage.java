package com.techvan.pages;

import com.techvan.base.BasePage;
import com.techvan.utils.ConfigReader;
import com.techvan.utils.Xp;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CartPage extends BasePage {

    private static final Pattern BDT = Pattern.compile("BDT\\s*([0-9][0-9,]*)");
    private static final String L = Xp.lower("normalize-space()");

    private final By quantity = By.cssSelector("input[type='number'], input[name*='qty' i], input[name*='quantity' i]");
    private final By plus = By.xpath("//button[normalize-space()='+' or contains(@aria-label,'ncrease') " +
            "or contains(@aria-label,'lus') or contains(@title,'ncrease')]");
    private final By remove = By.xpath("//button[contains(@aria-label,'emove') or contains(@aria-label,'elete') " +
            "or contains(@title,'emove') or contains(@title,'elete') or contains(normalize-space(),'Remove') " +
            "or contains(normalize-space(),'Delete')]");
    private final By confirmDialog = By.xpath("//*[@role='dialog' or @role='alertdialog']//button[" +
            "contains(" + L + ",'yes') or contains(" + L + ",'confirm') or contains(" + L + ",'remove') or contains(" + L + ",'ok')]");
    private final By checkout = By.xpath("//*[self::a or self::button][not(ancestor::header) and (" +
            "contains(" + L + ",'checkout') or contains(" + L + ",'check out') or contains(" + L + ",'proceed'))]");
    private final By totalLabel = By.xpath("//*[not(*) and (starts-with(" + L + ",'total') or starts-with(" + L + ",'grand total') " +
            "or starts-with(" + L + ",'subtotal') or starts-with(" + L + ",'sub total') or starts-with(" + L + ",'order total'))]");

    public CartPage open() {

        driver.get(ConfigReader.get("baseUrl").replaceAll("/+$", "") + ConfigReader.get("cartPath", "/cart"));

        try {
            wait.until(d -> {
                String t = d.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT);
                return t.contains("bdt") || t.contains("empty");
            });
        } catch (TimeoutException ignored) {
            // an assertion in the scenario will report what the page actually shows
        }

        return this;
    }

    public boolean contains(String productName) {
        return bodyText().toLowerCase(Locale.ROOT).contains(productName.toLowerCase(Locale.ROOT));
    }

    public boolean showsBdtPrice() {
        return BDT.matcher(bodyText()).find();
    }

    public String quantityValue() {
        return visible(quantity).getAttribute("value");
    }

    public void increaseQuantity() {
        click(plus);
    }

    public void removeFirstItem() {
        click(remove);

        // some shops ask "Are you sure?"
        List<WebElement> dialog = driver.findElements(confirmDialog);
        if (!dialog.isEmpty() && dialog.get(0).isDisplayed()) {
            dialog.get(0).click();
        }
    }

    public boolean waitUntilEmpty() {
        try {
            return wait.until(d -> isEmptyNow());
        } catch (TimeoutException e) {
            return false;
        }
    }

    private boolean isEmptyNow() {
        String t = driver.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT);
        return t.contains("empty") || t.contains("no items") || t.contains("no products");
    }

    public boolean isCheckoutButtonDisplayed() {
        return isDisplayed(checkout);
    }

    public void proceedToCheckout() {
        click(checkout);
    }

    /** The grand total: last "Total" row on the page that carries a BDT amount. */
    public int total() {

        List<WebElement> labels = driver.findElements(totalLabel);

        for (int i = labels.size() - 1; i >= 0; i--) {
            String row = labels.get(i)
                    .findElement(By.xpath("./ancestor-or-self::*[contains(.,'BDT')][1]"))
                    .getText();

            int last = -1;
            Matcher m = BDT.matcher(row);
            while (m.find()) {
                last = Integer.parseInt(m.group(1).replace(",", ""));
            }
            if (last >= 0) {
                return last;
            }
        }

        throw new AssertionError("Could not find a 'Total' amount in BDT on the cart page");
    }
}
