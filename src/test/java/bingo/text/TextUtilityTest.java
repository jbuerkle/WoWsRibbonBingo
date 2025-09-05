package bingo.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextUtilityTest {

    @Test
    void getAsPercentageShouldReturnCorrectText() {
        assertEquals("0%", TextUtility.getAsPercentage(0));
        assertEquals("25%", TextUtility.getAsPercentage(0.25));
        assertEquals("50%", TextUtility.getAsPercentage(0.5));
        assertEquals("75%", TextUtility.getAsPercentage(0.75));
        assertEquals("100%", TextUtility.getAsPercentage(1));
    }
}
