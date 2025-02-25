package bingo.math.impl;

import bingo.math.Term;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MultiplicationTest {
    private static final Term one = new Literal(1);
    private static final Term three = new Literal(3);
    private static final Term oneTimesOne = new Multiplication(one, one);
    private static final Term oneTimesThree = new Multiplication(new Literal(1.0), three);
    private static final Term threeTimesOne = new Multiplication(three, new Literal(1.0));
    private static final Term threeTimesFive = new Multiplication(three, new Literal(5));
    private static final Term fiveTimesThree = new Multiplication(new Literal(5), three);
    private static final Term twoPlusTwo = new Addition(new Literal(2), new Literal(2));

    @Test
    void getValueShouldReturnCorrectValue() {
        assertEquals(60, new Multiplication(new Literal(300), new Literal(0.2)).getValue());
    }

    @ParameterizedTest
    @MethodSource("isLiteralShouldReturnTrueArgumentProvider")
    void isLiteralShouldReturnTrue(Term left, Term right) {
        assertTrue(new Multiplication(left, right).isLiteral());
    }

    static Stream<Arguments> isLiteralShouldReturnTrueArgumentProvider() {
        return Stream.of(
                Arguments.of(one, one),
                Arguments.of(one, three),
                Arguments.of(three, one),
                Arguments.of(oneTimesOne, oneTimesOne),
                Arguments.of(oneTimesThree, one),
                Arguments.of(one, threeTimesOne));
    }

    @ParameterizedTest
    @MethodSource("isLiteralShouldReturnFalseArgumentProvider")
    void isLiteralShouldReturnFalse(Term left, Term right) {
        assertFalse(new Multiplication(left, right).isLiteral());
    }

    static Stream<Arguments> isLiteralShouldReturnFalseArgumentProvider() {
        return Stream.of(
                Arguments.of(three, three),
                Arguments.of(oneTimesThree, oneTimesThree),
                Arguments.of(threeTimesOne, threeTimesOne),
                Arguments.of(one, threeTimesFive),
                Arguments.of(fiveTimesThree, one));
    }

    @Test
    void getAsStringShouldReturnRightSideOnly() {
        assertEquals("3", oneTimesThree.getAsString());
    }

    @Test
    void getAsStringShouldReturnLeftSideOnly() {
        assertEquals("3", threeTimesOne.getAsString());
    }

    @Test
    void getAsStringShouldReturnThreeTimesThreeWithLiterals() {
        assertEquals("3 * 3", new Multiplication(three, three).getAsString());
    }

    @Test
    void getAsStringShouldReturnThreeTimesThreeWithMultiplications() {
        assertEquals("3 * 3", new Multiplication(threeTimesOne, oneTimesThree).getAsString());
    }

    @Test
    void getAsStringShouldReturnRightSideInParenthesis() {
        assertEquals("3 * (2 + 2)", new Multiplication(three, twoPlusTwo).getAsString());
    }

    @Test
    void getAsStringShouldReturnLeftSideInParenthesis() {
        assertEquals("(2 + 2) * 3", new Multiplication(twoPlusTwo, three).getAsString());
    }

    @Test
    void getAsStringShouldReturnBothSidesInParenthesis() {
        assertEquals("(2 + 2) * (2 + 2)", new Multiplication(twoPlusTwo, twoPlusTwo).getAsString());
    }

    @Test
    void getAsStringShouldReturnNeitherSideInParenthesis() {
        assertEquals("3 * 5 * 5 * 3", new Multiplication(threeTimesFive, fiveTimesThree).getAsString());
    }
}
