package bingo.game.results;

import java.io.Serial;
import java.io.Serializable;

public record BingoResultBar(int level) implements Serializable {
    @Serial
    private static final long serialVersionUID = 350820194486527459L;

    public int getPointRequirement() {
        return getPointRequirement(this.level);
    }

    private int getPointRequirement(int currentLevel) {
        final int pointRequirementForCurrentLevel;
        if (currentLevel > 4) {
            pointRequirementForCurrentLevel = 300;
        } else if (currentLevel > 1) {
            pointRequirementForCurrentLevel = 200;
        } else if (currentLevel > 0) {
            pointRequirementForCurrentLevel = 300;
        } else {
            return 0;
        }
        return pointRequirementForCurrentLevel + getPointRequirement(currentLevel - 1);
    }

    public int getNumberOfSubsAsReward() {
        return (int) Math.pow(2, this.level);
    }

    public String getNumberOfSubsAsString() {
        int numberOfSubs = getNumberOfSubsAsReward();
        return numberOfSubs + (numberOfSubs == 1 ? " sub" : " subs");
    }
}
