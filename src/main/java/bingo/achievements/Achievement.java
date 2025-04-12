package bingo.achievements;

import bingo.achievements.modifiers.PointValueModifier;
import bingo.math.terms.impl.LabeledTerm;
import bingo.math.terms.impl.Literal;
import bingo.math.terms.impl.TermWithPoints;
import bingo.ribbons.Ribbon;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public enum Achievement {
    ARSONIST("Arsonist", 30),
    AA_DEFENSE_EXPERT("AA Defense Expert", 15),
    CLOSE_QUARTERS_EXPERT("Close Quarters Expert", 25),
    DEVASTATING_STRIKE("Devastating Strike", 50),
    DOUBLE_STRIKE("Double Strike", 75),
    DIE_HARD("Die-Hard", 50),
    FIRST_BLOOD("First Blood", 50),
    ITS_JUST_A_FLESH_WOUND("It's Just a Flesh Wound", 50),
    FIREPROOF("Fireproof", 50),
    UNSINKABLE("Unsinkable", 50),
    DREADNOUGHT("Dreadnought", 50),
    COMBAT_SCOUT("Combat Scout", 30),
    CONFEDERATE("Confederate", 150),
    HIGH_CALIBER("High Caliber", 150),
    KRAKEN_UNLEASHED("Kraken Unleashed", 30),
    SOLO_WARRIOR("Solo Warrior", 300),
    WITHERER("Witherer", 30);

    private static final Map<Achievement, Set<PointValueModifier>> POINT_VALUE_MODIFIERS = setUpModifiers();

    private final String displayText;
    private final int flatPointValue;

    Achievement(String displayText, int flatPointValue) {
        this.displayText = displayText;
        this.flatPointValue = flatPointValue;
    }

    public String getDisplayText() {
        return displayText;
    }

    public int getFlatPointValue() {
        return flatPointValue;
    }

    public Set<PointValueModifier> getPointValueModifiers() {
        return POINT_VALUE_MODIFIERS.get(this);
    }

    @Override
    public String toString() {
        return new LabeledTerm(displayText, new TermWithPoints(new Literal(flatPointValue))).getAsString();
    }

    public static String getAllAchievementsListedAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Achievement achievement : Achievement.values()) {
            stringBuilder.append("- ").append(achievement.toString());
            POINT_VALUE_MODIFIERS.get(achievement)
                    .stream()
                    .map(PointValueModifier::toString)
                    .forEach(stringBuilder::append);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private static Map<Achievement, Set<PointValueModifier>> setUpModifiers() {
        Map<Achievement, Set<PointValueModifier>> pointValueModifiers = new HashMap<>();
        for (Achievement achievement : Achievement.values()) {
            pointValueModifiers.put(achievement, new LinkedHashSet<>());
        }
        pointValueModifiers.get(ARSONIST).add(new PointValueModifier(Ribbon.SET_ON_FIRE, 0.1));
        pointValueModifiers.get(AA_DEFENSE_EXPERT).add(new PointValueModifier(Ribbon.AIRCRAFT_SHOT_DOWN, 0.3));
        pointValueModifiers.get(AA_DEFENSE_EXPERT).add(new PointValueModifier(Ribbon.SHOT_DOWN_BY_FIGHTER, 0.3));
        pointValueModifiers.get(KRAKEN_UNLEASHED).add(new PointValueModifier(Ribbon.DESTROYED, 0.2));
        pointValueModifiers.get(COMBAT_SCOUT).add(new PointValueModifier(Ribbon.SPOTTED, 0.6));
        pointValueModifiers.get(WITHERER).add(new PointValueModifier(Ribbon.SET_ON_FIRE, 0.3));
        pointValueModifiers.get(WITHERER).add(new PointValueModifier(Ribbon.CAUSED_FLOODING, 0.3));
        return pointValueModifiers;
    }
}
