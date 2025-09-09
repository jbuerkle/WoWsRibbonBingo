package bingo.restrictions.impl;

import bingo.restrictions.ShipRestriction;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForcedMainArmamentTypeTest {
    private final ShipRestriction shipRestriction = new ForcedMainArmamentType(MainArmamentType.AIRCRAFT);

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        assertEquals(
                "You are forced to use a ship with aircraft as main armament in the current level",
                shipRestriction.getDisplayText());
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
