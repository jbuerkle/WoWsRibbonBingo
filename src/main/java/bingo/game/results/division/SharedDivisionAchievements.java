package bingo.game.results.division;

import bingo.achievements.division.DivisionAchievement;
import bingo.achievements.division.DivisionAchievementResult;
import bingo.math.terms.Term;
import bingo.math.terms.impl.Addition;
import bingo.math.terms.impl.Equation;
import bingo.math.terms.impl.LabeledTerm;
import bingo.math.terms.impl.Literal;
import bingo.math.terms.impl.TermWithPoints;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SharedDivisionAchievements implements Serializable {
    @Serial
    private static final long serialVersionUID = -4604976196744982631L;

    private final int numberOfPlayers;
    private final List<DivisionAchievementResult> achievementResultList;

    public SharedDivisionAchievements(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        this.achievementResultList = new LinkedList<>();
    }

    public void addAchievementResult(DivisionAchievement achievement, int amount) {
        DivisionAchievementResult achievementResult = new DivisionAchievementResult(achievement, amount);
        removeExistingResultIfPresent(achievement);
        if (amount > 0) {
            achievementResultList.add(achievementResult);
        }
    }

    private void removeExistingResultIfPresent(DivisionAchievement achievement) {
        Optional<DivisionAchievementResult> matchingResult = achievementResultList.stream()
                .filter(existingResult -> existingResult.achievement().equals(achievement))
                .findAny();
        matchingResult.ifPresent(achievementResultList::remove);
    }

    public long getPointValue() {
        return Math.round(getAsTerm().getValue());
    }

    @Override
    public String toString() {
        return getAsTerm().getAsString();
    }

    private Term getAsTerm() {
        Term calculationTerm = achievementResultList.stream()
                .map(achievementResult -> achievementResult.getAsTerm(numberOfPlayers))
                .sorted(Comparator.comparingDouble(Term::getValue))
                .toList()
                .reversed()
                .stream()
                .reduce(Addition::new)
                .orElse(new Literal(0));
        Term calculationAsEquation = new TermWithPoints(new Equation(calculationTerm));
        return new LabeledTerm("Shared division achievements", calculationAsEquation);
    }
}
