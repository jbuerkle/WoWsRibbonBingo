package bingo.game.achievements.modifiers;

import bingo.game.ribbons.Ribbon;
import bingo.game.text.TextUtility;

public record PointValueModifier(Ribbon ribbon, double bonusModifier) {

    @Override
    public String toString() {
        return " + %s bonus points for all '%s' ribbons".formatted(
                TextUtility.getAsPercentage(bonusModifier),
                ribbon.getDisplayText());
    }
}
