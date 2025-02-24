package bingo.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BingoGame {
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 7;

    private final List<ResultBar> resultBars;
    private Optional<BingoResult> bingoResult;
    private boolean challengeEnded;
    private int currentLevel;

    public BingoGame() {
        this.resultBars = new LinkedList<>();
        for (int level = START_LEVEL - 1; level <= MAX_LEVEL; level++) {
            resultBars.add(new ResultBar(level));
        }
        this.currentLevel = START_LEVEL;
        doResetForCurrentLevel();
    }

    private void resetChallengeEndedFlag() {
        challengeEnded = false;
    }

    private void resetBingoResult() {
        bingoResult = Optional.empty();
    }

    public void doResetForCurrentLevel() {
        resetChallengeEndedFlag();
        resetBingoResult();
    }

    public void submitBingoResult(BingoResult bingoResult) {
        this.bingoResult = Optional.ofNullable(bingoResult);
        resetChallengeEndedFlag();
    }

    public void goToNextLevel() {
        if (playerCanGoToNextLevel()) {
            doResetForCurrentLevel();
            currentLevel++;
        }
    }

    public void endChallenge() {
        if (playerCanGoToNextLevel()) {
            challengeEnded = true;
        }
    }

    public boolean playerCanGoToNextLevel() {
        return requirementOfCurrentResultBarIsMet() && hasNextLevel();
    }

    private boolean requirementOfCurrentResultBarIsMet() {
        return bingoResult.isPresent() &&
                bingoResult.get().getPointResult() >= getPointRequirementOfLevel(currentLevel);
    }

    private int getPointRequirementOfLevel(int level) {
        return resultBars.get(level).getPointRequirement();
    }

    private String getPointRequirementOfLevelAsString(int level) {
        return "Requirement of level %s: %s points".formatted(level, getPointRequirementOfLevel(level));
    }

    private String getNumberOfSubsAsStringForLevel(int level) {
        return resultBars.get(level).getNumberOfSubsAsString();
    }

    private boolean hasNextLevel() {
        return currentLevel < MAX_LEVEL;
    }

    public String getAllResultBarsAndRewardsInTableFormat() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("| Level | Points required | Number of subs as reward: 2^(Level) |\n");
        stringBuilder.append("|---|---:|---:|\n");
        for (ResultBar resultBar : resultBars) {
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
        if (challengeEnded) {
            return "Challenge ended voluntarily on level %s. Your reward: %s".formatted(
                    currentLevel,
                    getNumberOfSubsAsStringForLevel(
                            currentLevel));
        }
        StringBuilder stringBuilder = new StringBuilder();
        bingoResult.ifPresent(result -> stringBuilder.append(result).append(". "));
        stringBuilder.append(getPointRequirementOfLevelAsString(currentLevel));
        if (requirementOfCurrentResultBarIsMet()) {
            stringBuilder.append(
                    ", which means your result meets the point requirement, and you unlocked the reward for the current level: ");
            stringBuilder.append(getNumberOfSubsAsStringForLevel(currentLevel));
            if (hasNextLevel()) {
                stringBuilder.append(
                        ". You can now choose to end the challenge and receive your reward, or continue to the next level. ");
                stringBuilder.append(getPointRequirementOfLevelAsString(currentLevel + 1));
            } else {
                stringBuilder.append(". This is the highest reward you can get. Congratulations!");
            }
        } else if (bingoResult.isPresent()) {
            stringBuilder.append(
                    ", which means your result does not meet the point requirement, and the challenge is over. You lose any unlocked rewards. Your reward for participating: ");
            stringBuilder.append(getNumberOfSubsAsStringForLevel(0));
        }
        return stringBuilder.toString();
    }
}
