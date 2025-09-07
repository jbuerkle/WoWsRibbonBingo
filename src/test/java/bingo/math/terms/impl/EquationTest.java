package bingo.math.terms.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquationTest {
    private static final Equation fifty = new Equation(new Literal(50.0));
    private static final Equation fiveTimesThree = new Equation(new Multiplication(new Literal(5), new Literal(3)));
    private static final Equation onePointFour = new Equation(new Literal(1.4));
    private static final Equation onePointFive = new Equation(new Literal(1.5));
    private static final Equation onePointSix = new Equation(new Literal(1.6));

    @Test
    void getValueShouldReturnExactValue() {
        assertEquals(50, fifty.getValue());
        assertEquals(15, fiveTimesThree.getValue());
    }

    @Test
    void getValueShouldReturnRoundedValue() {
        assertEquals(1, onePointFour.getValue());
        assertEquals(2, onePointFive.getValue());
        assertEquals(2, onePointSix.getValue());
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
