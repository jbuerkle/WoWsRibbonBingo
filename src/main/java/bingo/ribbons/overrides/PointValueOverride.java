package bingo.ribbons.overrides;

import bingo.math.terms.impl.Literal;
import bingo.math.terms.impl.TermWithPoints;
import bingo.ships.MainArmamentType;

public record PointValueOverride(MainArmamentType mainArmamentType, int pointValue) {

    @Override
    public String toString() {
        return "%s for ships with %s as main armament".formatted(
                getPointValueAsString(),
                mainArmamentType.getDisplayText().toLowerCase());
    }

    private String getPointValueAsString() {
        return new TermWithPoints(new Literal(pointValue)).getAsString();
    }
}
