package bingo.game.results;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class BingoResultBars implements Serializable {
    @Serial
    private static final long serialVersionUID = 2043542897462699748L;

    private final List<BingoResultBar> resultBars;
    private final double pointRequirementModifier;

    public BingoResultBars(double pointRequirementModifier, int maxLevel) {
        this.resultBars = new LinkedList<>();
        this.pointRequirementModifier = pointRequirementModifier;
        for (int level = 0; level <= maxLevel; level++) {
            resultBars.add(new BingoResultBar(level));
        }
    }

    private BingoResultBar getResultBarOfLevel(int level) {
        return resultBars.get(level);
    }

    public long getPointRequirementOfLevel(int level) {
        int baseRequirement = getResultBarOfLevel(level).getPointRequirement();
        return Math.round(baseRequirement * pointRequirementModifier);
    }

    public int getNumberOfSubsAsRewardForLevel(int level) {
        return getResultBarOfLevel(level).getNumberOfSubsAsReward();
    }

    public String getNumberOfSubsAsStringForLevel(int level) {
        return getResultBarOfLevel(level).getNumberOfSubsAsString();
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
}
