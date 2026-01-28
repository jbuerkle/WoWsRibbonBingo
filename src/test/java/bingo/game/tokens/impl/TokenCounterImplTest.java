package bingo.game.tokens.impl;

import bingo.game.rules.RetryRule;
import bingo.game.tokens.TokenCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

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
    private static final String ONE_TOKEN_FOR_RULE_9B = "+1 token (imbalanced matchmaking). ";
    private static final String MINUS_ONE_EXTRA_LIFE = "-1 extra life 💔 ";

    private List<RetryRule> activeRetryRules;
    private TokenCounter tokenCounter;

    @BeforeEach
    void setup() {
        activeRetryRules = new LinkedList<>();
        tokenCounter = new TokenCounterImpl();
    }

    @Test
    void initialCounterShouldReturnZeroTokens() {
        assertCounterShows(NOW_ZERO_TOKENS);
        assertExtraLivesAre(0);
    }

    @Test
    void initialCounterShouldNotChangeAfterConfirmation() {
        checkCounterBeforeAndAfterConfirmation(NOW_ZERO_TOKENS);
    }

    @Test
    void counterShouldAddOneTokenForSuccessfulMatch() {
        tokenCounter.calculateMatchResult(true, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(ONE_TOKEN_FOR_RULE_9A.concat(". "), NOW_ONE_TOKEN);
    }

    @Test
    void counterShouldAddOneTokenForImbalancedMatch() {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        tokenCounter.calculateMatchResult(false, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(ONE_TOKEN_FOR_RULE_9B, NOW_ONE_TOKEN);
    }

    @Test
    void counterShouldAddOneTokenForImbalancedMatchAndIgnoreUnfairDisadvantage() {
        activeRetryRules.add(RetryRule.UNFAIR_DISADVANTAGE);
        counterShouldAddOneTokenForImbalancedMatch();
    }

    @Test
    void counterShouldAddTwoTokensForImbalancedButSuccessfulMatch() {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        tokenCounter.calculateMatchResult(true, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(
                ONE_TOKEN_FOR_RULE_9A.concat(", ").concat(ONE_TOKEN_FOR_RULE_9B),
                NOW_TWO_TOKENS);
    }

    @Test
    void counterShouldAddOneTokenForFinalLevelIfNotSuccessful() {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        tokenCounter.calculateMatchResult(false, false, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(ONE_TOKEN_FOR_RULE_9B, NOW_ONE_TOKEN);
    }

    @Test
    void counterShouldNotAddTokensForFinalLevelIfSuccessful() {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        tokenCounter.calculateMatchResult(true, false, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(NOW_ZERO_TOKENS);
    }

    @Test
    void counterShouldNotAddTokensIfCancelled() {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        tokenCounter.calculateMatchResult(true, true, activeRetryRules);
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
        tokenCounter.calculateMatchResult(false, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(MINUS_ONE_EXTRA_LIFE, NOW_ZERO_TOKENS);
    }

    @Test
    void counterShouldNotDeductTokensWhenThereWasAnUnfairDisadvantage() {
        addTokensUntilCounterIsAt(6);
        activeRetryRules.add(RetryRule.UNFAIR_DISADVANTAGE);
        tokenCounter.calculateMatchResult(false, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(NOW_ONE_EXTRA_LIFE_AND_ZERO_TOKENS);
        assertExtraLivesAre(1);
    }

    @Test
    void counterShouldNotDeductTokensWhenThereAreNone() {
        tokenCounter.calculateMatchResult(false, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(NOW_ZERO_TOKENS);
    }

    @Test
    void counterShouldNotDeductTokensWhenSwitchingFromInsufficientResultToSufficientResult() {
        addTokensUntilCounterIsAt(10);
        tokenCounter.calculateMatchResult(false, true, activeRetryRules);
        tokenCounter.calculateMatchResult(true, true, activeRetryRules);
        tokenCounter.confirmMatchResult();
        assertCounterShows(NOW_ONE_EXTRA_LIFE_AND_FIVE_TOKENS);
        assertExtraLivesAre(1);
    }

    @Test
    void counterShouldNotAddTokensWhenSwitchingFromSufficientResultToInsufficientResult() {
        addTokensUntilCounterIsAt(6);
        tokenCounter.calculateMatchResult(true, true, activeRetryRules);
        tokenCounter.calculateMatchResult(false, true, activeRetryRules);
        tokenCounter.confirmMatchResult();
        assertCounterShows(NOW_ZERO_TOKENS);
        assertExtraLivesAre(0);
    }

    private void addTokensUntilCounterIsAt(int numberOfTokens) {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        for (int i = 0; i < numberOfTokens; i++) {
            tokenCounter.calculateMatchResult(false, true, activeRetryRules);
            tokenCounter.confirmMatchResult();
        }
        activeRetryRules.remove(RetryRule.IMBALANCED_MATCHMAKING);
    }

    private void checkCounterBeforeAndAfterConfirmation(String expectedTokenResult) {
        assertCounterShows(expectedTokenResult);
        tokenCounter.confirmMatchResult();
        assertCounterShows(expectedTokenResult);
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
