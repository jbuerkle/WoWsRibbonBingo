package bingo.math.impl;

import bingo.math.Term;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TermWithPointsTest {
    private static final Term zero = new TermWithPoints(new Literal(0));
    private static final Term one = new TermWithPoints(new Literal(1));
    private static final Term two = new TermWithPoints(new Literal(2));

    @Test
    void getValueShouldReturnCorrectValue() {
        assertEquals(2, two.getValue());
    }

    @Test
    void isLiteralShouldReturnFalse() {
        assertFalse(two.isLiteral());
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
