package bingo.game.results.division;

import bingo.achievements.division.DivisionAchievement;
import bingo.achievements.division.DivisionAchievementResult;
import bingo.math.terms.Term;
import bingo.math.terms.impl.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SharedDivisionAchievements implements Serializable {
    @Serial
    private static final long serialVersionUID = -4604976196744982631L;

    private final int numberOfPlayers;
    private final Set<DivisionAchievementResult> achievementResultSet;

    public SharedDivisionAchievements(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        this.achievementResultSet = new HashSet<>();
    }

    public void addAchievementResult(DivisionAchievement achievement, int amount) {
        DivisionAchievementResult achievementResult = new DivisionAchievementResult(achievement, amount);
        removeExistingResultIfPresent(achievement);
        if (amount > 0) {
            achievementResultSet.add(achievementResult);
        }
    }

    private void removeExistingResultIfPresent(DivisionAchievement achievement) {
        Optional<DivisionAchievementResult> matchingResult = achievementResultSet.stream()
                .filter(existingResult -> existingResult.achievement().equals(achievement))
                .findAny();
        matchingResult.ifPresent(achievementResultSet::remove);
    }

    public long getPointValue() {
        return Math.round(getAsTerm().getValue());
    }

    @Override
    public String toString() {
        return getAsTerm().getAsString();
    }

    private Term getAsTerm() {
        Term calculationTerm = achievementResultSet.stream()
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
