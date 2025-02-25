package bingo.math.impl;

import bingo.math.Term;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LabeledTermTest {
    private static final Term labeledFifty = new LabeledTerm("Test", new Literal(50));

    @Test
    void getValueShouldReturnCorrectValue() {
        assertEquals(50, labeledFifty.getValue());
    }

    @Test
    void isLiteralShouldReturnFalse() {
        assertFalse(labeledFifty.isLiteral());
    }

    @Test
    void getAsStringShouldReturnLabeledTerm() {
        assertEquals("Test: 50", labeledFifty.getAsString());
    }
}
