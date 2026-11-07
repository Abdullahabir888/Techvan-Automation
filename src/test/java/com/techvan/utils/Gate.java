package com.techvan.utils;

import org.testng.SkipException;

/** Opt-in switches for scenarios that change data on the live site. */
public final class Gate {

    private Gate() {}

    public static void require(String configKey, String reason) {
        if (!ConfigReader.getBool(configKey)) {
            throw new SkipException("Skipped: " + reason + " (enable with -D" + configKey + "=true)");
        }
    }
}
