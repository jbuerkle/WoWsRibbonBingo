package ribbons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RibbonTest {

    @Test
    void getAsStringShouldReturnCorrectDisplayTextWithModifierDisabled() {
        assertEquals("Citadel hit: 30 points", Ribbon.CITADEL_HIT.getAsString(false));
        assertEquals("Rocket hit: 2 points", Ribbon.ROCKET_HIT.getAsString(false));
        assertEquals("Main gun hit: 1 point", Ribbon.MAIN_GUN_HIT.getAsString(false));
    }

    @Test
    void getAsStringShouldReturnCorrectDisplayTextWithModifierEnabled() {
        assertEquals("Citadel hit: 30 points", Ribbon.CITADEL_HIT.getAsString(true));
        assertEquals("Rocket hit: 2 points", Ribbon.ROCKET_HIT.getAsString(true));
        assertEquals("Main gun hit: 3 points", Ribbon.MAIN_GUN_HIT.getAsString(true));
    }

    @Test
    void getAllRibbonsListedAsStringShouldReturnLongString() {
        String expectedString = """
                - Destroyed: 120 points
                - Main gun hit: 1 point (3x modifier for BB guns)
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
        assertEquals(expectedString, Ribbon.getAllRibbonsListedAsString());
    }
}
