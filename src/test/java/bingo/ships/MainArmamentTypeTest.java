package bingo.ships;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainArmamentTypeTest {

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        assertEquals("Gun caliber up to 202mm", MainArmamentType.SMALL_CALIBER_GUNS.getDisplayText());
        assertEquals("Gun caliber of 203mm up to 304mm", MainArmamentType.MEDIUM_CALIBER_GUNS.getDisplayText());
        assertEquals("Gun caliber of 305mm up to 405mm", MainArmamentType.LARGE_CALIBER_GUNS.getDisplayText());
        assertEquals("Gun caliber of 406mm and above", MainArmamentType.EXTRA_LARGE_CALIBER_GUNS.getDisplayText());
        assertEquals("Torpedoes", MainArmamentType.TORPEDOES.getDisplayText());
        assertEquals("Aircraft", MainArmamentType.AIRCRAFT.getDisplayText());
    }
}
