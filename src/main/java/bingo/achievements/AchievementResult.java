package bingo.achievements;

import bingo.achievements.modifiers.PointValueModifier;
import bingo.math.terms.Term;
import bingo.math.terms.impl.Addition;
import bingo.math.terms.impl.LabeledTerm;
import bingo.math.terms.impl.Literal;
import bingo.math.terms.impl.Multiplication;
import bingo.math.terms.impl.TermWithPoints;
import bingo.ribbons.RibbonResult;
import bingo.ships.MainArmamentType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public record AchievementResult(Achievement achievement, int amount) implements Serializable {
    @Serial
    private static final long serialVersionUID = -4822147319230201720L;

    public Term getAsTerm(Set<RibbonResult> ribbonResultSet, MainArmamentType mainArmamentType) {
        Term flatPointValueTerm = new TermWithPoints(new Literal(achievement.getFlatPointValue()));
        Term singleAchievementValueTerm = achievement.getPointValueModifiers()
                .stream()
                .map(pointValueModifierToTerm(ribbonResultSet, mainArmamentType))
                .reduce(Addition::new)
                .map(modifiersTermToFullTerm(flatPointValueTerm))
                .orElse(flatPointValueTerm);
        Term fullAchievementValueTerm = new Multiplication(new Literal(amount), singleAchievementValueTerm);
        return new LabeledTerm(achievement.getDisplayText(), fullAchievementValueTerm);
    }

    private Function<PointValueModifier, Term> pointValueModifierToTerm(
            Set<RibbonResult> ribbonResultSet, MainArmamentType mainArmamentType) {
        return pointValueModifier -> findMatchingRibbonResult(ribbonResultSet, pointValueModifier).map(
                        ribbonResultToTerm(mainArmamentType))
                .map(ribbonResultTermToMultiplicationTerm(pointValueModifier))
                .orElse(new Literal(0));
    }

    private Optional<RibbonResult> findMatchingRibbonResult(
            Set<RibbonResult> ribbonResultSet, PointValueModifier pointValueModifier) {
        return ribbonResultSet.stream()
                .filter(ribbonResult -> pointValueModifier.ribbon().equals(ribbonResult.ribbon()))
                .findAny();
    }

    private Function<RibbonResult, Term> ribbonResultToTerm(MainArmamentType mainArmamentType) {
        return ribbonResult -> ribbonResult.getAsTerm(mainArmamentType);
    }

    private Function<Term, Term> ribbonResultTermToMultiplicationTerm(PointValueModifier pointValueModifier) {
        return ribbonResultTerm -> {
            Term bonusModifierTerm = new Literal(pointValueModifier.bonusModifier());
            return new Multiplication(ribbonResultTerm, bonusModifierTerm);
        };
    }

    private Function<Term, Term> modifiersTermToFullTerm(Term flatPointValueTerm) {
        return modifiersTerm -> (Term) new Addition(flatPointValueTerm, modifiersTerm);
    }
}
