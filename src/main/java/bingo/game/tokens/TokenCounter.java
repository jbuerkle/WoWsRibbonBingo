package bingo.game.tokens;

import java.io.Serializable;

public interface TokenCounter extends Serializable {

    void calculateMatchResult(boolean isSuccessfulMatch, boolean hasNextLevel, boolean retryingIsAllowed);

    void confirmMatchResult();

    void cancelMatchResult();

    boolean hasExtraLife();

    int getCurrentExtraLives();
}
