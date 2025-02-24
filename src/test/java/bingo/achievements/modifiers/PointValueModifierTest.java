package bingo.achievements.modifiers;

import bingo.ribbons.Ribbon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PointValueModifierTest {

    @Test
    void getBonusModifierAsPercentageShouldReturnCorrectText() {
        assertEquals("0%", getBonusModifierAsPercentage(0.0));
        assertEquals("10%", getBonusModifierAsPercentage(0.1));
        assertEquals("25%", getBonusModifierAsPercentage(0.25));
        assertEquals("50%", getBonusModifierAsPercentage(0.5));
        assertEquals("75%", getBonusModifierAsPercentage(0.75));
        assertEquals("90%", getBonusModifierAsPercentage(0.90));
        assertEquals("100%", getBonusModifierAsPercentage(1.0));
    }

    private String getBonusModifierAsPercentage(double bonusModifier) {
        return new PointValueModifier(Ribbon.MAIN_GUN_HIT, bonusModifier).getBonusModifierAsPercentage();
    }
}
