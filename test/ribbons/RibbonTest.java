package ribbons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RibbonTest {

    @Test
    void toStringMethodShouldReturnCorrectDisplayText() {
        assertEquals("Citadel hit: 30 points", Ribbon.CITADEL_HIT.toString());
        assertEquals("Rocket hit: 2 points", Ribbon.ROCKET_HIT.toString());
        assertEquals("Shell hit: 1 point", Ribbon.SHELL_HIT.toString());
    }
}
