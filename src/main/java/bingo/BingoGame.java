package bingo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BingoGame {
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 8;

    private final List<ResultBar> resultBars;
    private final Iterator<ResultBar> resultBarIterator;
    private ResultBar currentResultBar;
    private BingoResult bingoResult;

    public BingoGame() {
        this.resultBars = new LinkedList<>();
        for (int level = START_LEVEL; level <= MAX_LEVEL; level++) {
            resultBars.add(new ResultBar(level));
        }
        this.resultBarIterator = resultBars.iterator();
        this.currentResultBar = resultBarIterator.next();
        this.bingoResult = new BingoResult(false);
    }

    public ResultBar getCurrentResultBar() {
        return currentResultBar;
    }

    public void submitBingoResult(BingoResult bingoResult) {
        this.bingoResult = bingoResult;
    }

    public boolean requirementOfCurrentResultBarIsMet() {
        return bingoResult.getPointResult() >= currentResultBar.getPointRequirement();
    }

    public boolean hasNextLevel() {
        return resultBarIterator.hasNext();
    }

    public void goToNextLevel() {
        if (requirementOfCurrentResultBarIsMet() && hasNextLevel()) {
            currentResultBar = resultBarIterator.next();
            bingoResult = new BingoResult(false);
        }
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
