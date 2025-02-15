package ribbons;

import bingo.points.WeightedPointResult;
import ships.MainArmamentType;

public record WeightedRibbonResult(RibbonResult ribbonResult, MainArmamentType mainArmamentType)
        implements WeightedPointResult {

    @Override
    public int getPointValue() {
        return ribbonResult.getPointValue(mainArmamentType);
    }

    @Override
    public String getAsString() {
        return ribbonResult.getAsString(mainArmamentType);
    }
}
