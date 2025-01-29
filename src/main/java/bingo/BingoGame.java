package bingo;

import java.util.LinkedList;
import java.util.List;

public class BingoGame {
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 8;

    private final List<ResultBar> resultBars;
    private BingoResult bingoResult;
    private int currentLevel;

    public BingoGame() {
        this.resultBars = new LinkedList<>();
        for (int level = START_LEVEL; level <= MAX_LEVEL; level++) {
            resultBars.add(new ResultBar(level));
        }
        this.bingoResult = new BingoResult(false);
        this.currentLevel = START_LEVEL;
    }

    public void submitBingoResult(BingoResult bingoResult) {
        this.bingoResult = bingoResult;
    }

    public void goToNextLevel() {
        if (playerCanGoToNextLevel()) {
            bingoResult = new BingoResult(false);
            currentLevel++;
        }
    }

    public boolean playerCanGoToNextLevel() {
        return requirementOfCurrentResultBarIsMet() && hasNextLevel();
    }

    private boolean requirementOfCurrentResultBarIsMet() {
        return bingoResult.getPointResult() >= resultBars.get(currentLevel).getPointRequirement();
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
}
