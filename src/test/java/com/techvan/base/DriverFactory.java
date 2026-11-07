
package com.techvan.base;

import com.techvan.utils.ConfigReader;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

/**
 * DriverFactory
 *
 * Manages WebDriver instances using ThreadLocal.
 * Supports Chrome, Firefox and Edge.
 * Selenium Manager automatically manages driver binaries.
 *
 * Designed for parallel Cucumber + TestNG execution.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER =
            new ThreadLocal<>();

    private DriverFactory() {
        // Prevent object creation
    }

    /**
     * Initialize browser for the current thread.
     */
    public static void init() {

        if (DRIVER.get() != null) {
            throw new IllegalStateException(
                    "WebDriver is already initialized for this thread."
            );
        }

        String browser = Objects.requireNonNull(
                ConfigReader.get("browser"),
                "Browser configuration is missing."
        ).trim().toLowerCase(Locale.ROOT);

        boolean headless = ConfigReader.getBool("headless");

        int implicitWait = ConfigReader.getInt("implicitWait");
        int pageLoadTimeout = ConfigReader.getInt("pageLoadTimeout");

        if (implicitWait < 0 || pageLoadTimeout <= 0) {
            throw new IllegalArgumentException(
                    "Invalid browser timeout configuration."
            );
        }

        WebDriver driver = createDriver(browser, headless);

        try {

            driver.manage().timeouts().implicitlyWait(
                    Duration.ofSeconds(implicitWait)
            );

            driver.manage().timeouts().pageLoadTimeout(
                    Duration.ofSeconds(pageLoadTimeout)
            );

            if (!headless) {
                driver.manage().window().maximize();
            }

            DRIVER.set(driver);

        } catch (RuntimeException e) {

            try {
                driver.quit();
            } catch (RuntimeException cleanupError) {
                e.addSuppressed(cleanupError);
            }

            throw e;
        }
    }

    /**
     * Create browser instance.
     */
    private static WebDriver createDriver(
            String browser,
            boolean headless
    ) {

        return switch (browser) {

            case "chrome" -> {

                ChromeOptions options = new ChromeOptions();

                options.addArguments(
                        "--disable-notifications",
                        "--disable-search-engine-choice-screen",
                        "--window-size=1920,1080"
                );

                if (headless) {
                    options.addArguments("--headless=new");
                }

                yield new ChromeDriver(options);
            }

            case "firefox" -> {

                FirefoxOptions options = new FirefoxOptions();

                if (headless) {
                    options.addArguments("-headless");
                }

                options.addArguments(
                        "--width=1920",
                        "--height=1080"
                );

                yield new FirefoxDriver(options);
            }

            case "edge" -> {

                EdgeOptions options = new EdgeOptions();

                options.addArguments(
                        "--disable-notifications",
                        "--disable-search-engine-choice-screen",
                        "--window-size=1920,1080"
                );

                if (headless) {
                    options.addArguments("--headless=new");
                }

                yield new EdgeDriver(options);
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser
                    + ". Supported browsers: chrome, firefox, edge."
            );
        };
    }

    /**
     * Get WebDriver for the current thread.
     */
    public static WebDriver get() {

        WebDriver driver = DRIVER.get();

        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver is not initialized. Call DriverFactory.init() first."
            );
        }

        return driver;
    }

    /**
     * Check whether current thread has an initialized driver.
     */
    public static boolean isInitialized() {
        return DRIVER.get() != null;
    }

    /**
     * Quit browser and clean ThreadLocal.
     */
    public static void quit() {

        WebDriver driver = DRIVER.get();

        try {

            if (driver != null) {
                driver.quit();
            }

        } finally {
            DRIVER.remove();
        }
    }
}
