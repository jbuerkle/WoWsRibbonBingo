package bingo.achievements.modifiers;

import bingo.ribbons.Ribbon;
import bingo.text.TextUtility;

public record PointValueModifier(Ribbon ribbon, double bonusModifier) {

    @Override
    public String toString() {
        return " + %s bonus points for all '%s' ribbons".formatted(
                TextUtility.getAsPercentage(bonusModifier),
                ribbon.getDisplayText());
    }
}
