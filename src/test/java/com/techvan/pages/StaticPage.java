package com.techvan.pages;

import com.techvan.base.BasePage;
import com.techvan.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Locale;

/** About, Contact, Terms, Privacy, Payment Methods. */
public class StaticPage extends BasePage {

    private final By body    = By.tagName("body");
    private final By heading = By.xpath("(//h1 | //h2)[1]");

    public StaticPage open(String path) {
        driver.get(ConfigReader.get("baseUrl") + path);
        visible(body);
        return this;
    }

    public boolean hasHeading() {
        return isDisplayed(heading);
    }

    public boolean bodyContains(String text) {
        return visible(body).getText().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT));
    }

    /**
     * Looks only at the title and the main heading. Scanning the whole body for "404"/"500"
     * gave false failures on any page with a price such as "BDT 25,500".
     */
    public boolean isNotErrorPage() {
        visible(body);

        String title = driver.getTitle() == null ? "" : driver.getTitle().toLowerCase(Locale.ROOT);

        List<WebElement> h = driver.findElements(By.xpath("(//h1)[1]"));
        String h1 = h.isEmpty() ? "" : h.get(0).getText().toLowerCase(Locale.ROOT);

        return !looksLikeError(title) && !looksLikeError(h1);
    }

    private boolean looksLikeError(String text) {
        return text.matches(".*\\b(404|500)\\b.*")
                || text.contains("page not found")
                || text.contains("not found")
                || text.contains("something went wrong");
    }
}
