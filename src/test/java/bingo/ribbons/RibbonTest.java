package bingo.ribbons;

import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RibbonTest {

    @Test
    void getPointValueShouldReturnCorrectValueWithSmallCaliberGunsAsMainArmamentType() {
        assertEquals(1, Ribbon.MAIN_GUN_HIT.getPointValue(MainArmamentType.SMALL_CALIBER_GUNS));
        assertEquals(20, Ribbon.CITADEL_HIT.getPointValue(MainArmamentType.SMALL_CALIBER_GUNS));
        assertEquals(30, Ribbon.TORPEDO_HIT.getPointValue(MainArmamentType.SMALL_CALIBER_GUNS));
        assertEquals(30, Ribbon.SPOTTED.getPointValue(MainArmamentType.SMALL_CALIBER_GUNS));
    }

    @Test
    void getPointValueShouldReturnCorrectValueWithMediumCaliberGunsAsMainArmamentType() {
        assertEquals(2, Ribbon.MAIN_GUN_HIT.getPointValue(MainArmamentType.MEDIUM_CALIBER_GUNS));
        assertEquals(40, Ribbon.CITADEL_HIT.getPointValue(MainArmamentType.MEDIUM_CALIBER_GUNS));
        assertEquals(30, Ribbon.TORPEDO_HIT.getPointValue(MainArmamentType.MEDIUM_CALIBER_GUNS));
        assertEquals(30, Ribbon.SPOTTED.getPointValue(MainArmamentType.MEDIUM_CALIBER_GUNS));
    }

    @Test
    void getPointValueShouldReturnCorrectValueWithLargeCaliberGunsAsMainArmamentType() {
        assertEquals(3, Ribbon.MAIN_GUN_HIT.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals(60, Ribbon.CITADEL_HIT.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals(30, Ribbon.TORPEDO_HIT.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
        assertEquals(30, Ribbon.SPOTTED.getPointValue(MainArmamentType.LARGE_CALIBER_GUNS));
    }

    @Test
    void getPointValueShouldReturnCorrectValueWithExtraLargeCaliberGunsAsMainArmamentType() {
        assertEquals(4, Ribbon.MAIN_GUN_HIT.getPointValue(MainArmamentType.EXTRA_LARGE_CALIBER_GUNS));
        assertEquals(80, Ribbon.CITADEL_HIT.getPointValue(MainArmamentType.EXTRA_LARGE_CALIBER_GUNS));
        assertEquals(30, Ribbon.TORPEDO_HIT.getPointValue(MainArmamentType.EXTRA_LARGE_CALIBER_GUNS));
        assertEquals(30, Ribbon.SPOTTED.getPointValue(MainArmamentType.EXTRA_LARGE_CALIBER_GUNS));
    }

    @Test
    void getPointValueShouldReturnCorrectValueWithTorpedoesAsMainArmamentType() {
        assertEquals(1, Ribbon.MAIN_GUN_HIT.getPointValue(MainArmamentType.TORPEDOES));
        assertEquals(20, Ribbon.CITADEL_HIT.getPointValue(MainArmamentType.TORPEDOES));
        assertEquals(30, Ribbon.TORPEDO_HIT.getPointValue(MainArmamentType.TORPEDOES));
        assertEquals(30, Ribbon.SPOTTED.getPointValue(MainArmamentType.TORPEDOES));
    }

    @Test
    void getPointValueShouldReturnCorrectValueWithAircraftAsMainArmamentType() {
        assertEquals(1, Ribbon.MAIN_GUN_HIT.getPointValue(MainArmamentType.AIRCRAFT));
        assertEquals(20, Ribbon.CITADEL_HIT.getPointValue(MainArmamentType.AIRCRAFT));
        assertEquals(15, Ribbon.TORPEDO_HIT.getPointValue(MainArmamentType.AIRCRAFT));
        assertEquals(10, Ribbon.SPOTTED.getPointValue(MainArmamentType.AIRCRAFT));
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayText() {
        assertEquals("Citadel hit: 20 points", Ribbon.CITADEL_HIT.toString());
        assertEquals("Rocket hit: 3 points", Ribbon.ROCKET_HIT.toString());
        assertEquals("Main gun hit: 1 point", Ribbon.MAIN_GUN_HIT.toString());
        assertEquals("Torpedo hit: 30 points", Ribbon.TORPEDO_HIT.toString());
    }

    @Test
    void getAllRibbonsListedAsStringShouldReturnLongString() {
        String expectedString = """
                - Destroyed: 120 points
                - Main gun hit: 1 point (2 points for ships with gun caliber of 203mm+ as main armament, 3 points for ships with gun caliber of 305mm+ as main armament, 4 points for ships with gun caliber of 406mm+ as main armament)
                - Secondary hit: 1 point
                - Bomb hit: 3 points
                - Rocket hit: 3 points
                - Citadel hit: 20 points (40 points for ships with gun caliber of 203mm+ as main armament, 60 points for ships with gun caliber of 305mm+ as main armament, 80 points for ships with gun caliber of 406mm+ as main armament)
                - Torpedo hit: 30 points (15 points for ships with aircraft as main armament)
                - Depth charge hit: 10 points
                - Sonar ping: 5 points
                - Spotted: 30 points (10 points for ships with aircraft as main armament)
                - Incapacitation: 10 points
                - Set on fire: 20 points
                - Caused flooding: 40 points
                - Aircraft shot down: 10 points
                - Shot down by fighter: 10 points
                - Captured: 80 points
                - Assisted in capture: 40 points
                - Defended: 10 points
                - Buff picked up: 60 points
                """;
        assertEquals(expectedString, Ribbon.getAllRibbonsListedAsString());
    }
}
