package bingo.restrictions.impl;

import bingo.restrictions.ShipRestriction;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForcedMainArmamentTypeTest {

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        ShipRestriction shipRestriction = new ForcedMainArmamentType(MainArmamentType.AIRCRAFT);
        assertEquals(
                "You are forced to use a ship with aircraft as main armament in the current level",
                shipRestriction.getDisplayText());
    }
}
