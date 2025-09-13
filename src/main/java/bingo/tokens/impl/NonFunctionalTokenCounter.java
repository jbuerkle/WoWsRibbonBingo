package bingo.tokens.impl;

import bingo.rules.RetryRule;
import bingo.tokens.TokenCounter;

import java.io.Serial;
import java.util.List;

public class NonFunctionalTokenCounter implements TokenCounter {
    @Serial
    private static final long serialVersionUID = -8370148681960492335L;

    @Override
    public void calculateMatchResult(
            boolean isSuccessfulMatch, boolean hasNextLevel,
            List<RetryRule> activeRetryRules) {

    }

    @Override
    public void confirmMatchResult() {

    }

    @Override
    public void cancelMatchResult() {

    }

    @Override
    public boolean hasExtraLife() {
        return false;
    }

    @Override
    public int getCurrentExtraLives() {
        return 0;
    }

    @Override
    public String toString() {
        return "Not a token counter";
    }
}
