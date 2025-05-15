package bingo.restrictions.impl;

import bingo.restrictions.ShipRestriction;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BannedMainArmamentTypeTest {

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        ShipRestriction shipRestriction = new BannedMainArmamentType(MainArmamentType.AIRCRAFT);
        assertEquals(
                "Ships with aircraft as main armament are banned from use in the current level",
                shipRestriction.getDisplayText());
    }
}
