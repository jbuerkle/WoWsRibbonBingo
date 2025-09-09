package bingo.math.terms.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TermWithSubsTest {
    private static final TermWithSubs zero = new TermWithSubs(new Literal(0));
    private static final TermWithSubs one = new TermWithSubs(new Literal(1));
    private static final TermWithSubs two = new TermWithSubs(new Literal(2));
    private static final TermWithSubs fiveTimesThree =
            new TermWithSubs(new Multiplication(new Literal(5), new Literal(3)));

    @Test
    void getValueShouldDelegate() {
        assertEquals(2, two.getValue());
        assertEquals(15, fiveTimesThree.getValue());
    }

    @Test
    void isLiteralShouldDelegate() {
        assertTrue(two.isLiteral());
        assertFalse(fiveTimesThree.isLiteral());
    }

    @Test
    void getAsStringShouldReturnZeroSubs() {
        assertEquals("0 subs", zero.getAsString());
    }

    @Test
    void getAsStringShouldReturnOneSub() {
        assertEquals("1 sub", one.getAsString());
    }

    @Test
    void getAsStringShouldReturnTwoSubs() {
        assertEquals("2 subs", two.getAsString());
    }
}
