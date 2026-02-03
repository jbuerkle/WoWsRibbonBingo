package bingo.game.tokens.impl;

import bingo.game.tokens.TokenCounter;

import java.io.Serial;

public class NonFunctionalTokenCounter implements TokenCounter {
    @Serial
    private static final long serialVersionUID = -8370148681960492335L;

    @Override
    public void calculateMatchResult(boolean isSuccessfulMatch, boolean hasNextLevel, boolean retryingIsAllowed) {

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
