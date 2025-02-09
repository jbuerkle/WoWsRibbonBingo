package ribbons;

import ribbons.overrides.PointValueOverride;
import ships.MainArmamentType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public enum Ribbon {
    DESTROYED("Destroyed", 120),
    MAIN_GUN_HIT("Main gun hit", 1),
    SECONDARY_HIT("Secondary hit", 1),
    BOMB_HIT("Bomb hit", 3),
    ROCKET_HIT("Rocket hit", 3),
    CITADEL_HIT("Citadel hit", 30),
    TORPEDO_HIT("Torpedo hit", 30),
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
    DEFENDED("Defended", 10),
    BUFF_PICKED_UP("Buff picked up", 40);

    private static final Map<Ribbon, Set<PointValueOverride>> POINT_VALUE_OVERRIDES = setUpOverrides();

    private final String displayText;
    private final int pointValue;

    Ribbon(String displayText, int pointValue) {
        this.displayText = displayText;
        this.pointValue = pointValue;
    }

    public String getDisplayText() {
        return displayText;
    }

    public int getPointValue(MainArmamentType mainArmamentType) {
        return getPointValueOverride(mainArmamentType).orElse(pointValue);
    }

    private Optional<Integer> getPointValueOverride(MainArmamentType mainArmamentType) {
        if (pointValueOverrideExistsForRibbon(this)) {
            return POINT_VALUE_OVERRIDES.get(this)
                    .stream()
                    .filter(pointValueOverride -> pointValueOverride.mainArmamentType().equals(mainArmamentType))
                    .map(PointValueOverride::pointValue)
                    .findAny();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return displayText + ": " + getPointValueAsString(pointValue);
    }

    public static String getAllRibbonsListedAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Ribbon ribbon : Ribbon.values()) {
            stringBuilder.append("- ").append(ribbon.toString());
            if (pointValueOverrideExistsForRibbon(ribbon)) {
                String allPointValueOverridesForRibbon = POINT_VALUE_OVERRIDES.get(ribbon)
                        .stream()
                        .map(Ribbon::getPointValueOverrideAsString)
                        .reduce((string1, string2) -> string1.concat(", ").concat(string2))
                        .orElse("");
                stringBuilder.append(" (").append(allPointValueOverridesForRibbon).append(")");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private static boolean pointValueOverrideExistsForRibbon(Ribbon ribbon) {
        return POINT_VALUE_OVERRIDES.containsKey(ribbon);
    }

    private static String getPointValueOverrideAsString(PointValueOverride pointValueOverride) {
        return "%s for ships with %s as main armament".formatted(
                getPointValueAsString(pointValueOverride.pointValue()),
                pointValueOverride.mainArmamentType().getDisplayText().toLowerCase());
    }

    private static String getPointValueAsString(int pointValue) {
        return pointValue + (pointValue == 1 ? " point" : " points");
    }

    private static Map<Ribbon, Set<PointValueOverride>> setUpOverrides() {
        Map<Ribbon, Set<PointValueOverride>> pointValueOverrides = new HashMap<>();
        pointValueOverrides.put(MAIN_GUN_HIT, Set.of(new PointValueOverride(MainArmamentType.LARGE_CALIBER_GUNS, 3)));
        pointValueOverrides.put(TORPEDO_HIT, Set.of(new PointValueOverride(MainArmamentType.AIRCRAFT, 15)));
        return pointValueOverrides;
    }
}
