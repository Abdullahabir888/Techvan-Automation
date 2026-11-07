package com.techvan.pages;

import com.techvan.base.BasePage;
import com.techvan.utils.Xp;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checkout = the step BEFORE payment. SAFETY: this class can never place an order or pay.
 * continueToPayment() only clicks Continue/Next/Proceed-to-payment buttons and refuses
 * anything that looks like Place Order / Pay / Confirm.
 */
public class CheckoutPage extends BasePage {

    private static final Pattern BDT = Pattern.compile("BDT\\s*([0-9][0-9,]*)");
    private static final Pattern SAFE_BUTTON = Pattern.compile("continue|next|proceed to payment|go to payment|save and continue");
    private static final Pattern FINAL_BUTTON = Pattern.compile("place order|pay now|\\bpay\\b|confirm|complete order|submit order|buy now");

    private final By paymentOptions = By.xpath(
            "//*[not(*) and not(ancestor::footer) and not(ancestor::header) and (" +
            Xp.textHas("cash on delivery", "bkash", "nagad", "sslcommerz", "online payment", "credit card", "debit card", "payment method") + ")]");

    public boolean isOnCheckout() {
        return driver.getCurrentUrl().toLowerCase(Locale.ROOT).contains("checkout");
    }

    public void waitForCheckout() {
        waitForUrlContains("checkout");
    }

    public boolean summaryContains(String productName) {
        return bodyText().toLowerCase(Locale.ROOT).contains(productName.toLowerCase(Locale.ROOT));
    }

    /** Largest BDT amount on the page (the order total is normally the largest). */
    public int totalAmount() {

        int max = -1;
        Matcher m = BDT.matcher(bodyText());

        while (m.find()) {
            max = Math.max(max, Integer.parseInt(m.group(1).replace(",", "")));
        }

        return max;
    }

    public boolean hasField(String... keywords) {
        try {
            return shortWait.until(d -> !d.findElements(Xp.fieldLike(keywords)).isEmpty());
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean paymentMethodsListed() {
        return isDisplayed(paymentOptions);
    }

    public void fillDeliveryDetails() {
        typeIfPresent(Xp.fieldLike("fullname", "full name", "firstname", "first name", "name"), "QA Test");
        typeIfPresent(Xp.fieldLike("phone", "mobile", "tel"), "01000000000");
        typeIfPresent(Xp.fieldLike("email"), "qa.test@example.com");
        typeIfPresent(Xp.fieldLike("address"), "QA test address, Dhaka (do not process)");
    }

    public void continueToPayment() {

        List<String> seen = new ArrayList<>();

        for (WebElement b : driver.findElements(By.xpath("//button[not(ancestor::header) and not(ancestor::footer)]"))) {

            String text = b.getText().trim().toLowerCase(Locale.ROOT);
            seen.add(text);

            if (text.isEmpty() || !b.isDisplayed()) continue;
            if (FINAL_BUTTON.matcher(text).find()) continue;

            if (SAFE_BUTTON.matcher(text).find()) {
                scrollTo(b);
                b.click();
                return;
            }
        }

        throw new AssertionError("No safe 'Continue' button found; refusing to click order/pay buttons. Buttons seen: " + seen);
    }
}
