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
    void getValueShouldReturnCorrectValueWithLargeCaliberGunsAsMainArmamentType() {
        assertEquals(220, resultA.getAsTerm(MainArmamentType.LARGE_CALIBER_GUNS).getValue());
        assertEquals(30, resultB.getAsTerm(MainArmamentType.LARGE_CALIBER_GUNS).getValue());
        assertEquals(90, resultC.getAsTerm(MainArmamentType.LARGE_CALIBER_GUNS).getValue());
        assertEquals(110, resultD.getAsTerm(MainArmamentType.LARGE_CALIBER_GUNS).getValue());
        assertEquals(150, resultE.getAsTerm(MainArmamentType.LARGE_CALIBER_GUNS).getValue());
    }

    @Test
    void getValueShouldReturnCorrectValueWithAircraftAsMainArmamentType() {
        assertEquals(220, resultA.getAsTerm(MainArmamentType.AIRCRAFT).getValue());
        assertEquals(15, resultB.getAsTerm(MainArmamentType.AIRCRAFT).getValue());
        assertEquals(30, resultC.getAsTerm(MainArmamentType.AIRCRAFT).getValue());
        assertEquals(110, resultD.getAsTerm(MainArmamentType.AIRCRAFT).getValue());
        assertEquals(75, resultE.getAsTerm(MainArmamentType.AIRCRAFT).getValue());
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextWithLargeCaliberGunsAsMainArmamentType() {
        assertEquals(
                "Set on fire: 11 * 20 points",
                resultA.getAsTerm(MainArmamentType.LARGE_CALIBER_GUNS).getAsString());
        assertEquals("Spotted: 3 * 10 points", resultB.getAsTerm(MainArmamentType.LARGE_CALIBER_GUNS).getAsString());
        assertEquals(
                "Main gun hit: 30 * 3 points",
                resultC.getAsTerm(MainArmamentType.LARGE_CALIBER_GUNS).getAsString());
        assertEquals("Secondary hit: 110 points", resultD.getAsTerm(MainArmamentType.LARGE_CALIBER_GUNS).getAsString());
        assertEquals(
                "Torpedo hit: 5 * 30 points",
                resultE.getAsTerm(MainArmamentType.LARGE_CALIBER_GUNS).getAsString());
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextWithAircraftAsMainArmamentType() {
        assertEquals("Set on fire: 11 * 20 points", resultA.getAsTerm(MainArmamentType.AIRCRAFT).getAsString());
        assertEquals("Spotted: 3 * 5 points", resultB.getAsTerm(MainArmamentType.AIRCRAFT).getAsString());
        assertEquals("Main gun hit: 30 points", resultC.getAsTerm(MainArmamentType.AIRCRAFT).getAsString());
        assertEquals("Secondary hit: 110 points", resultD.getAsTerm(MainArmamentType.AIRCRAFT).getAsString());
        assertEquals("Torpedo hit: 5 * 15 points", resultE.getAsTerm(MainArmamentType.AIRCRAFT).getAsString());
    }
}
