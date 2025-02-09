package ships;

public enum MainArmamentType {
    SMALL_OR_MEDIUM_CALIBER_GUNS("Gun caliber up to 304mm"),
    LARGE_CALIBER_GUNS("Gun caliber of 305mm+"),
    TORPEDOES("Torpedoes"),
    AIRCRAFT("Aircraft");

    private final String displayText;

    MainArmamentType(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
