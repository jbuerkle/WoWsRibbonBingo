package bingo.ships;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainArmamentTypeTest {

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        assertEquals("Gun caliber up to 304mm", MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS.getDisplayText());
        assertEquals("Gun caliber of 305mm+", MainArmamentType.LARGE_CALIBER_GUNS.getDisplayText());
        assertEquals("Torpedoes", MainArmamentType.TORPEDOES.getDisplayText());
        assertEquals("Aircraft", MainArmamentType.AIRCRAFT.getDisplayText());
    }
}
