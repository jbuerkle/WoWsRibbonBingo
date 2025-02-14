package bingo;

public record ResultBar(int level) {

    public int getPointRequirement() {
        return getPointRequirement(this.level);
    }

    private int getPointRequirement(int currentLevel) {
        final int pointRequirementForCurrentLevel;
        if (currentLevel > 4) {
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

    public String getNumberOfSubsAsString() {
        int numberOfSubs = getNumberOfSubsAsReward();
        return numberOfSubs + (numberOfSubs == 1 ? " sub" : " subs");
    }
}
