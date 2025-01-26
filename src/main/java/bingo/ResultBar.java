package bingo;

public class ResultBar {
    private final int level;

    public ResultBar(int level) {
        this.level = level;
    }

    public int getPointRequirement() {
        return getPointRequirement(this.level);
    }

    private int getPointRequirement(int currentLevel) {
        final int pointRequirementForCurrentLevel;
        if (currentLevel > 6) {
            pointRequirementForCurrentLevel = 100;
        } else if (currentLevel > 2) {
            pointRequirementForCurrentLevel = 150;
        } else if (currentLevel > 0) {
            pointRequirementForCurrentLevel = 200;
        } else {
            return 0;
        }
        return pointRequirementForCurrentLevel + getPointRequirement(currentLevel - 1);
    }

    public int getNumberOfSubsAsReward() {
        return (int) Math.pow(2, this.level - 1);
    }
}
