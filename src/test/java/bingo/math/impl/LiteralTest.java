package bingo.math.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LiteralTest {

    @Test
    void getValueShouldReturnCorrectValue() {
        assertEquals(0.2, new Literal(0.2).getValue());
    }

    @Test
    void isLiteralShouldReturnTrue() {
        assertTrue(new Literal(0.2).isLiteral());
    }

    @Test
    void getAsStringShouldReturnInteger() {
        assertEquals("1", new Literal(1).getAsString());
    }

    @Test
    void getAsStringShouldReturnDouble() {
        assertEquals("1.0", new Literal(1.0).getAsString());
    }
}
