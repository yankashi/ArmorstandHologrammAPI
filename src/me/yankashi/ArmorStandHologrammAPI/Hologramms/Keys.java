package me.yankashi.ArmorStandHologrammAPI.Hologramms;

import org.jetbrains.annotations.Contract;

/**
 * Enum for different keys
 */
public enum Keys {
    SHOP("Shop"),
    DISPLAY("Display"),
    PLOT("Plot");

    private String key;

    Keys(String key) {
        this.key = key;
    }

    @Contract(pure = true)
    public String getKey() {
        return key;
    }
}