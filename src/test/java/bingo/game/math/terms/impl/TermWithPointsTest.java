package bingo.game.math.terms.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TermWithPointsTest {
    private static final TermWithPoints zero = new TermWithPoints(new Literal(0));
    private static final TermWithPoints one = new TermWithPoints(new Literal(1));
    private static final TermWithPoints two = new TermWithPoints(new Literal(2));
    private static final TermWithPoints fiveTimesThree =
            new TermWithPoints(new Multiplication(new Literal(5), new Literal(3)));

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
    void getAsStringShouldReturnZeroPoints() {
        assertEquals("0 points", zero.getAsString());
    }

    @Test
    void getAsStringShouldReturnOnePoint() {
        assertEquals("1 point", one.getAsString());
    }

    @Test
    void getAsStringShouldReturnTwoPoints() {
        assertEquals("2 points", two.getAsString());
    }
}
