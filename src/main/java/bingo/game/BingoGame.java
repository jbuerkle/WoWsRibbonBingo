package bingo.game;

import bingo.game.results.BingoResult;
import bingo.game.results.BingoResultBar;
import bingo.restrictions.ShipRestriction;
import bingo.rules.RetryRule;
import bingo.ships.Ship;
import bingo.tokens.TokenCounter;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class BingoGame implements Serializable {
    @Serial
    private static final long serialVersionUID = -6697185137194220209L;
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 7;

    private final TokenCounter tokenCounter;
    private final List<BingoResultBar> resultBars;
    private final List<Ship> shipsUsed;
    private boolean challengeEndedVoluntarily;
    private ShipRestriction shipRestriction;
    private BingoResult bingoResult;
    private List<RetryRule> activeRetryRules;
    private BingoGameState bingoGameState;
    private int currentLevel;

    BingoGame(TokenCounter tokenCounter) {
        this.tokenCounter = tokenCounter;
        this.resultBars = new LinkedList<>();
        this.shipsUsed = new LinkedList<>();
        this.challengeEndedVoluntarily = false;
        for (int level = START_LEVEL - 1; level <= MAX_LEVEL; level++) {
            resultBars.add(new BingoResultBar(level));
        }
        this.bingoGameState = BingoGameState.LEVEL_INITIALIZED;
        this.currentLevel = START_LEVEL;
        resetBingoResult();
    }

    public BingoGame() {
        this(new TokenCounter());
    }

    private void resetBingoResult() {
        bingoResult = null;
        activeRetryRules = new LinkedList<>();
    }

    private boolean bingoResultIsSubmitted() {
        return bingoResult != null;
    }

    private boolean processBingoGameAction(BingoGameAction selectedAction) {
        return switch (bingoGameState) {
            case LEVEL_INITIALIZED -> processActionForLevelInitializedState(selectedAction);
            case UNCONFIRMED_RESULT -> processActionForUnconfirmedResultState(selectedAction);
            case UNCONFIRMED_VOLUNTARY_END -> {
                processActionForUnconfirmedVoluntaryEndState(selectedAction);
                yield true;
            }
            case CHALLENGE_ENDED -> false;
        };
    }

    private boolean processActionForLevelInitializedState(BingoGameAction selectedAction) {
        return switch (selectedAction) {
            case SUBMIT_RESULT -> {
                bingoGameState = BingoGameState.UNCONFIRMED_RESULT;
                yield true;
            }
            case CONFIRM_RESULT -> false;
            case RESET_WITHOUT_CONFIRMING -> true;
            case END_CHALLENGE_VOLUNTARILY -> {
                bingoGameState = BingoGameState.UNCONFIRMED_VOLUNTARY_END;
                yield true;
            }
        };
    }

    private boolean processActionForUnconfirmedResultState(BingoGameAction selectedAction) {
        return switch (selectedAction) {
            case SUBMIT_RESULT -> true;
            case CONFIRM_RESULT -> {
                if (requirementOfCurrentResultBarIsMet()) {
                    if (hasNextLevel()) {
                        bingoGameState = BingoGameState.LEVEL_INITIALIZED;
                        removeShipRestriction();
                        resetBingoResult();
                        currentLevel++;
                    } else {
                        bingoGameState = BingoGameState.CHALLENGE_ENDED;
                    }
                } else {
                    if (retryingIsAllowed()) {
                        bingoGameState = BingoGameState.LEVEL_INITIALIZED;
                        resetBingoResult();
                    } else {
                        bingoGameState = BingoGameState.CHALLENGE_ENDED;
                    }
                }
                tokenCounter.confirmMatchResult();
                yield true;
            }
            case RESET_WITHOUT_CONFIRMING -> {
                bingoGameState = BingoGameState.LEVEL_INITIALIZED;
                yield true;
            }
            case END_CHALLENGE_VOLUNTARILY -> false;
        };
    }

    private void processActionForUnconfirmedVoluntaryEndState(BingoGameAction selectedAction) {
        switch (selectedAction) {
            case SUBMIT_RESULT -> bingoGameState = BingoGameState.UNCONFIRMED_RESULT;
            case CONFIRM_RESULT -> {
                bingoGameState = BingoGameState.CHALLENGE_ENDED;
                challengeEndedVoluntarily = true;
            }
            case RESET_WITHOUT_CONFIRMING -> bingoGameState = BingoGameState.LEVEL_INITIALIZED;
            case END_CHALLENGE_VOLUNTARILY -> {
            }
        }
    }

    public boolean doResetForCurrentLevel() {
        boolean stateChangeSuccessful = processBingoGameAction(BingoGameAction.RESET_WITHOUT_CONFIRMING);
        if (stateChangeSuccessful) {
            resetBingoResult();
            tokenCounter.cancelMatchResult();
        }
        return stateChangeSuccessful;
    }

    public boolean submitBingoResult(BingoResult bingoResult, List<RetryRule> activeRetryRules) {
        boolean stateChangeSuccessful = processBingoGameAction(BingoGameAction.SUBMIT_RESULT);
        if (stateChangeSuccessful) {
            this.bingoResult = bingoResult;
            this.activeRetryRules = activeRetryRules;
            tokenCounter.calculateMatchResult(requirementOfCurrentResultBarIsMet(), hasNextLevel(), activeRetryRules);
        }
        return stateChangeSuccessful;
    }

    public boolean confirmCurrentResult() {
        return processBingoGameAction(BingoGameAction.CONFIRM_RESULT);
    }

    public boolean endChallenge() {
        return processBingoGameAction(BingoGameAction.END_CHALLENGE_VOLUNTARILY);
    }

    private boolean retryingIsAllowed() {
        return !activeRetryRules.isEmpty() || tokenCounter.hasExtraLife();
    }

    private boolean requirementOfCurrentResultBarIsMet() {
        return bingoResultIsSubmitted() && bingoResult.getPointValue() >= getPointRequirementOfLevel(currentLevel);
    }

    private int getPointRequirementOfLevel(int level) {
        return resultBars.get(level).getPointRequirement();
    }

    private String getPointRequirementOfLevelAsString(int level) {
        return "Requirement of level %s: %s points".formatted(level, getPointRequirementOfLevel(level));
    }

    private boolean hasNextLevel() {
        return currentLevel < MAX_LEVEL;
    }

    private boolean shipRestrictionIsSet() {
        return shipRestriction != null;
    }

    public boolean setShipRestriction(ShipRestriction shipRestriction) {
        if (shipRestrictionIsSet()) {
            return false;
        }
        this.shipRestriction = shipRestriction;
        return true;
    }

    public ShipRestriction getShipRestriction() {
        return shipRestriction;
    }

    public void removeShipRestriction() {
        shipRestriction = null;
    }

    public boolean addShipUsed(Ship shipUsed) {
        String nameOfShipUsed = shipUsed.name();
        for (Ship previouslyUsedShip : shipsUsed) {
            if (nameOfShipUsed.equalsIgnoreCase(previouslyUsedShip.name())) {
                return false;
            }
        }
        shipsUsed.add(shipUsed);
        return true;
    }

    public List<Ship> getShipsUsed() {
        return shipsUsed;
    }

    public String getAllResultBarsAndRewardsInTableFormat() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("| Level | Points required | Number of subs as reward: 2^(Level) |\n");
        stringBuilder.append("|---|---:|---:|\n");
        for (BingoResultBar resultBar : resultBars) {
            stringBuilder.append("| %s | %s | 2^%s = %s |\n".formatted(
                    resultBar.level(),
                    resultBar.getPointRequirement(),
                    resultBar.level(),
                    resultBar.getNumberOfSubsAsString()));
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (bingoGameState.equals(BingoGameState.UNCONFIRMED_VOLUNTARY_END) || challengeEndedVoluntarily) {
            appendTextForVoluntaryEndOfChallenge(stringBuilder);
        } else {
            if (bingoResultIsSubmitted()) {
                stringBuilder.append(bingoResult).append(". ");
            }
            stringBuilder.append(getPointRequirementOfLevelAsString(currentLevel));
            if (requirementOfCurrentResultBarIsMet()) {
                appendTextForSuccessfulMatch(stringBuilder);
            } else if (bingoResultIsSubmitted()) {
                appendTextForUnsuccessfulMatch(stringBuilder);
            } else if (shipRestrictionIsSet()) {
                stringBuilder.append(". ").append(shipRestriction.getDisplayText()).append(".");
            }
        }
        appendTextIfBingoGameIsInChallengeEndedState(stringBuilder);
        return stringBuilder.toString();
    }

    private void appendTextForVoluntaryEndOfChallenge(StringBuilder stringBuilder) {
        BingoResultBar previousResultBar = resultBars.get(currentLevel - 1);
        stringBuilder.append("Challenge ended voluntarily on level ")
                .append(currentLevel)
                .append(". Your reward from the previous level: ")
                .append(previousResultBar.getNumberOfSubsAsString());
        appendTextForConversionOfExtraLives(previousResultBar, stringBuilder);
    }

    private void appendTextForSuccessfulMatch(StringBuilder stringBuilder) {
        BingoResultBar currentResultBar = resultBars.get(currentLevel);
        stringBuilder.append(" ✅ Unlocked reward: ").append(currentResultBar.getNumberOfSubsAsString()).append(" ");
        if (hasNextLevel()) {
            stringBuilder.append(tokenCounter).append(" ➡️ ");
            stringBuilder.append(getPointRequirementOfLevelAsString(currentLevel + 1));
        } else {
            stringBuilder.append("This is the highest reward you can get. Congratulations! \uD83C\uDF8A");
            appendTextForConversionOfExtraLives(currentResultBar, stringBuilder);
        }
    }

    private void appendTextForUnsuccessfulMatch(StringBuilder stringBuilder) {
        stringBuilder.append(" ❌ Active retry rules: ");
        if (retryingIsAllowed()) {
            if (activeRetryRules.contains(RetryRule.IMBALANCED_MATCHMAKING)) {
                stringBuilder.append(RetryRule.IMBALANCED_MATCHMAKING.getDisplayText());
            } else if (activeRetryRules.contains(RetryRule.UNFAIR_DISADVANTAGE)) {
                stringBuilder.append(RetryRule.UNFAIR_DISADVANTAGE.getDisplayText());
            } else if (tokenCounter.hasExtraLife()) {
                stringBuilder.append("Extra life (rule 8d)");
            }
            stringBuilder.append(" ✅ ").append(tokenCounter);
        } else {
            stringBuilder.append(
                    "None ❌ The challenge is over and you lose any unlocked rewards. Your reward for participating: ");
            stringBuilder.append(resultBars.getFirst().getNumberOfSubsAsString());
        }
    }

    private void appendTextForConversionOfExtraLives(BingoResultBar resultBar, StringBuilder stringBuilder) {
        if (tokenCounter.hasExtraLife()) {
            int unlockedReward = resultBar.getNumberOfSubsAsReward();
            int extraLives = tokenCounter.getCurrentExtraLives();
            int conversionFactorForExtraLives = 6;
            int totalReward = unlockedReward + extraLives * conversionFactorForExtraLives;
            String calculationAsText =
                    " Total reward: %s + (unused extra lives: %s) * %s = %s subs \uD83C\uDF81".formatted(
                            unlockedReward,
                            extraLives,
                            conversionFactorForExtraLives,
                            totalReward);
            stringBuilder.append(calculationAsText);
        }
    }

    private void appendTextIfBingoGameIsInChallengeEndedState(StringBuilder stringBuilder) {
        if (bingoGameState.equals(BingoGameState.CHALLENGE_ENDED)) {
            stringBuilder.append("\n\nEnd of challenge confirmed. Changes are no longer allowed.");
        }
    }
}
