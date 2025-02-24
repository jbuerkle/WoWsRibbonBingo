package bingo.ribbons;

import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RibbonTest {

    @Test
    void getPointValueShouldReturnCorrectValueWithLargeCaliberGunsAsMainArmamentType() {
        assertEquals(30, Ribbon.CITADEL_HIT.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals(3, Ribbon.ROCKET_HIT.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals(3, Ribbon.MAIN_GUN_HIT.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals(30, Ribbon.TORPEDO_HIT.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
    }

    @Test
    void getPointValueShouldReturnCorrectValueWithAircraftAsMainArmamentType() {
        assertEquals(30, Ribbon.CITADEL_HIT.getPointValue(MainArmamentType.AIRCRAFT));
        assertEquals(3, Ribbon.ROCKET_HIT.getPointValue(MainArmamentType.AIRCRAFT));
        assertEquals(1, Ribbon.MAIN_GUN_HIT.getPointValue(MainArmamentType.AIRCRAFT));
        assertEquals(15, Ribbon.TORPEDO_HIT.getPointValue(MainArmamentType.AIRCRAFT));
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayText() {
        assertEquals("Citadel hit: 30 points", Ribbon.CITADEL_HIT.toString());
        assertEquals("Rocket hit: 3 points", Ribbon.ROCKET_HIT.toString());
        assertEquals("Main gun hit: 1 point", Ribbon.MAIN_GUN_HIT.toString());
        assertEquals("Torpedo hit: 30 points", Ribbon.TORPEDO_HIT.toString());
    }

    @Test
    void getAllRibbonsListedAsStringShouldReturnLongString() {
        String expectedString = """
                - Destroyed: 120 points
                - Main gun hit: 1 point (3 points for ships with gun caliber of 305mm+ as main armament)
                - Secondary hit: 1 point
                - Bomb hit: 3 points
                - Rocket hit: 3 points
                - Citadel hit: 30 points
                - Torpedo hit: 30 points (15 points for ships with aircraft as main armament)
                - Depth charge hit: 10 points
                - Sonar ping: 1 point
                - Spotted: 10 points (5 points for ships with aircraft as main armament)
                - Incapacitation: 10 points
                - Set on fire: 20 points
                - Caused flooding: 40 points
                - Aircraft shot down: 10 points
                - Shot down by fighter: 10 points
                - Captured: 60 points
                - Assisted in capture: 30 points
                - Defended: 10 points
                - Buff picked up: 40 points
                """;
        assertEquals(expectedString, Ribbon.getAllRibbonsListedAsString());
    }
}
