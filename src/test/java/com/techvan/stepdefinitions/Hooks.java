package com.techvan.stepdefinitions;

import com.techvan.base.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.nio.file.Files;
import java.nio.file.Path;

public class Hooks {

    @Before
    public void setUp() {
        DriverFactory.init();
    }

    /**
     * On failure: one screenshot of the final page state, embedded in the Cucumber HTML report
     * AND saved to target/screenshots. The browser is always closed afterwards.
     */
    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverFactory.isInitialized()) {
                byte[] png = ((TakesScreenshot) DriverFactory.get()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(png, "image/png", scenario.getName());

                Path dir = Path.of("target", "screenshots");
                Files.createDirectories(dir);
                String file = scenario.getName().replaceAll("[^a-zA-Z0-9]+", "_")
                        + "_" + System.currentTimeMillis() + ".png";
                Files.write(dir.resolve(file), png);
            }
        } catch (Exception e) {
            System.err.println("Could not capture screenshot: " + e.getMessage());
        } finally {
            DriverFactory.quit();
        }
    }
}
