package bingo.achievements.modifiers;

import bingo.ribbons.Ribbon;

public record PointValueModifier(Ribbon ribbon, double bonusModifier) {

    public String getBonusModifierAsPercentage() {
        return Math.round(bonusModifier * 100) + "%";
    }
}
