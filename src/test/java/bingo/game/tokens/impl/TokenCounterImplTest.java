package bingo.game.tokens.impl;

import bingo.game.tokens.TokenCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenCounterImplTest {
    private static final String TOKEN_COUNTER_PREFIX = "Token counter: ";
    private static final String NOW_ZERO_TOKENS = "Now 0 tokens 🪙 total.";
    private static final String NOW_ONE_TOKEN = "Now 1 token 🪙 total.";
    private static final String NOW_TWO_TOKENS = "Now 2 tokens 🪙 total.";
    private static final String NOW_ONE_EXTRA_LIFE_AND_ZERO_TOKENS = "Now 1 extra life ❤️ and 0 tokens 🪙 total.";
    private static final String NOW_ONE_EXTRA_LIFE_AND_FIVE_TOKENS = "Now 1 extra life ❤️ and 5 tokens 🪙 total.";
    private static final String NOW_TWO_EXTRA_LIVES_AND_ONE_TOKEN = "Now 2 extra lives ❤️ and 1 token 🪙 total.";
    private static final String ONE_TOKEN_FOR_RULE_9A = "+1 token (successful match)";
    private static final String ONE_TOKEN_FOR_RULE_9B = "+1 token (retrying is allowed). ";
    private static final String MINUS_ONE_EXTRA_LIFE = "-1 extra life 💔 ";

    private TokenCounter tokenCounter;

    @BeforeEach
    void setup() {
        tokenCounter = new TokenCounterImpl();
    }

    @Test
    void initialCounterShouldReturnZeroTokens() {
        assertCounterShows(NOW_ZERO_TOKENS);
        assertExtraLivesAre(0);
    }

    @Test
    void initialCounterShouldNotChangeAfterConfirmation() {
        checkCounterBeforeAndAfterConfirmation();
    }

    @Test
    void counterShouldAddOneTokenForSuccessfulMatch() {
        tokenCounter.calculateMatchResult(true, true, false);
        checkCounterBeforeAndAfterConfirmation(ONE_TOKEN_FOR_RULE_9A.concat(". "), NOW_ONE_TOKEN);
    }

    @Test
    void counterShouldAddOneTokenWhenRetryingIsAllowed() {
        tokenCounter.calculateMatchResult(false, true, true);
        checkCounterBeforeAndAfterConfirmation(ONE_TOKEN_FOR_RULE_9B, NOW_ONE_TOKEN);
    }

    @Test
    void counterShouldAddTwoTokensForSuccessfulMatchWhenRetryingIsAllowed() {
        tokenCounter.calculateMatchResult(true, true, true);
        checkCounterBeforeAndAfterConfirmation(
                ONE_TOKEN_FOR_RULE_9A.concat(", ").concat(ONE_TOKEN_FOR_RULE_9B),
                NOW_TWO_TOKENS);
    }

    @Test
    void counterShouldAddOneTokenForFinalLevelIfNotSuccessful() {
        tokenCounter.calculateMatchResult(false, false, true);
        checkCounterBeforeAndAfterConfirmation(ONE_TOKEN_FOR_RULE_9B, NOW_ONE_TOKEN);
    }

    @Test
    void counterShouldNotAddTokensForFinalLevelIfSuccessful() {
        tokenCounter.calculateMatchResult(true, false, true);
        checkCounterBeforeAndAfterConfirmation();
    }

    @Test
    void counterShouldNotAddTokensIfCancelled() {
        tokenCounter.calculateMatchResult(true, true, true);
        tokenCounter.cancelMatchResult();
        assertCounterShows(NOW_ZERO_TOKENS);
    }

    @Test
    void counterShouldAddOneExtraLife() {
        addTokensUntilCounterIsAt(6);
        assertCounterShows(NOW_ONE_EXTRA_LIFE_AND_ZERO_TOKENS);
        assertExtraLivesAre(1);
    }

    @Test
    void counterShouldAddOneExtraLifeAndFiveTokens() {
        addTokensUntilCounterIsAt(11);
        assertCounterShows(NOW_ONE_EXTRA_LIFE_AND_FIVE_TOKENS);
        assertExtraLivesAre(1);
    }

    @Test
    void counterShouldAddTwoExtraLivesAndOneToken() {
        addTokensUntilCounterIsAt(13);
        assertCounterShows(NOW_TWO_EXTRA_LIVES_AND_ONE_TOKEN);
        assertExtraLivesAre(2);
    }

    @Test
    void counterShouldDeductOneExtraLife() {
        addTokensUntilCounterIsAt(6);
        tokenCounter.calculateMatchResult(false, true, false);
        checkCounterBeforeAndAfterConfirmation(MINUS_ONE_EXTRA_LIFE, NOW_ZERO_TOKENS);
    }

    @Test
    void counterShouldNotDeductTokensWhenThereAreNone() {
        tokenCounter.calculateMatchResult(false, true, false);
        checkCounterBeforeAndAfterConfirmation();
    }

    @Test
    void counterShouldNotDeductTokensWhenSwitchingFromInsufficientResultToSufficientResult() {
        addTokensUntilCounterIsAt(10);
        tokenCounter.calculateMatchResult(false, true, false);
        tokenCounter.calculateMatchResult(true, true, false);
        tokenCounter.confirmMatchResult();
        assertCounterShows(NOW_ONE_EXTRA_LIFE_AND_FIVE_TOKENS);
        assertExtraLivesAre(1);
    }

    @Test
    void counterShouldNotAddTokensWhenSwitchingFromSufficientResultToInsufficientResult() {
        addTokensUntilCounterIsAt(6);
        tokenCounter.calculateMatchResult(true, true, false);
        tokenCounter.calculateMatchResult(false, true, false);
        tokenCounter.confirmMatchResult();
        assertCounterShows(NOW_ZERO_TOKENS);
        assertExtraLivesAre(0);
    }

    private void addTokensUntilCounterIsAt(int numberOfTokens) {
        for (int i = 0; i < numberOfTokens; i++) {
            tokenCounter.calculateMatchResult(false, true, true);
            tokenCounter.confirmMatchResult();
        }
    }

    private void checkCounterBeforeAndAfterConfirmation() {
        assertCounterShows(NOW_ZERO_TOKENS);
        tokenCounter.confirmMatchResult();
        assertCounterShows(NOW_ZERO_TOKENS);
    }

    private void checkCounterBeforeAndAfterConfirmation(String expectedTokenChange, String expectedTokenResult) {
        assertCounterShows(expectedTokenChange.concat(expectedTokenResult));
        tokenCounter.confirmMatchResult();
        assertCounterShows(expectedTokenResult);
    }

    private void assertCounterShows(String expectedString) {
        assertEquals(TOKEN_COUNTER_PREFIX.concat(expectedString), tokenCounter.toString());
    }

    private void assertExtraLivesAre(int expectedNumber) {
        assertEquals(expectedNumber, tokenCounter.getCurrentExtraLives());
        assertEquals(expectedNumber > 0, tokenCounter.hasExtraLife());
    }
}
