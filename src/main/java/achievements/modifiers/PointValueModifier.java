package achievements.modifiers;

import ribbons.Ribbon;

public record PointValueModifier(Ribbon ribbon, double bonusModifier) {

    public String getBonusModifierAsPercentage() {
        return Math.round(bonusModifier * 100) + "%";
    }
}
