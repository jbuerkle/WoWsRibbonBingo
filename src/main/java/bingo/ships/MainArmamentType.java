package bingo.ships;

import java.io.Serial;
import java.io.Serializable;

public enum MainArmamentType implements Serializable {
    SMALL_CALIBER_GUNS("Gun caliber up to 202mm"),
    MEDIUM_CALIBER_GUNS("Gun caliber of 203mm+"),
    LARGE_CALIBER_GUNS("Gun caliber of 305mm+"),
    EXTRA_LARGE_CALIBER_GUNS("Gun caliber of 406mm+"),
    TORPEDOES("Torpedoes"),
    AIRCRAFT("Aircraft");

    @Serial
    private static final long serialVersionUID = 8814846151313859190L;

    private final String displayText;

    MainArmamentType(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
