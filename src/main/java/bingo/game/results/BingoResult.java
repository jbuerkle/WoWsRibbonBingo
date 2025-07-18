package bingo.game.results;

import bingo.achievements.Achievement;
import bingo.achievements.AchievementResult;
import bingo.math.terms.Term;
import bingo.math.terms.impl.*;
import bingo.ribbons.Ribbon;
import bingo.ribbons.RibbonResult;
import bingo.ships.MainArmamentType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class BingoResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -4747703773714476437L;

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

    public MainArmamentType getMainArmamentType() {
        return mainArmamentType;
    }

    public Set<RibbonResult> getRibbonResultSet() {
        return new HashSet<>(ribbonResultSet);
    }

    public Set<AchievementResult> getAchievementResultSet() {
        return new HashSet<>(achievementResultSet);
    }

    public long getPointValue() {
        return Math.round(getAsTerm().getValue());
    }

    @Override
    public String toString() {
        return getAsTerm().getAsString();
    }

    private Term getAsTerm() {
        Term calculationTerm = getAllResultsCombinedAsTerms().stream()
                .sorted(Comparator.comparingDouble(Term::getValue))
                .toList()
                .reversed()
                .stream()
                .reduce(Addition::new)
                .orElse(new Literal(0));
        Term calculationAsEquation = new TermWithPoints(new Equation(calculationTerm));
        return new LabeledTerm("Ribbon Bingo result", calculationAsEquation);
    }

    private Set<Term> getAllResultsCombinedAsTerms() {
        Set<Term> combinedResults = new HashSet<>();
        ribbonResultSet.stream()
                .map(ribbonResult -> ribbonResult.getAsTerm(mainArmamentType))
                .forEach(combinedResults::add);
        achievementResultSet.stream()
                .map(achievementResult -> achievementResult.getAsTerm(ribbonResultSet, mainArmamentType))
                .forEach(combinedResults::add);
        return combinedResults;
    }
}
