package ribbons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RibbonTest {

    @Test
    void toStringMethodShouldReturnCorrectDisplayText() {
        assertEquals("Citadel hit: 30 points", Ribbon.CITADEL_HIT.toString());
        assertEquals("Rocket hit: 2 points", Ribbon.ROCKET_HIT.toString());
        assertEquals("Main gun hit: 1 point", Ribbon.MAIN_GUN_HIT.toString());
    }

    @Test
    void allRibbonsListedAsStringShouldReturnLongString() {
        String expectedString = """
                - Destroyed: 120 points
                - Main gun hit: 1 point
                - Secondary hit: 1 point
                - Bomb hit: 2 points
                - Rocket hit: 2 points
                - Citadel hit: 30 points
                - Torpedo hit: 20 points
                - Depth charge hit: 10 points
                - Sonar ping: 1 point
                - Spotted: 5 points
                - Incapacitation: 10 points
                - Set on fire: 20 points
                - Caused flooding: 40 points
                - Aircraft shot down: 5 points
                - Shot down by fighter: 5 points
                - Captured: 60 points
                - Assisted in capture: 30 points
                - Defended: 10 points
                """;
        assertEquals(expectedString, Ribbon.allRibbonsListedAsString());
    }
}
