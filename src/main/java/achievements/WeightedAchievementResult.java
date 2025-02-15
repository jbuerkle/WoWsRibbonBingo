package achievements;

import bingo.points.WeightedPointResult;
import ribbons.RibbonResult;
import ships.MainArmamentType;

import java.util.Set;

public record WeightedAchievementResult(AchievementResult achievementResult, MainArmamentType mainArmamentType,
        Set<RibbonResult> ribbonResultSet) implements WeightedPointResult {

    @Override
    public int getPointValue() {
        return achievementResult.getPointValue(ribbonResultSet, mainArmamentType);
    }

    @Override
    public String getAsString() {
        return achievementResult.getAsString(ribbonResultSet, mainArmamentType);
    }
}
