package bingo.game.results;

import bingo.game.achievements.Achievement;
import bingo.game.achievements.AchievementResult;
import bingo.game.math.terms.Term;
import bingo.game.math.terms.impl.Addition;
import bingo.game.math.terms.impl.Equation;
import bingo.game.math.terms.impl.LabeledTerm;
import bingo.game.math.terms.impl.Literal;
import bingo.game.math.terms.impl.TermWithPoints;
import bingo.game.ribbons.Ribbon;
import bingo.game.ribbons.RibbonResult;
import bingo.game.ships.MainArmamentType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BingoResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -4747703773714476437L;

    private final MainArmamentType mainArmamentType;
    private final List<RibbonResult> ribbonResultList;
    private final List<AchievementResult> achievementResultList;

    public BingoResult(MainArmamentType mainArmamentType) {
        this.mainArmamentType = mainArmamentType;
        this.ribbonResultList = new LinkedList<>();
        this.achievementResultList = new LinkedList<>();
    }

    public void addRibbonResult(Ribbon ribbon, int amount) {
        RibbonResult ribbonResult = new RibbonResult(ribbon, amount);
        addResult(ribbonResult, amount, ribbonResultList, RibbonResult::ribbon);
    }

    public void addAchievementResult(Achievement achievement, int amount) {
        AchievementResult achievementResult = new AchievementResult(achievement, amount);
        addResult(achievementResult, amount, achievementResultList, AchievementResult::achievement);
    }

    private <T, R> void addResult(T result, int amount, List<T> resultList, Function<T, R> keyGetter) {
        removeExistingResultIfPresent(result, resultList, keyGetter);
        if (amount > 0) {
            resultList.add(result);
        }
    }

    private <T, R> void removeExistingResultIfPresent(T result, List<T> resultList, Function<T, R> keyGetter) {
        R key = keyGetter.apply(result);
        Optional<T> matchingResult =
                resultList.stream().filter(existingResult -> keyGetter.apply(existingResult).equals(key)).findAny();
        matchingResult.ifPresent(resultList::remove);
    }

    public MainArmamentType getMainArmamentType() {
        return mainArmamentType;
    }

    public List<RibbonResult> getRibbonResultList() {
        return new LinkedList<>(ribbonResultList);
    }

    public List<AchievementResult> getAchievementResultList() {
        return new LinkedList<>(achievementResultList);
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

    private List<Term> getAllResultsCombinedAsTerms() {
        List<Term> combinedResults = new LinkedList<>();
        ribbonResultList.stream()
                .map(ribbonResult -> ribbonResult.getAsTerm(mainArmamentType))
                .forEach(combinedResults::add);
        achievementResultList.stream()
                .map(achievementResult -> achievementResult.getAsTerm(ribbonResultList, mainArmamentType))
                .forEach(combinedResults::add);
        return combinedResults;
    }
}
