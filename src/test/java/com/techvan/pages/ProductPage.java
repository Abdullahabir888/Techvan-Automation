package com.techvan.pages;

import com.techvan.base.BasePage;
import com.techvan.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductPage extends BasePage {

    private final By productName   = By.tagName("h1");
    // Prefer the first price that appears AFTER the product title (avoids header/related-product prices)
    private final By priceAfterTitle = By.xpath("(//h1/following::*[contains(normalize-space(),'BDT') and not(*)])[1]");
    private final By priceAnywhere   = By.xpath("(//*[contains(normalize-space(),'BDT') and not(*)])[1]");
    private final By stockStatus   = By.xpath("//*[contains(translate(normalize-space(),'instock','INSTOCK'),'IN STOCK') and not(*)]");
    private final By outOfStock    = By.xpath("//*[normalize-space()='Out of Stock' or normalize-space()='OUT OF STOCK']");
    private final By addToCart     = By.xpath("//button[normalize-space()='Add to Cart']");
    private final By buyNow        = By.xpath("//button[normalize-space()='Buy Now']");
    private final By addToCompare  = By.xpath("//*[self::button or self::a][normalize-space()='Add to Compare']");
    private final By writeReview   = By.xpath("//*[self::button or self::a][normalize-space()='Write Review']");
    private final By quantityInput = By.cssSelector("input[type='number'], input[name*='qty' i], input[name*='quantity' i]");
    private final By breadcrumbs   = By.xpath("//nav[contains(@aria-label,'readcrumb')]//a | //ol//a[contains(@href,'/en/shop') or contains(@href,'/categories/')]");
    private final By cartBadge     = By.xpath("(//*[contains(@class,'cart') or contains(@class,'badge')]//*[string-length(normalize-space())<=3 and number(normalize-space())=number(normalize-space())])[1]");

    // "Avail Offer" pop-up that asks how the user wants to get the offer
    private final By offerTitle = By.xpath("//*[normalize-space()='Avail Offer']");

    private static final Pattern FIRST_NUMBER = Pattern.compile("(\\d[\\d,]*)");

    public ProductPage openByPath(String path) {
        String base = ConfigReader.get("baseUrl").replaceAll("/en/?$", "");
        driver.get(base + path);
        visible(productName);
        return this;
    }

    public String getName()            { return text(productName); }
    public boolean isInStock()         { return isDisplayed(stockStatus); }
    public boolean isOutOfStock()      { return isDisplayed(outOfStock); }
    public boolean isAddToCartShown()  { return isDisplayed(addToCart); }
    public boolean isBuyNowShown()     { return isDisplayed(buyNow); }
    public boolean isCompareShown()    { return isDisplayed(addToCompare); }
    public boolean isWriteReviewShown(){ return isDisplayed(writeReview); }

    public int breadcrumbCount() {
        return count(breadcrumbs, 1);
    }

    public String getPrice() {
        wait.until(d -> !d.findElements(priceAfterTitle).isEmpty()
                || !d.findElements(priceAnywhere).isEmpty());

        List<WebElement> els = driver.findElements(priceAfterTitle);
        if (els.isEmpty()) {
            els = driver.findElements(priceAnywhere);
        }
        return els.get(0).getText().trim();
    }

    public String getQuantity() {
        return visible(quantityInput).getAttribute("value");
    }

    public void selectColor(String color) {
        click(By.xpath(String.format("//*[normalize-space()='%s']", color)));
    }

    public void openTab(String name) {
        click(By.xpath(String.format("//*[self::button or self::li or self::a][normalize-space()='%s']", name)));
    }

    public boolean isTabDisplayed(String name) {
        return isDisplayed(By.xpath(String.format("//*[self::button or self::li or self::a][normalize-space()='%s']", name)));
    }

    public void clickAddToCart() { click(addToCart); handleOfferPopup(); }
    public void clickBuyNow()    { click(buyNow); handleOfferPopup(); }

    /**
     * If the "Avail Offer" pop-up appears, pick one of its three options and return true.
     * Choose with -DofferOption=existing | new | another (default: another).
     */
    public boolean handleOfferPopup() {

        if (!isDisplayed(offerTitle)) {
            return false;
        }

        String choice = ConfigReader.get("offerOption", "another").toLowerCase();

        String label = switch (choice) {
            case "existing" -> "I already have a banglalink Connection";
            case "new"      -> "I want a new banglalink Connection";
            default         -> "I have another SIM";
        };

        By option = By.xpath("//*[normalize-space()='" + label + "']");

        click(option);
        return true;
    }
    public void clickCompare()   { click(addToCompare); }

    /** Reads the header cart counter without waiting. Returns -1 if not found. */
    public int getCartCount() {
        try {
            List<WebElement> els = driver.findElements(cartBadge);
            return els.isEmpty() ? -1 : Integer.parseInt(els.get(0).getText().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    /** "BDT 16,999" -> 16999. Uses only the FIRST number, so "BDT 20,000 BDT 18,000" no longer becomes one huge value. */
    public int getPriceValue() {
        Matcher m = FIRST_NUMBER.matcher(getPrice());
        if (!m.find()) {
            throw new AssertionError("No number found in price: " + getPrice());
        }
        return Integer.parseInt(m.group(1).replace(",", ""));
    }
}
