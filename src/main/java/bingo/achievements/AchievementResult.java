package bingo.achievements;

import bingo.achievements.modifiers.PointValueModifier;
import bingo.ribbons.RibbonResult;
import bingo.ships.MainArmamentType;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public record AchievementResult(Achievement achievement, int amount) {

    public int getPointValue(Set<RibbonResult> ribbonResultSet, MainArmamentType mainArmamentType) {
        int singleAchievementValue = achievement.getFlatPointValue() + achievement.getPointValueModifiers()
                .stream()
                .map(calculatePointsFromPointValueModifier(ribbonResultSet, mainArmamentType))
                .reduce(Integer::sum)
                .orElse(0);
        return singleAchievementValue * amount;
    }

    private Function<PointValueModifier, Integer> calculatePointsFromPointValueModifier(
            Set<RibbonResult> ribbonResultSet, MainArmamentType mainArmamentType) {
        return pointValueModifier -> findMatchingRibbonResult(ribbonResultSet, pointValueModifier).map(
                        calculatePointsFromRibbonResult(mainArmamentType, pointValueModifier))
                .map(Math::round)
                .map(Long::intValue)
                .orElse(0);
    }

    private Function<RibbonResult, Double> calculatePointsFromRibbonResult(
            MainArmamentType mainArmamentType, PointValueModifier pointValueModifier) {
        return ribbonResult -> ribbonResult.getPointValue(mainArmamentType) * pointValueModifier.bonusModifier();
    }

    public String getAsString(Set<RibbonResult> ribbonResultSet, MainArmamentType mainArmamentType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(achievement.getFlatPointValue());
        boolean modifierAdded = false;
        for (PointValueModifier pointValueModifier : achievement.getPointValueModifiers()) {
            Optional<RibbonResult> matchingRibbonResult = findMatchingRibbonResult(ribbonResultSet, pointValueModifier);
            if (matchingRibbonResult.isPresent()) {
                RibbonResult ribbonResult = matchingRibbonResult.get();
                stringBuilder.append(" + ")
                        .append(ribbonResult.getPointValue(mainArmamentType))
                        .append(" * ")
                        .append(pointValueModifier.bonusModifier());
                modifierAdded = true;
            }
        }
        String fullAchievementValueAsString = getFullAchievementValueAsString(stringBuilder.toString(), modifierAdded);
        return achievement.getDisplayText() + ": " + fullAchievementValueAsString + " points";
    }

    private String getFullAchievementValueAsString(String singleAchievementValueAsString, boolean modifierAdded) {
        return amount == 1 ?
                singleAchievementValueAsString :
                modifierAdded ?
                        "%s * (%s)".formatted(amount, singleAchievementValueAsString) :
                        "%s * %s".formatted(amount, singleAchievementValueAsString);
    }

    private Optional<RibbonResult> findMatchingRibbonResult(
            Set<RibbonResult> ribbonResultSet, PointValueModifier pointValueModifier) {
        return ribbonResultSet.stream()
                .filter(ribbonResult -> pointValueModifier.ribbon().equals(ribbonResult.ribbon()))
                .findAny();
    }
}
