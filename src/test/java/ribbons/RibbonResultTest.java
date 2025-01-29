package ribbons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RibbonResultTest {
    private final RibbonResult resultA = new RibbonResult(Ribbon.SET_ON_FIRE, 11);
    private final RibbonResult resultB = new RibbonResult(Ribbon.CAUSED_FLOODING, 3);
    private final RibbonResult resultC = new RibbonResult(Ribbon.MAIN_GUN_HIT, 30);
    private final RibbonResult resultD = new RibbonResult(Ribbon.SECONDARY_HIT, 110);

    @Test
    void getPointValueShouldReturnCorrectValueWithModifierDisabled() {
        assertEquals(220, resultA.getPointValue(false));
        assertEquals(120, resultB.getPointValue(false));
        assertEquals(30, resultC.getPointValue(false));
        assertEquals(110, resultD.getPointValue(false));
    }

    @Test
    void getPointValueShouldReturnCorrectValueWithModifierEnabled() {
        assertEquals(220, resultA.getPointValue(true));
        assertEquals(120, resultB.getPointValue(true));
        assertEquals(90, resultC.getPointValue(true));
        assertEquals(110, resultD.getPointValue(true));
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextWithModifierDisabled() {
        assertEquals("Set on fire: 11 * 20 points", resultA.getAsString(false));
        assertEquals("Caused flooding: 3 * 40 points", resultB.getAsString(false));
        assertEquals("Main gun hit: 30 * 1 points", resultC.getAsString(false));
        assertEquals("Secondary hit: 110 * 1 points", resultD.getAsString(false));
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextWithModifierEnabled() {
        assertEquals("Set on fire: 11 * 20 points", resultA.getAsString(true));
        assertEquals("Caused flooding: 3 * 40 points", resultB.getAsString(true));
        assertEquals("Main gun hit: 30 * 3 points", resultC.getAsString(true));
        assertEquals("Secondary hit: 110 * 1 points", resultD.getAsString(true));
    }

    @Test
    void resultShouldBeEqualWithSameRibbon() {
        RibbonResult mainGunResultA = new RibbonResult(Ribbon.MAIN_GUN_HIT, 30);
        RibbonResult mainGunResultB = new RibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        assertEquals(mainGunResultA, mainGunResultB);
        assertEquals(mainGunResultA.hashCode(), mainGunResultB.hashCode());
    }

    @Test
    void resultShouldNotBeEqualWithDifferentRibbon() {
        RibbonResult mainGunResult = new RibbonResult(Ribbon.MAIN_GUN_HIT, 50);
        RibbonResult secondaryResult = new RibbonResult(Ribbon.SECONDARY_HIT, 50);
        assertNotEquals(mainGunResult, secondaryResult);
        assertNotEquals(mainGunResult.hashCode(), secondaryResult.hashCode());
    }
}
