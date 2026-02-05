package bingo.game.restrictions.impl;

import bingo.game.restrictions.ShipRestriction;
import bingo.game.ships.MainArmamentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForcedMainArmamentTypeTest {
    private final ShipRestriction shipRestriction = new ForcedMainArmamentType(MainArmamentType.AIRCRAFT);

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        assertEquals("must use ships with aircraft as main armament", shipRestriction.getDisplayText());
    }

    @Test
    void allowsMainArmamentTypeShouldReturnTrue() {
        assertTrue(shipRestriction.allowsMainArmamentType(MainArmamentType.AIRCRAFT));
    }

    @Test
    void allowsMainArmamentTypeShouldReturnFalse() {
        assertFalse(shipRestriction.allowsMainArmamentType(MainArmamentType.TORPEDOES));
    }
}
