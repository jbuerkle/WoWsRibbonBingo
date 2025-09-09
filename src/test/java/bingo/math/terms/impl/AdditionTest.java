package bingo.math.terms.impl;

import bingo.math.terms.Term;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdditionTest {
    private static final Term zero = new Literal(0);
    private static final Term three = new Literal(3);
    private static final Term zeroPlusZero = new Addition(zero, zero);
    private static final Term zeroPlusThree = new Addition(new Literal(0.0), three);
    private static final Term threePlusZero = new Addition(three, new Literal(0.0));
    private static final Term threePlusFive = new Addition(three, new Literal(5));
    private static final Term fivePlusThree = new Addition(new Literal(5), three);
    private static final Term fourTimesFour = new Multiplication(new Literal(4), new Literal(4));

    @Test
    void getValueShouldReturnCorrectValue() {
        assertEquals(75, new Addition(new Literal(50), new Literal(25)).getValue());
    }

    @ParameterizedTest
    @MethodSource("isLiteralShouldReturnTrueArgumentProvider")
    void isLiteralShouldReturnTrue(Term left, Term right) {
        assertTrue(new Addition(left, right).isLiteral());
    }

    static Stream<Arguments> isLiteralShouldReturnTrueArgumentProvider() {
        return Stream.of(
                Arguments.of(zero, zero),
                Arguments.of(zero, three),
                Arguments.of(three, zero),
                Arguments.of(zeroPlusZero, zeroPlusZero),
                Arguments.of(zeroPlusThree, zero),
                Arguments.of(zero, threePlusZero));
    }

    @ParameterizedTest
    @MethodSource("isLiteralShouldReturnFalseArgumentProvider")
    void isLiteralShouldReturnFalse(Term left, Term right) {
        assertFalse(new Addition(left, right).isLiteral());
    }

    static Stream<Arguments> isLiteralShouldReturnFalseArgumentProvider() {
        return Stream.of(
                Arguments.of(three, three),
                Arguments.of(zeroPlusThree, zeroPlusThree),
                Arguments.of(threePlusZero, threePlusZero),
                Arguments.of(zero, threePlusFive),
                Arguments.of(fivePlusThree, zero));
    }

    @Test
    void getAsStringShouldReturnRightSideOnly() {
        assertEquals("3", zeroPlusThree.getAsString());
    }

    @Test
    void getAsStringShouldReturnLeftSideOnly() {
        assertEquals("3", threePlusZero.getAsString());
    }

    @Test
    void getAsStringShouldReturnThreePlusThreeWithLiterals() {
        assertEquals("3 + 3", new Addition(three, three).getAsString());
    }

    @Test
    void getAsStringShouldReturnThreePlusThreeWithAdditions() {
        assertEquals("3 + 3", new Addition(threePlusZero, zeroPlusThree).getAsString());
    }

    @Test
    void getAsStringShouldReturnAllSubTermsWithAdditions() {
        assertEquals("3 + 5 + 5 + 3", new Addition(threePlusFive, fivePlusThree).getAsString());
    }

    @Test
    void getAsStringShouldReturnAllSubTermsWithMultiplications() {
        assertEquals("4 * 4 + 4 * 4", new Addition(fourTimesFour, fourTimesFour).getAsString());
    }
}
