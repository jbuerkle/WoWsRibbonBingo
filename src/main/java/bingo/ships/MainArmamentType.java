package bingo.ships;

import java.io.Serial;
import java.io.Serializable;

public enum MainArmamentType implements Serializable {
    SMALL_CALIBER_GUNS("Gun caliber up to 202mm"),
    MEDIUM_CALIBER_GUNS("Gun caliber of 203mm up to 304mm"),
    LARGE_CALIBER_GUNS("Gun caliber of 305mm up to 405mm"),
    EXTRA_LARGE_CALIBER_GUNS("Gun caliber of 406mm and above"),
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
