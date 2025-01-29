package ribbons;

public enum Ribbon {
    DESTROYED("Destroyed", 120),
    MAIN_GUN_HIT("Main gun hit", 1, 3),
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
    private final int battleshipModifier;

    Ribbon(String displayText, int pointValue, int battleshipModifier) {
        this.displayText = displayText;
        this.pointValue = pointValue;
        this.battleshipModifier = battleshipModifier;
    }

    Ribbon(String displayText, int pointValue) {
        this.displayText = displayText;
        this.pointValue = pointValue;
        this.battleshipModifier = 1;
    }

    public String getDisplayText() {
        return displayText;
    }

    public int getPointValue(boolean battleshipModifierEnabled) {
        if (battleshipModifierEnabled) {
            return pointValue * battleshipModifier;
        } else {
            return pointValue;
        }
    }

    private String getPointValueAsString(boolean battleshipModifierEnabled) {
        int actualPointValue = getPointValue(battleshipModifierEnabled);
        return actualPointValue + (actualPointValue == 1 ? " point" : " points");
    }

    public String getAsString(boolean battleshipModifierEnabled) {
        return displayText + ": " + getPointValueAsString(battleshipModifierEnabled);
    }

    public static String getAllRibbonsListedAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Ribbon ribbon : Ribbon.values()) {
            stringBuilder.append("- ").append(ribbon.getAsString(false));
            if (ribbon.battleshipModifier != 1) {
                stringBuilder.append(" (%sx modifier for BB guns)".formatted(ribbon.battleshipModifier));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
