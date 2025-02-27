package bingo.achievements.modifiers;

import bingo.ribbons.Ribbon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PointValueModifierTest {

    @Test
    void toStringMethodShouldReturnCorrectDisplayText() {
        assertEquals(" + 25% bonus points for all 'Main gun hit' ribbons", getPointValueModifierAsString());
    }

    private String getPointValueModifierAsString() {
        return new PointValueModifier(Ribbon.MAIN_GUN_HIT, 0.25).toString();
    }
}
