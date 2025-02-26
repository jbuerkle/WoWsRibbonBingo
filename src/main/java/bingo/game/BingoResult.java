package bingo.game;

import bingo.achievements.Achievement;
import bingo.achievements.AchievementResult;
import bingo.achievements.WeightedAchievementResult;
import bingo.points.WeightedPointResult;
import bingo.ribbons.Ribbon;
import bingo.ribbons.RibbonResult;
import bingo.ribbons.WeightedRibbonResult;
import bingo.ships.MainArmamentType;

import java.util.*;
import java.util.function.Function;

public class BingoResult {
    private final MainArmamentType mainArmamentType;
    private final Set<RibbonResult> ribbonResultSet;
    private final Set<AchievementResult> achievementResultSet;

    public BingoResult(MainArmamentType mainArmamentType) {
        this.mainArmamentType = mainArmamentType;
        this.ribbonResultSet = new HashSet<>();
        this.achievementResultSet = new HashSet<>();
    }

    public void addRibbonResult(Ribbon ribbon, int amount) {
        RibbonResult ribbonResult = new RibbonResult(ribbon, amount);
        addResult(ribbonResult, amount, ribbonResultSet, RibbonResult::ribbon);
    }

    public void addAchievementResult(Achievement achievement, int amount) {
        AchievementResult achievementResult = new AchievementResult(achievement, amount);
        addResult(achievementResult, amount, achievementResultSet, AchievementResult::achievement);
    }

    private <T, R> void addResult(T result, int amount, Set<T> resultSet, Function<T, R> keyGetter) {
        removeExistingResultIfPresent(result, resultSet, keyGetter);
        if (amount > 0) {
            resultSet.add(result);
        }
    }

    private <T, R> void removeExistingResultIfPresent(T result, Set<T> resultSet, Function<T, R> keyGetter) {
        R key = keyGetter.apply(result);
        Optional<T> matchingResult =
                resultSet.stream().filter(existingResult -> keyGetter.apply(existingResult).equals(key)).findAny();
        matchingResult.ifPresent(resultSet::remove);
    }

    public int getPointResult() {
        return getCombinedResultSet().stream().map(WeightedPointResult::getPointValue).reduce(Integer::sum).orElse(0);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Ribbon Bingo result: ");
        int resultsAdded = 0;
        for (WeightedPointResult weightedPointResult : getWeightedResultListSortedByPoints()) {
            if (resultsAdded > 0) {
                stringBuilder.append(" + ");
            }
            stringBuilder.append(weightedPointResult.getAsString());
            resultsAdded++;
        }
        if (resultsAdded > 0) {
            stringBuilder.append(" = ");
        }
        stringBuilder.append(getPointResult()).append(" points");
        return stringBuilder.toString();
    }

    private List<WeightedPointResult> getWeightedResultListSortedByPoints() {
        return getCombinedResultSet().stream()
                .sorted(Comparator.comparingInt(WeightedPointResult::getPointValue))
                .toList()
                .reversed();
    }

    private Set<WeightedPointResult> getCombinedResultSet() {
        Set<WeightedPointResult> combinedResultSet = new HashSet<>();
        ribbonResultSet.stream()
                .map(ribbonResult -> new WeightedRibbonResult(ribbonResult, mainArmamentType))
                .forEach(combinedResultSet::add);
        achievementResultSet.stream()
                .map(achievementResult -> new WeightedAchievementResult(
                        achievementResult,
                        mainArmamentType,
                        ribbonResultSet))
                .forEach(combinedResultSet::add);
        return combinedResultSet;
    }
}
