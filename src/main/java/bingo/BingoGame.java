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
        this.bingoResult = new BingoResult();
        this.currentLevel = START_LEVEL;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void submitBingoResult(BingoResult bingoResult) {
        this.bingoResult = bingoResult;
    }

    public boolean resultBarOfCurrentLevelIsMet() {
        return bingoResult.getPointResult() >= resultBars.get(currentLevel).getPointRequirement();
    }

    public void goToNextLevel() {
        if (resultBarOfCurrentLevelIsMet() && currentLevel < MAX_LEVEL) {
            bingoResult = new BingoResult();
            currentLevel++;
        }
    }
}
