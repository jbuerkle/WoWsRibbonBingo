package bingo.game.ships;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainArmamentTypeTest {

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        assertEquals("1–202mm guns", MainArmamentType.SMALL_CALIBER_GUNS.getDisplayText());
        assertEquals("203–304mm guns", MainArmamentType.MEDIUM_CALIBER_GUNS.getDisplayText());
        assertEquals("305–405mm guns", MainArmamentType.LARGE_CALIBER_GUNS.getDisplayText());
        assertEquals("406mm+ guns", MainArmamentType.EXTRA_LARGE_CALIBER_GUNS.getDisplayText());
        assertEquals("Torpedoes", MainArmamentType.TORPEDOES.getDisplayText());
        assertEquals("Aircraft", MainArmamentType.AIRCRAFT.getDisplayText());
    }
}
