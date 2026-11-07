package com.techvan.runners;

import io.cucumber.core.cli.Main;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

/** Re-runs only the scenarios that failed last time. Run this class directly (IntelliJ gutter icon). */
public class FailedRunner {
    @Test
    public void rerunFailedScenarios() throws Exception {
        Path failed = Path.of("target", "failed.txt");
        if (!Files.exists(failed) || Files.readString(failed).isBlank()) {
            throw new SkipException("target/failed.txt is missing or empty - nothing to re-run.");
        }
        int exitCode = Main.run(new String[]{
                "--glue", "com.techvan.stepdefinitions",
                "--plugin", "pretty",
                "--plugin", "html:target/cucumber-reports/rerun-report.html",
                "--monochrome",
                "@target/failed.txt"
        }, Thread.currentThread().getContextClassLoader());

        if (exitCode != 0) {
            throw new AssertionError("Cucumber rerun failed with exit code " + exitCode);
        }
    }
}
