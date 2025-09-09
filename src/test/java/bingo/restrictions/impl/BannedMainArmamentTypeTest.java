package bingo.restrictions.impl;

import bingo.restrictions.ShipRestriction;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BannedMainArmamentTypeTest {
    private final ShipRestriction shipRestriction = new BannedMainArmamentType(MainArmamentType.AIRCRAFT);

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        assertEquals(
                "Ships with aircraft as main armament are banned from use in the current level",
                shipRestriction.getDisplayText());
    }

    @Test
    void allowsMainArmamentTypeShouldReturnTrue() {
        assertTrue(shipRestriction.allowsMainArmamentType(MainArmamentType.TORPEDOES));
    }

    @Test
    void allowsMainArmamentTypeShouldReturnFalse() {
        assertFalse(shipRestriction.allowsMainArmamentType(MainArmamentType.AIRCRAFT));
    }
}
