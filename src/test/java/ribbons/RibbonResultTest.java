package ribbons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RibbonResultTest {

    @Test
    void getPointValueShouldReturnCorrectValue() {
        RibbonResult resultA = new RibbonResult(Ribbon.SET_ON_FIRE, 11);
        RibbonResult resultB = new RibbonResult(Ribbon.CAUSED_FLOODING, 3);
        RibbonResult resultC = new RibbonResult(Ribbon.MAIN_GUN_HIT, 30);
        RibbonResult resultD = new RibbonResult(Ribbon.SECONDARY_HIT, 110);
        assertEquals(220, resultA.getPointValue());
        assertEquals(120, resultB.getPointValue());
        assertEquals(30, resultC.getPointValue());
        assertEquals(110, resultD.getPointValue());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayText() {
        RibbonResult resultA = new RibbonResult(Ribbon.SET_ON_FIRE, 11);
        RibbonResult resultB = new RibbonResult(Ribbon.CAUSED_FLOODING, 3);
        assertEquals("Set on fire: 11 * 20 points", resultA.toString());
        assertEquals("Caused flooding: 3 * 40 points", resultB.toString());
    }

    @Test
    void resultShouldBeEqualWithSameRibbon() {
        RibbonResult resultA = new RibbonResult(Ribbon.MAIN_GUN_HIT, 30);
        RibbonResult resultB = new RibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        assertEquals(resultA, resultB);
        assertEquals(resultA.hashCode(), resultB.hashCode());
    }

    @Test
    void resultShouldNotBeEqualWithDifferentRibbon() {
        RibbonResult resultA = new RibbonResult(Ribbon.MAIN_GUN_HIT, 50);
        RibbonResult resultB = new RibbonResult(Ribbon.SECONDARY_HIT, 50);
        assertNotEquals(resultA, resultB);
        assertNotEquals(resultA.hashCode(), resultB.hashCode());
    }
}
