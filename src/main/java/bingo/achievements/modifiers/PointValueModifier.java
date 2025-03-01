package bingo.achievements.modifiers;

import bingo.ribbons.Ribbon;

public record PointValueModifier(Ribbon ribbon, double bonusModifier) {

    @Override
    public String toString() {
        return " + %s bonus points for all '%s' ribbons".formatted(
                getBonusModifierAsPercentage(),
                ribbon.getDisplayText());
    }

    private String getBonusModifierAsPercentage() {
        return Math.round(bonusModifier * 100) + "%";
    }
}
