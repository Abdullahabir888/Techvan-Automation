
package com.techvan.pages;

import com.techvan.base.BasePage;
import com.techvan.utils.ConfigReader;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomePage extends BasePage {


    private final By logo =
            By.cssSelector("a[href$='/en'] img[alt*='Techvan' i]");

    private final By orderTracking =
            By.xpath("//*[normalize-space()='Order Tracking']");

    private final By recentlyViewed =
            By.cssSelector("a[href$='/en/recently-viewed']");

    private final By shopLink =
            By.cssSelector("a[href$='/en/shop']");

    private final By allCategories =
            By.xpath("//*[normalize-space()='All Categories']");

    private final By compareIcon =
            By.cssSelector("a[href$='/en/compare']");

    private final By wishlistIcon =
            By.cssSelector("a[href$='/en/account/wishlist']");

    private final By profileIcon =
            By.cssSelector("a[href$='/en/account/profile']");


    private final By heroSlides =
            By.cssSelector(
                    "[class*='swiper'] img, [class*='slider'] img, " +
                    "[class*='carousel'] img, [class*='hero'] img, [class*='banner'] img"
            );

    private final By budgetViewAll =
            By.xpath(
                    "//*[self::h2 or self::h3 or self::h4 or self::h5]" +
                    "[normalize-space()='Budget Deals']" +
                    "/following::a[normalize-space()='View All'][1]"
            );

    /*
     * Product links are filtered by their visible BDT price.
     * Verify this locator against the actual product-card DOM.
     */
    private final By productCards =
            By.xpath(
                    "//a[contains(@href,'/shop/') and contains(.,'BDT')]"
            );


    private final By newsletterTitle =
            By.xpath("//h2[normalize-space()='Stay essential.']");

    private final By paymentLogos =
            By.cssSelector(
                    "img[src*='/paymentsMethods/'], " +
                    "img[alt*='paymentsMethods']"
            );


    // ================= PAGE ACTIONS =================

    public HomePage open() {

        driver.get(ConfigReader.get("baseUrl"));

        wait.until(
                ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(logo),
                        ExpectedConditions.visibilityOfElementLocated(budgetViewAll)
                )
        );

        return this;
    }


    public boolean isLogoDisplayed() {
        return isDisplayed(logo);
    }

    public boolean isOrderTrackingDisplayed() {
        return isDisplayed(orderTracking);
    }

    public boolean isAllCategoriesDisplayed() {
        return isDisplayed(allCategories);
    }

    public boolean isCompareIconDisplayed() {
        return isDisplayed(compareIcon);
    }

    public boolean isWishlistIconDisplayed() {
        return isDisplayed(wishlistIcon);
    }

    public boolean isProfileIconDisplayed() {
        return isDisplayed(profileIcon);
    }

    public boolean isSectionDisplayed(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Section name cannot be empty."
            );
        }

        String heading = xpathLiteral(name.trim());

        By section = By.xpath(
                "//*[self::h1 or self::h2 or self::h3 or " +
                "self::h4 or self::h5]" +
                "[normalize-space()=" + heading + "]"
        );

        scrollToBottom();
        return isDisplayed(section);
    }


    public boolean isNewsletterDisplayed() {
        scrollToBottom();
        return isDisplayed(newsletterTitle);
    }


    public int heroSlideCount(int min) {
        return count(heroSlides, min);
    }

    public int productCardCount(int min) {
        return count(productCards, min);
    }

    public int paymentLogoCount(int min) {
        scrollToBottom();
        return count(paymentLogos, min);
    }


    public void clickLogo() {
        click(logo);
    }

    public void clickShop() {
        click(shopLink);
        waitForUrlContains("/shop");
    }

    public void clickRecentlyViewed() {
        click(recentlyViewed);
        waitForUrlContains("/recently-viewed");
    }

    public void clickCompare() {
        click(compareIcon);
        waitForUrlContains("/compare");
    }

    public void clickWishlist() {
        click(wishlistIcon);
    }

    public void clickProfile() {
        click(profileIcon);
    }

    public void clickBudgetViewAll() {
        click(budgetViewAll);
        waitForUrlContains("/categories/");
    }


    public void clickBrandTab(String brand) {

        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException(
                    "Brand name cannot be empty."
            );
        }

        By brandTab = By.xpath(
                "//*[self::a or self::button or @role='tab']" +
                "[normalize-space()=" + xpathLiteral(brand.trim()) + "]"
        );

        click(brandTab);
    }


    public void clickFooterLink(String linkText) {

        if (linkText == null || linkText.isBlank()) {
            throw new IllegalArgumentException(
                    "Footer link cannot be empty."
            );
        }

        By footerLink = By.xpath(
                "(//a[normalize-space()=" +
                xpathLiteral(linkText.trim()) +
                "])[last()]"
        );

        click(footerLink);
    }

    /**
     * Opens a product matching the supplied name.
     */
    public void openProduct(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Product name cannot be empty."
            );
        }

        List<WebElement> cards = all(productCards);

        String expected = name.trim().toLowerCase(Locale.ROOT);

        for (WebElement card : cards) {

            String actual = card.getText()
                    .toLowerCase(Locale.ROOT);

            if (actual.contains(expected)) {

                wait.until(
                        ExpectedConditions.elementToBeClickable(card)
                ).click();

                waitForUrlContains("/shop/");

                return;
            }
        }

        throw new AssertionError(
                "Product not found on homepage: " + name
        );
    }


    /**
     * Opens the first available product.
     */
    public void openFirstProduct() {

        List<WebElement> cards = all(productCards);

        if (cards.isEmpty()) {
            throw new AssertionError(
                    "No products found on TechVan homepage."
            );
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(cards.get(0))
        ).click();

        waitForUrlContains("/shop/");
    }


    /**
     * Returns all visible product names and prices.
     */
    public List<String> getProductDetails() {

        List<String> products = new ArrayList<>();

        for (WebElement card : all(productCards)) {

            String details = card.getText().trim();

            if (!details.isEmpty()) {
                products.add(details);
            }
        }

        return products;
    }


    /**
     * Checks whether a product is marked Out of Stock.
     */
    public boolean isProductOutOfStock(String productName) {

        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException(
                    "Product name cannot be empty."
            );
        }

        String expected = productName.trim().toLowerCase(Locale.ROOT);

        for (WebElement card : all(productCards)) {

            String details = card.getText()
                    .toLowerCase(Locale.ROOT);

            if (details.contains(expected)) {
                return details.contains("out of stock");
            }
        }

        throw new AssertionError(
                "Product not found: " + productName
        );
    }

    /**
     * Safely creates an XPath string literal,
     * including values containing apostrophes.
     */
    private String xpathLiteral(String value) {

        if (!value.contains("'")) {
            return "'" + value + "'";
        }

        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }

        String[] parts = value.split("'", -1);

        StringBuilder result = new StringBuilder("concat(");

        for (int i = 0; i < parts.length; i++) {

            if (i > 0) {
                result.append(", \"'\", ");
            }

            result.append("'").append(parts[i]).append("'");
        }

        result.append(")");

        return result.toString();
    }
}
