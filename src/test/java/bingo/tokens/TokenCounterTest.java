package bingo.tokens;

import bingo.rules.RetryRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenCounterTest {
    private static final String YOU_NOW_HAVE_ZERO_TOKENS = "You now have 0 tokens.";
    private static final String YOU_NOW_HAVE_ONE_TOKEN = "You now have 1 token.";
    private static final String YOU_NOW_HAVE_TWO_TOKENS = "You now have 2 tokens.";
    private static final String YOU_NOW_HAVE_ONE_EXTRA_LIFE_AND_ZERO_TOKENS = "You now have 1 extra life and 0 tokens.";

    private List<RetryRule> activeRetryRules;
    private TokenCounter tokenCounter;

    @BeforeEach
    void setup() {
        activeRetryRules = new LinkedList<>();
        tokenCounter = new TokenCounter();
    }

    @Test
    void initialCounterShouldReturnZeroTokens() {
        assertCounterShows(YOU_NOW_HAVE_ZERO_TOKENS);
        assertExtraLivesAre(0);
    }

    @Test
    void initialCounterShouldNotChangeAfterConfirmation() {
        checkCounterBeforeAndAfterConfirmation(YOU_NOW_HAVE_ZERO_TOKENS);
    }

    @Test
    void counterShouldAddOneTokenForSuccessfulMatch() {
        tokenCounter.calculateMatchResult(true, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation("You gain 1 token for a successful match. ", YOU_NOW_HAVE_ONE_TOKEN);
    }

    @Test
    void counterShouldAddOneTokenForImbalancedMatch() {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        tokenCounter.calculateMatchResult(false, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(
                "You gain 1 token due to imbalanced matchmaking (rule 8a or 8b). ",
                YOU_NOW_HAVE_ONE_TOKEN);
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
                "You gain 1 token for a successful match and 1 token due to imbalanced matchmaking (rule 8a or 8b). ",
                YOU_NOW_HAVE_TWO_TOKENS);
    }

    @Test
    void counterShouldAddOneTokenForFinalLevelIfNotSuccessful() {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        tokenCounter.calculateMatchResult(false, false, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(
                "You gain 1 token due to imbalanced matchmaking (rule 8a or 8b). ",
                YOU_NOW_HAVE_ONE_TOKEN);
    }

    @Test
    void counterShouldNotAddTokensForFinalLevelIfSuccessful() {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        tokenCounter.calculateMatchResult(true, false, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(YOU_NOW_HAVE_ZERO_TOKENS);
    }

    @Test
    void counterShouldNotAddTokensIfCancelled() {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        tokenCounter.calculateMatchResult(true, true, activeRetryRules);
        tokenCounter.cancelMatchResult();
        assertCounterShows(YOU_NOW_HAVE_ZERO_TOKENS);
    }

    @Test
    void counterShouldAddOneExtraLife() {
        addTokensUntilCounterIsAt(6);
        assertCounterShows(YOU_NOW_HAVE_ONE_EXTRA_LIFE_AND_ZERO_TOKENS);
        assertExtraLivesAre(1);
    }

    @Test
    void counterShouldAddOneExtraLifeAndFiveTokens() {
        addTokensUntilCounterIsAt(11);
        assertCounterShows("You now have 1 extra life and 5 tokens.");
        assertExtraLivesAre(1);
    }

    @Test
    void counterShouldAddTwoExtraLivesAndOneToken() {
        addTokensUntilCounterIsAt(13);
        assertCounterShows("You now have 2 extra lives and 1 token.");
        assertExtraLivesAre(2);
    }

    @Test
    void counterShouldDeductOneExtraLife() {
        addTokensUntilCounterIsAt(6);
        tokenCounter.calculateMatchResult(false, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation("You lose 1 extra life. ", YOU_NOW_HAVE_ZERO_TOKENS);
    }

    @Test
    void counterShouldNotDeductTokensWhenThereWasAnUnfairDisadvantage() {
        addTokensUntilCounterIsAt(6);
        activeRetryRules.add(RetryRule.UNFAIR_DISADVANTAGE);
        tokenCounter.calculateMatchResult(false, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(YOU_NOW_HAVE_ONE_EXTRA_LIFE_AND_ZERO_TOKENS);
        assertExtraLivesAre(1);
    }

    @Test
    void counterShouldNotDeductTokensWhenThereAreNone() {
        tokenCounter.calculateMatchResult(false, true, activeRetryRules);
        checkCounterBeforeAndAfterConfirmation(YOU_NOW_HAVE_ZERO_TOKENS);
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
        assertEquals(expectedString, tokenCounter.toString());
    }

    private void assertExtraLivesAre(int expectedNumber) {
        assertEquals(expectedNumber, tokenCounter.getCurrentExtraLives());
        assertEquals(expectedNumber > 0, tokenCounter.hasExtraLife());
    }
}
