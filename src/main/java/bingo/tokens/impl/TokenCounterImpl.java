package bingo.tokens.impl;

import bingo.rules.RetryRule;
import bingo.tokens.TokenCounter;

import java.io.Serial;
import java.util.List;

public class TokenCounterImpl implements TokenCounter {
    @Serial
    private static final long serialVersionUID = 2094700030153408780L;
    private static final int TOKENS_NEEDED_FOR_EXTRA_LIFE = 6;
    private static final String SENTENCE_END = ". ";
    private static final String PLUS = "+";
    private static final String MINUS = "-";

    private int currentTokens;
    private int extraLivesLostForUnsuccessfulMatch;
    private int tokensGainedForSuccessfulMatch;
    private int tokensGainedForImbalancedMatch;
    private int tokensAfterMatch;

    public TokenCounterImpl() {
        currentTokens = 0;
        resetMatchTokenCounters();
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
        stringBuilder.append("Token counter: ");
        if (tokensGainedForSuccessfulMatch > 0 && tokensGainedForImbalancedMatch > 0) {
            stringBuilder.append(tokensGainedForSuccessfulMatchAsString())
                    .append(", ")
                    .append(tokensGainedForImbalancedMatchAsString());
        } else if (tokensGainedForSuccessfulMatch > 0) {
            stringBuilder.append(tokensGainedForSuccessfulMatchAsString()).append(SENTENCE_END);
        } else if (tokensGainedForImbalancedMatch > 0) {
            stringBuilder.append(tokensGainedForImbalancedMatchAsString());
        } else if (extraLivesLostForUnsuccessfulMatch > 0) {
            stringBuilder.append(extraLivesLostForUnsuccessfulMatchAsString());
        }
        stringBuilder.append(resultAfterMatchAsString());
        return stringBuilder.toString();
    }

    private String tokensGainedForSuccessfulMatchAsString() {
        return PLUS.concat(getTokensAsString(tokensGainedForSuccessfulMatch)).concat(" (successful match)");
    }

    private String tokensGainedForImbalancedMatchAsString() {
        return PLUS.concat(getTokensAsString(tokensGainedForImbalancedMatch))
                .concat(" (imbalanced matchmaking)")
                .concat(SENTENCE_END);
    }

    private String extraLivesLostForUnsuccessfulMatchAsString() {
        return MINUS.concat(getExtraLivesAsString(extraLivesLostForUnsuccessfulMatch)).concat(" 💔 ");
    }

    private String resultAfterMatchAsString() {
        int extraLives = getExtraLivesAfterMatch();
        int unusedTokens = getUnusedTokensAfterMatch();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Now ");
        if (extraLives > 0) {
            stringBuilder.append(getExtraLivesAsString(extraLives)).append(" ❤️ and ");
        }
        stringBuilder.append(getTokensAsString(unusedTokens)).append(" 🪙 total.");
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
