package bingo.math.impl;

import bingo.math.Term;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EquationTest {
    private static final Term fifty = new Equation(new Literal(50.0));
    private static final Term fiveTimesThree = new Equation(new Multiplication(new Literal(5), new Literal(3)));

    @Test
    void getValueShouldReturnCorrectValue() {
        assertEquals(50.0, fifty.getValue());
    }

    @Test
    void isLiteralShouldReturnFalse() {
        assertFalse(fifty.isLiteral());
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
