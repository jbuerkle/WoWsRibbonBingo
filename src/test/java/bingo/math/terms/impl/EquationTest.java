package bingo.math.terms.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquationTest {
    private static final Equation fifty = new Equation(new Literal(50.0));
    private static final Equation fiveTimesThree = new Equation(new Multiplication(new Literal(5), new Literal(3)));

    @Test
    void getValueShouldDelegate() {
        assertEquals(50, fifty.getValue());
        assertEquals(15, fiveTimesThree.getValue());
    }

    @Test
    void isLiteralShouldDelegate() {
        assertTrue(fifty.isLiteral());
        assertFalse(fiveTimesThree.isLiteral());
    }

    @Test
    void getAsStringShouldReturnLiteral() {
        assertEquals("50", fifty.getAsString());
    }

    @Test
    void getAsStringShouldReturnEquation() {
        assertEquals("5 * 3 = 15", fiveTimesThree.getAsString());
    }
}
