package bingo.ribbons;

import bingo.math.terms.impl.LabeledTerm;
import bingo.math.terms.impl.Literal;
import bingo.math.terms.impl.TermWithPoints;
import bingo.ribbons.overrides.PointValueOverride;
import bingo.ships.MainArmamentType;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public enum Ribbon implements Serializable {
    DESTROYED("Destroyed", 120),
    MAIN_GUN_HIT("Main gun hit", 1),
    SECONDARY_HIT("Secondary hit", 1),
    BOMB_HIT("Bomb hit", 3),
    ROCKET_HIT("Rocket hit", 3),
    CITADEL_HIT("Citadel hit", 20),
    TORPEDO_HIT("Torpedo hit", 40),
    DEPTH_CHARGE_HIT("Depth charge hit", 10),
    SONAR_PING("Sonar ping", 5),
    SPOTTED("Spotted", 30),
    INCAPACITATION("Incapacitation", 10),
    SET_ON_FIRE("Set on fire", 20),
    CAUSED_FLOODING("Caused flooding", 40),
    AIRCRAFT_SHOT_DOWN("Aircraft shot down", 10),
    SHOT_DOWN_BY_FIGHTER("Shot down by fighter", 10),
    CAPTURED("Captured", 80),
    ASSISTED_IN_CAPTURE("Assisted in capture", 40),
    DEFENDED("Defended", 10),
    BUFF_PICKED_UP("Buff picked up", 60);

    @Serial
    private static final long serialVersionUID = 6166813751001367167L;
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
        return POINT_VALUE_OVERRIDES.get(this)
                .stream()
                .filter(pointValueOverride -> pointValueOverride.mainArmamentType().equals(mainArmamentType))
                .map(PointValueOverride::pointValue)
                .findAny();
    }

    @Override
    public String toString() {
        return new LabeledTerm(displayText, new TermWithPoints(new Literal(pointValue))).getAsString();
    }

    public static String getAllRibbonsListedAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Ribbon ribbon : Ribbon.values()) {
            stringBuilder.append("- ").append(ribbon.toString());
            POINT_VALUE_OVERRIDES.get(ribbon)
                    .stream()
                    .map(PointValueOverride::toString)
                    .reduce((overrideA, overrideB) -> overrideA.concat(", ").concat(overrideB))
                    .ifPresent(appendInParenthesisTo(stringBuilder));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private static Consumer<String> appendInParenthesisTo(StringBuilder stringBuilder) {
        return allOverridesForRibbon -> stringBuilder.append(" (").append(allOverridesForRibbon).append(")");
    }

    private static Map<Ribbon, Set<PointValueOverride>> setUpOverrides() {
        Map<Ribbon, Set<PointValueOverride>> pointValueOverrides = new HashMap<>();
        for (Ribbon ribbon : Ribbon.values()) {
            pointValueOverrides.put(ribbon, new LinkedHashSet<>());
        }
        pointValueOverrides.get(MAIN_GUN_HIT).add(new PointValueOverride(MainArmamentType.MEDIUM_CALIBER_GUNS, 2));
        pointValueOverrides.get(MAIN_GUN_HIT).add(new PointValueOverride(MainArmamentType.LARGE_CALIBER_GUNS, 3));
        pointValueOverrides.get(MAIN_GUN_HIT).add(new PointValueOverride(MainArmamentType.EXTRA_LARGE_CALIBER_GUNS, 4));
        pointValueOverrides.get(CITADEL_HIT).add(new PointValueOverride(MainArmamentType.MEDIUM_CALIBER_GUNS, 40));
        pointValueOverrides.get(CITADEL_HIT).add(new PointValueOverride(MainArmamentType.LARGE_CALIBER_GUNS, 60));
        pointValueOverrides.get(CITADEL_HIT).add(new PointValueOverride(MainArmamentType.EXTRA_LARGE_CALIBER_GUNS, 80));
        pointValueOverrides.get(TORPEDO_HIT).add(new PointValueOverride(MainArmamentType.AIRCRAFT, 20));
        pointValueOverrides.get(SPOTTED).add(new PointValueOverride(MainArmamentType.AIRCRAFT, 10));
        return pointValueOverrides;
    }
}
