package bingo.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RetryRuleTest {

    @Test
    void getDisplayTextShouldReturnCorrectText() {
        assertEquals("Imbalanced matchmaking (rule 8a or 8b)", RetryRule.IMBALANCED_MATCHMAKING.getDisplayText());
        assertEquals("Unfair disadvantage (rule 8c)", RetryRule.UNFAIR_DISADVANTAGE.getDisplayText());
    }
}
