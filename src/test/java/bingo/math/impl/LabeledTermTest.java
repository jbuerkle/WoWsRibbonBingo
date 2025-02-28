package bingo.math.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LabeledTermTest {
    private static final LabeledTerm labeledFifty = new LabeledTerm("Test", new Literal(50));
    private static final LabeledTerm labeledFiveTimesThree =
            new LabeledTerm("Multiplication", new Multiplication(new Literal(5), new Literal(3)));

    @Test
    void getValueShouldDelegate() {
        assertEquals(50, labeledFifty.getValue());
        assertEquals(15, labeledFiveTimesThree.getValue());
    }

    @Test
    void isLiteralShouldDelegate() {
        assertTrue(labeledFifty.isLiteral());
        assertFalse(labeledFiveTimesThree.isLiteral());
    }

    @Test
    void getAsStringShouldReturnLabeledTerm() {
        assertEquals("Test: 50", labeledFifty.getAsString());
    }
}
