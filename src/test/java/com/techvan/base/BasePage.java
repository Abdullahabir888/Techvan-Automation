
package com.techvan.base;

import com.techvan.utils.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * BasePage
 * Shared Selenium actions and explicit wait methods.
 * All page classes should extend this class.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    /** Short wait for 'is it there?' checks so negative assertions fail fast. */
    protected final WebDriverWait shortWait;

    protected BasePage() {

        this.driver = Objects.requireNonNull(
                DriverFactory.get(),
                "WebDriver is not initialized. Initialize DriverFactory first."
        );

        int timeout = ConfigReader.getInt("explicitWait");

        if (timeout <= 0) {
            throw new IllegalArgumentException(
                    "explicitWait must be greater than zero."
            );
        }

        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(timeout)
        );

        this.wait.ignoring(StaleElementReferenceException.class);

        this.shortWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(Math.max(1, ConfigReader.getInt("shortWait", 5)))
        );
        this.shortWait.ignoring(StaleElementReferenceException.class);
    }

    // Wait until element is visible
    protected WebElement visible(By by) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(by)
        );
    }

    // Wait until element is clickable
    protected WebElement clickable(By by) {
        return wait.until(
                ExpectedConditions.elementToBeClickable(by)
        );
    }

    // Click element
    protected void click(By by) {
        WebElement element = clickable(by);

        scrollTo(element);

        // Use normal Selenium click to preserve real user interaction.
        // If intercepted, investigate the overlay or page state.
        clickable(by).click();
    }

    // Clear and enter text
    protected void type(By by, String value) {

        if (value == null) {
            throw new IllegalArgumentException(
                    "Input value cannot be null."
            );
        }

        WebElement element = visible(by);

        element.clear();
        element.sendKeys(value);
    }

    // Get visible element text
    protected String text(By by) {
        return visible(by).getText().trim();
    }

    // Check whether element is displayed (fails fast using the short wait)
    protected boolean isDisplayed(By by) {

        try {
            return shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(by)
            ).isDisplayed();

        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    // Count elements, waiting briefly until at least 'atLeast' exist (returns the real count either way)
    protected int count(By by, int atLeast) {

        try {
            shortWait.until(d -> d.findElements(by).size() >= atLeast);
        } catch (TimeoutException ignored) {
            // fall through and report the real number
        }

        return driver.findElements(by).size();
    }

    // Type into the first displayed matching field; returns false if none exists
    protected boolean typeIfPresent(By by, String value) {

        for (WebElement e : driver.findElements(by)) {
            try {
                if (e.isDisplayed()) {
                    e.clear();
                    e.sendKeys(value);
                    return true;
                }
            } catch (StaleElementReferenceException ignored) {
                // try the next match
            }
        }

        return false;
    }

    // Visible text of the whole page body
    protected String bodyText() {
        return visible(By.tagName("body")).getText();
    }

    // Scroll to the bottom so lazy-loaded sections render
    protected void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);"
        );
    }

    // Get all matching elements
    protected List<WebElement> all(By by) {

        return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(by)
        );
    }

    // Scroll element into view
    protected void scrollTo(WebElement element) {

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});",
                element
        );
    }

    // Wait until URL contains expected text
    protected void waitForUrlContains(String expectedPart) {

        if (expectedPart == null || expectedPart.isBlank()) {
            throw new IllegalArgumentException(
                    "Expected URL part cannot be empty."
            );
        }

        wait.until(
                ExpectedConditions.urlContains(expectedPart)
        );
    }

    // Wait for page title
    protected void waitForTitle(String expectedTitle) {

        wait.until(
                ExpectedConditions.titleContains(expectedTitle)
        );
    }

    // Wait for element to disappear
    protected void waitForInvisibility(By by) {

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(by)
        );
    }

    // Check current page title
    public String getTitle() {
        return driver.getTitle();
    }

    // Check current URL
    public String getUrl() {
        return driver.getCurrentUrl();
    }

    // Refresh current page
    protected void refreshPage() {
        driver.navigate().refresh();
    }

    // Navigate back
    protected void navigateBack() {
        driver.navigate().back();
    }
}
