package bingo.ribbons;

import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RibbonResultTest {
    private final RibbonResult resultA = new RibbonResult(Ribbon.SET_ON_FIRE, 11);
    private final RibbonResult resultB = new RibbonResult(Ribbon.SPOTTED, 3);
    private final RibbonResult resultC = new RibbonResult(Ribbon.MAIN_GUN_HIT, 30);
    private final RibbonResult resultD = new RibbonResult(Ribbon.SECONDARY_HIT, 110);
    private final RibbonResult resultE = new RibbonResult(Ribbon.TORPEDO_HIT, 5);

    @Test
    void getPointValueShouldReturnCorrectValueWithLargeCaliberGunsAsMainArmamentType() {
        assertEquals(220, resultA.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals(30, resultB.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals(90, resultC.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals(110, resultD.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals(150, resultE.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
    }

    @Test
    void getPointValueShouldReturnCorrectValueWithAircraftAsMainArmamentType() {
        assertEquals(220, resultA.getPointValue(MainArmamentType.AIRCRAFT));
        assertEquals(15, resultB.getPointValue(MainArmamentType.AIRCRAFT));
        assertEquals(30, resultC.getPointValue(MainArmamentType.AIRCRAFT));
        assertEquals(110, resultD.getPointValue(MainArmamentType.AIRCRAFT));
        assertEquals(75, resultE.getPointValue(MainArmamentType.AIRCRAFT));
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextWithLargeCaliberGunsAsMainArmamentType() {
        assertEquals("Set on fire: 11 * 20 points", resultA.getAsString(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals("Spotted: 3 * 10 points", resultB.getAsString(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals("Main gun hit: 30 * 3 points", resultC.getAsString(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals("Secondary hit: 110 * 1 points", resultD.getAsString(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals("Torpedo hit: 5 * 30 points", resultE.getAsString(MainArmamentType.LARGE_CALIBER_GUNS));
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextWithAircraftAsMainArmamentType() {
        assertEquals("Set on fire: 11 * 20 points", resultA.getAsString(MainArmamentType.AIRCRAFT));
        assertEquals("Spotted: 3 * 5 points", resultB.getAsString(MainArmamentType.AIRCRAFT));
        assertEquals("Main gun hit: 30 * 1 points", resultC.getAsString(MainArmamentType.AIRCRAFT));
        assertEquals("Secondary hit: 110 * 1 points", resultD.getAsString(MainArmamentType.AIRCRAFT));
        assertEquals("Torpedo hit: 5 * 15 points", resultE.getAsString(MainArmamentType.AIRCRAFT));
    }
}
