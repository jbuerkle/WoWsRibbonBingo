package bingo.game.ships;

import java.io.Serial;
import java.io.Serializable;

public enum MainArmamentType implements Serializable {
    SMALL_CALIBER_GUNS("1–202mm guns"),
    MEDIUM_CALIBER_GUNS("203–304mm guns"),
    LARGE_CALIBER_GUNS("305–405mm guns"),
    EXTRA_LARGE_CALIBER_GUNS("406mm+ guns"),
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
