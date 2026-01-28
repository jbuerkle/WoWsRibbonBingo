package bingo.game.tokens;

import bingo.game.rules.RetryRule;

import java.io.Serializable;
import java.util.List;

public interface TokenCounter extends Serializable {

    void calculateMatchResult(boolean isSuccessfulMatch, boolean hasNextLevel, List<RetryRule> activeRetryRules);

    void confirmMatchResult();

    void cancelMatchResult();

    boolean hasExtraLife();

    int getCurrentExtraLives();
}
