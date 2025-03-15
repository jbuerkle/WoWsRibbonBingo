package bingo.game.results;

public record BingoResultBar(int level) {

    public int getPointRequirement() {
        return getPointRequirement(this.level);
    }

    private int getPointRequirement(int currentLevel) {
        final int pointRequirementForCurrentLevel;
        if (currentLevel > 5) {
            pointRequirementForCurrentLevel = 200;
        } else if (currentLevel > 1) {
            pointRequirementForCurrentLevel = 150;
        } else if (currentLevel > 0) {
            pointRequirementForCurrentLevel = 400;
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
