package bingo.tokens;

import bingo.rules.RetryRule;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class TokenCounter implements Serializable {
    @Serial
    private static final long serialVersionUID = 2094700030153408780L;
    private static final int TOKENS_NEEDED_FOR_EXTRA_LIFE = 6;
    private static final String YOU_GAIN = "You gain ";
    private static final String YOU_LOSE = "You lose ";
    private static final String SENTENCE_END = ". ";

    private int currentTokens;
    private int extraLivesLostForUnsuccessfulMatch;
    private int tokensGainedForSuccessfulMatch;
    private int tokensGainedForImbalancedMatch;
    private int tokensAfterMatch;

    public TokenCounter() {
        currentTokens = 0;
        resetMatchTokenCounters();
    }

    public TokenCounter(
            int tokensAfterMatch, int tokensGainedForImbalancedMatch, int tokensGainedForSuccessfulMatch,
            int extraLivesLostForUnsuccessfulMatch, int currentTokens) {
        this.tokensAfterMatch = tokensAfterMatch;
        this.tokensGainedForImbalancedMatch = tokensGainedForImbalancedMatch;
        this.tokensGainedForSuccessfulMatch = tokensGainedForSuccessfulMatch;
        this.extraLivesLostForUnsuccessfulMatch = extraLivesLostForUnsuccessfulMatch;
        this.currentTokens = currentTokens;
    }

    public void calculateMatchResult(
            boolean isSuccessfulMatch, boolean hasNextLevel, List<RetryRule> activeRetryRules) {
        resetMatchTokenCounters();
        if (isSuccessfulMatch) {
            if (hasNextLevel) {
                tokensGainedForSuccessfulMatch = 1;
            }
        } else if (retryingIsNotAllowed(activeRetryRules) && hasExtraLife()) {
            extraLivesLostForUnsuccessfulMatch = 1;
        }
        if (activeRetryRules.contains(RetryRule.IMBALANCED_MATCHMAKING) && (hasNextLevel || !isSuccessfulMatch)) {
            tokensGainedForImbalancedMatch = 1;
        }
        tokensAfterMatch = currentTokens + tokensGainedForSuccessfulMatch + tokensGainedForImbalancedMatch -
                extraLivesLostForUnsuccessfulMatch * TOKENS_NEEDED_FOR_EXTRA_LIFE;
    }

    private boolean retryingIsNotAllowed(List<RetryRule> activeRetryRules) {
        return activeRetryRules.isEmpty();
    }

    public void confirmMatchResult() {
        currentTokens = tokensAfterMatch;
        resetMatchTokenCounters();
    }

    public void cancelMatchResult() {
        resetMatchTokenCounters();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (tokensGainedForSuccessfulMatch > 0 && tokensGainedForImbalancedMatch > 0) {
            stringBuilder.append(tokensGainedForSuccessfulMatchAsString())
                    .append(" and ")
                    .append(tokensGainedForImbalancedMatchAsString());
        } else if (tokensGainedForSuccessfulMatch > 0) {
            stringBuilder.append(tokensGainedForSuccessfulMatchAsString()).append(SENTENCE_END);
        } else if (tokensGainedForImbalancedMatch > 0) {
            stringBuilder.append(YOU_GAIN).append(tokensGainedForImbalancedMatchAsString());
        } else if (extraLivesLostForUnsuccessfulMatch > 0) {
            stringBuilder.append(extraLivesLostForUnsuccessfulMatchAsString());
        }
        stringBuilder.append(resultAfterMatchAsString());
        return stringBuilder.toString();
    }

    private String tokensGainedForSuccessfulMatchAsString() {
        return YOU_GAIN.concat(getTokensAsString(tokensGainedForSuccessfulMatch))
                .concat(" for a successful match as per rule 9a");
    }

    private String tokensGainedForImbalancedMatchAsString() {
        return getTokensAsString(tokensGainedForImbalancedMatch).concat(" due to imbalanced matchmaking as per rule 9b")
                .concat(SENTENCE_END);
    }

    private String extraLivesLostForUnsuccessfulMatchAsString() {
        return YOU_LOSE.concat(getExtraLivesAsString(extraLivesLostForUnsuccessfulMatch)).concat(SENTENCE_END);
    }

    private String resultAfterMatchAsString() {
        int extraLives = getExtraLivesAfterMatch();
        int unusedTokens = getUnusedTokensAfterMatch();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You now have ");
        if (extraLives > 0) {
            stringBuilder.append(getExtraLivesAsString(extraLives)).append(" and ");
        }
        stringBuilder.append(getTokensAsString(unusedTokens)).append(".");
        return stringBuilder.toString();
    }

    private String getTokensAsString(int tokens) {
        return tokens + (tokens == 1 ? " token" : " tokens");
    }

    private String getExtraLivesAsString(int extraLives) {
        return extraLives + (extraLives == 1 ? " extra life" : " extra lives");
    }

    public boolean hasExtraLife() {
        return getCurrentExtraLives() > 0;
    }

    public int getCurrentExtraLives() {
        return getExtraLivesForTokens(currentTokens);
    }

    private int getExtraLivesAfterMatch() {
        return getExtraLivesForTokens(tokensAfterMatch);
    }

    private int getExtraLivesForTokens(int tokens) {
        return tokens / TOKENS_NEEDED_FOR_EXTRA_LIFE;
    }

    private int getUnusedTokensAfterMatch() {
        return getUnusedTokens(tokensAfterMatch);
    }

    private int getUnusedTokens(int tokens) {
        return tokens % TOKENS_NEEDED_FOR_EXTRA_LIFE;
    }

    private void resetMatchTokenCounters() {
        extraLivesLostForUnsuccessfulMatch = 0;
        tokensGainedForSuccessfulMatch = 0;
        tokensGainedForImbalancedMatch = 0;
        tokensAfterMatch = currentTokens;
    }
}
