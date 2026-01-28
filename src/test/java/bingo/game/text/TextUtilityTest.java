package bingo.game.text;

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

    @Test
    void getSuffixForPointsShouldReturnCorrectText() {
        assertEquals(" point", TextUtility.getSuffixForPoints(1));
        assertEquals(" points", TextUtility.getSuffixForPoints(2));
        assertEquals(" points", TextUtility.getSuffixForPoints(3));
        assertEquals(" points", TextUtility.getSuffixForPoints(0));
    }

    @Test
    void getSuffixForSubsShouldReturnCorrectText() {
        assertEquals(" sub", TextUtility.getSuffixForSubs(1));
        assertEquals(" subs", TextUtility.getSuffixForSubs(2));
        assertEquals(" subs", TextUtility.getSuffixForSubs(3));
        assertEquals(" subs", TextUtility.getSuffixForSubs(0));
    }
}
