package bingo.game.ribbons.overrides;

import bingo.game.ships.MainArmamentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PointValueOverrideTest {

    @Test
    void toStringMethodShouldReturnCorrectDisplayText() {
        assertEquals("30 points for ships with 305–405mm guns as main armament", getPointValueOverrideAsString());
    }

    private String getPointValueOverrideAsString() {
        return new PointValueOverride(MainArmamentType.LARGE_CALIBER_GUNS, 30).toString();
    }
}
