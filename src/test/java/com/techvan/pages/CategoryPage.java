package com.techvan.pages;

import com.techvan.base.BasePage;
import com.techvan.utils.ConfigReader;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class CategoryPage extends BasePage {

    /*
     * A product card = a link to /shop/<slug> that contains an image and is NOT part of the
     * header, footer or nav menus. If this is wrong for the real DOM, set productCardXpath in
     * config.properties (or -DproductCardXpath=...) instead of editing code.
     */
    private static final String DEFAULT_CARD_XPATH =
            "//a[contains(@href,'/shop/') and .//img" +
            " and not(ancestor::header) and not(ancestor::footer) and not(ancestor::nav)]";

    private final By productCards = By.xpath(
            ConfigReader.get("productCardXpath", DEFAULT_CARD_XPATH));

    private final By cardContainer = By.xpath(
            "./ancestor::*[self::li or self::article or contains(@class,'card') or contains(@class,'product')][1]");

    private static final Pattern BDT_PRICE = Pattern.compile("(?s).*BDT\\s*[0-9,]+(?:\\.[0-9]+)?.*");
    private static final Pattern PRICE_NUMBER = Pattern.compile("BDT\\s*([0-9][0-9,]*)");


    // ================= NAVIGATION =================

    public CategoryPage openCategory(String slug) {

        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("Category slug cannot be empty.");
        }

        driver.get(buildUrl("/categories/" + slug.trim() + "/products"));
        waitForProducts();
        return this;
    }

    public CategoryPage openShop() {

        driver.get(buildUrl("/shop"));
        waitForProducts();
        return this;
    }

    private String buildUrl(String path) {

        String baseUrl = ConfigReader.get("baseUrl");

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("baseUrl is missing in config.properties.");
        }

        return baseUrl.replaceAll("/+$", "") + path;
    }

    private void waitForProducts() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productCards));
    }


    // ================= CARDS =================

    /** Distinct product cards (de-duplicated by link, so image+title links to one product count once). */
    private List<WebElement> cards() {

        Map<String, WebElement> unique = new LinkedHashMap<>();

        for (WebElement card : driver.findElements(productCards)) {
            String href = card.getAttribute("href");
            if (href != null) {
                unique.putIfAbsent(href, card);
            }
        }

        return new ArrayList<>(unique.values());
    }

    /** Text of the card; if the price is outside the link, fall back to the surrounding card container. */
    private String cardText(WebElement card) {

        String text = card.getText().trim();

        if (BDT_PRICE.matcher(text).matches()) {
            return text;
        }

        try {
            return card.findElement(cardContainer).getText().trim();
        } catch (Exception e) {
            return text;
        }
    }

    private String nameOf(WebElement card) {

        for (String line : cardText(card).split("\\R")) {
            String t = line.trim();
            if (!t.isEmpty() && !t.toUpperCase(Locale.ROOT).contains("BDT")) {
                return t;
            }
        }

        String title = card.getAttribute("title");
        return title == null ? "" : title.trim();
    }


    // ================= READ =================

    public int productCount() {
        return cards().size();
    }

    public List<String> productNames() {

        List<String> names = new ArrayList<>();

        for (WebElement card : cards()) {
            String n = nameOf(card);
            if (!n.isEmpty()) {
                names.add(n);
            }
        }

        return names;
    }

    /** Names of cards that show no BDT price (empty list = all good). */
    public List<String> cardsWithoutPrice() {

        List<String> bad = new ArrayList<>();

        for (WebElement card : cards()) {
            if (!BDT_PRICE.matcher(cardText(card)).matches()) {
                bad.add(describe(card));
            }
        }

        return bad;
    }

    /** Cards whose image is missing, broken (naturalWidth == 0) or not loaded. */
    public List<String> cardsWithoutImage() {

        List<String> bad = new ArrayList<>();

        for (WebElement card : cards()) {

            scrollTo(card); // lazy-loaded images only load when visible

            boolean ok = card.findElements(By.tagName("img")).stream().anyMatch(img -> {
                try {
                    String src = img.getAttribute("src");
                    Object w = ((org.openqa.selenium.JavascriptExecutor) driver)
                            .executeScript("return arguments[0].naturalWidth;", img);
                    return src != null && !src.isBlank() && w instanceof Number n && n.intValue() > 0;
                } catch (Exception e) {
                    return false;
                }
            });

            if (!ok) {
                bad.add(describe(card));
            }
        }

        return bad;
    }

    /** Names that do NOT contain the term (case-insensitive). Empty list = every product matches. */
    public List<String> productsNotMatching(String term) {

        String expected = term.toLowerCase(Locale.ROOT);
        List<String> bad = new ArrayList<>();

        for (WebElement card : cards()) {
            String name = nameOf(card);
            if (!name.toLowerCase(Locale.ROOT).contains(expected)
                    && !cardText(card).toLowerCase(Locale.ROOT).contains(expected)) {
                bad.add(name.isEmpty() ? describe(card) : name);
            }
        }

        return bad;
    }

    /** Prices that are zero or unreadable. */
    public List<String> invalidPrices() {

        List<String> bad = new ArrayList<>();

        for (WebElement card : cards()) {
            var m = PRICE_NUMBER.matcher(cardText(card));
            if (!m.find() || Integer.parseInt(m.group(1).replace(",", "")) <= 0) {
                bad.add(describe(card));
            }
        }

        return bad;
    }

    private String describe(WebElement card) {
        String name = nameOf(card);
        return name.isEmpty() ? String.valueOf(card.getAttribute("href")) : name;
    }

    public boolean isProductAvailable(String expectedName) {

        if (expectedName == null || expectedName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }

        String expected = expectedName.toLowerCase(Locale.ROOT);

        return productNames().stream()
                .anyMatch(n -> n.toLowerCase(Locale.ROOT).contains(expected));
    }


    // ================= FILTER =================

    /**
     * Clicks a filter option by its visible text (checkbox label, chip, button or link) and waits
     * for the listing or URL to change. Generic on purpose: send me the filter panel HTML and I
     * will tighten it.
     */
    public void applyFilter(String option) {

        if (option == null || option.isBlank()) {
            throw new IllegalArgumentException("Filter option cannot be empty.");
        }

        List<String> before = productNames();
        String urlBefore = driver.getCurrentUrl();

        String text = option.trim();

        By filter = By.xpath(
                "//*[self::label or self::button or self::a or self::span or self::li or @role='checkbox' or @role='option']" +
                "[normalize-space()=" + literal(text) + " and not(ancestor::header) and not(ancestor::footer)]"
        );

        click(filter);

        try {
            shortWait.until(d -> !d.getCurrentUrl().equals(urlBefore) || !productNames().equals(before));
        } catch (TimeoutException ignored) {
            // the filter may legitimately leave the list unchanged; the next assertions decide
        }

        waitForProducts();
    }

    private String literal(String v) {
        if (!v.contains("'")) return "'" + v + "'";
        return "\"" + v + "\"";
    }


    // ================= PRODUCT NAVIGATION =================

    public void openFirstProduct() {

        List<WebElement> cards = cards();

        if (cards.isEmpty()) {
            throw new AssertionError("No products found on the current listing page.");
        }

        WebElement first = cards.get(0);

        scrollTo(first);
        wait.until(ExpectedConditions.elementToBeClickable(first)).click();
        waitForUrlContains("/shop/");
    }
}
