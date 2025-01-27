package ribbons;

public enum Ribbon {
    DESTROYED("Destroyed", 120),
    MAIN_GUN_HIT("Main gun hit", 1),
    SECONDARY_HIT("Secondary hit", 1),
    BOMB_HIT("Bomb hit", 2),
    ROCKET_HIT("Rocket hit", 2),
    CITADEL_HIT("Citadel hit", 30),
    TORPEDO_HIT("Torpedo hit", 20),
    DEPTH_CHARGE_HIT("Depth charge hit", 10),
    SONAR_PING("Sonar ping", 1),
    SPOTTED("Spotted", 5),
    INCAPACITATION("Incapacitation", 10),
    SET_ON_FIRE("Set on fire", 20),
    CAUSED_FLOODING("Caused flooding", 40),
    AIRCRAFT_SHOT_DOWN("Aircraft shot down", 5),
    SHOT_DOWN_BY_FIGHTER("Shot down by fighter", 5),
    CAPTURED("Captured", 60),
    ASSISTED_IN_CAPTURE("Assisted in capture", 30),
    DEFENDED("Defended", 10);

    private final String displayText;
    private final int pointValue;

    Ribbon(String displayText, int pointValue) {
        this.displayText = displayText;
        this.pointValue = pointValue;
    }

    public String getDisplayText() {
        return displayText;
    }

    public int getPointValue() {
        return pointValue;
    }

    private String getPointValueAsString() {
        return pointValue + (pointValue == 1 ? " point" : " points");
    }

    @Override
    public String toString() {
        return displayText + ": " + getPointValueAsString();
    }

    public static String allRibbonsListedAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Ribbon ribbon : Ribbon.values()) {
            stringBuilder.append("- ").append(ribbon.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
