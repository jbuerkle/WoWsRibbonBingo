package bingo.tokens.impl;

import bingo.tokens.TokenCounter;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class NonFunctionalTokenCounterTest {
    private final TokenCounter tokenCounter = new NonFunctionalTokenCounter();

    @Test
    void calculateMatchResultShouldNotDoAnything() {
        assertDoesNotThrow(() -> tokenCounter.calculateMatchResult(true, true, Collections.emptyList()));
    }

    @Test
    void confirmMatchResultShouldNotDoAnything() {
        assertDoesNotThrow(tokenCounter::confirmMatchResult);
    }

    @Test
    void cancelMatchResultShouldNotDoAnything() {
        assertDoesNotThrow(tokenCounter::cancelMatchResult);
    }

    @Test
    void hasExtraLifeShouldAlwaysReturnFalse() {
        assertFalse(tokenCounter.hasExtraLife());
    }

    @Test
    void getCurrentExtraLivesShouldAlwaysReturnZero() {
        assertEquals(0, tokenCounter.getCurrentExtraLives());
    }

    @Test
    void toStringShouldAlwaysReturnDummyString() {
        assertEquals("Not a token counter", tokenCounter.toString());
    }
}
