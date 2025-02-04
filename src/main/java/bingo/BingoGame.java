package bingo;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BingoGame {
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 8;

    private final List<ResultBar> resultBars;
    private Optional<BingoResult> bingoResult;
    private boolean challengeEnded;
    private int currentLevel;

    public BingoGame() {
        this.resultBars = new LinkedList<>();
        for (int level = START_LEVEL; level <= MAX_LEVEL; level++) {
            resultBars.add(new ResultBar(level));
        }
        this.bingoResult = Optional.empty();
        this.currentLevel = START_LEVEL;
        resetChallengeEndedFlag();
    }

    private void resetChallengeEndedFlag() {
        challengeEnded = false;
    }

    public void submitBingoResult(BingoResult bingoResult) {
        this.bingoResult = Optional.ofNullable(bingoResult);
        resetChallengeEndedFlag();
    }

    public void goToNextLevel() {
        if (playerCanGoToNextLevel()) {
            bingoResult = Optional.empty();
            resetChallengeEndedFlag();
            currentLevel++;
        }
    }

    public void endChallenge() {
        if (requirementOfCurrentResultBarIsMet()) {
            challengeEnded = true;
        }
    }

    public boolean playerCanGoToNextLevel() {
        return requirementOfCurrentResultBarIsMet() && hasNextLevel();
    }

    private boolean requirementOfCurrentResultBarIsMet() {
        return bingoResult.isPresent() && bingoResult.get().getPointResult() >= getPointRequirementOfLevel(currentLevel);
    }

    private int getPointRequirementOfLevel(int level) {
        return resultBars.get(level - 1).getPointRequirement();
    }

    private String getPointRequirementOfLevelAsString(int level) {
        return "Requirement of level %s: %s points".formatted(level, getPointRequirementOfLevel(level));
    }

    private String getNumberOfSubsAsStringForLevel(int level) {
        return resultBars.get(level - 1).getNumberOfSubsAsString();
    }

    private boolean hasNextLevel() {
        return currentLevel < MAX_LEVEL;
    }

    public String getAllResultBarsAndRewardsInTableFormat() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("| Level | Points required | Number of subs as reward: 2^(Level-1) |\n");
        stringBuilder.append("|---|---:|---:|\n");
        for (ResultBar resultBar : resultBars) {
            stringBuilder.append("| %s | %s | 2^%s = %s |\n"
                    .formatted(
                            resultBar.level(),
                            resultBar.getPointRequirement(),
                            resultBar.level() - 1,
                            resultBar.getNumberOfSubsAsString()));
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        if (challengeEnded) {
            return "Challenge ended voluntarily on level %s. Your reward: %s".formatted(
                    currentLevel, getNumberOfSubsAsStringForLevel(currentLevel));
        }
        StringBuilder stringBuilder = new StringBuilder();
        bingoResult.ifPresent(result -> stringBuilder.append(result).append(". "));
        stringBuilder.append(getPointRequirementOfLevelAsString(currentLevel));
        if (requirementOfCurrentResultBarIsMet()) {
            stringBuilder.append(", which means your result meets the point requirement, and you unlocked the reward for the current level: ");
            stringBuilder.append(getNumberOfSubsAsStringForLevel(currentLevel));
            if (hasNextLevel()) {
                stringBuilder.append(". You can now choose to end the challenge and receive your reward, or continue to the next level. ");
                stringBuilder.append(getPointRequirementOfLevelAsString(currentLevel + 1));
            } else {
                stringBuilder.append(". This is the highest reward you can get. Congratulations!");
            }
        } else if (bingoResult.isPresent()) {
            stringBuilder.append(", which means your result does not meet the point requirement, and the challenge is over. You lose any unlocked rewards.");
        }
        return stringBuilder.toString();
    }
}
