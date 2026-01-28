package bingo.game.achievements;

import bingo.game.achievements.modifiers.PointValueModifier;
import bingo.game.math.terms.Term;
import bingo.game.math.terms.impl.Addition;
import bingo.game.math.terms.impl.LabeledTerm;
import bingo.game.math.terms.impl.Literal;
import bingo.game.math.terms.impl.Multiplication;
import bingo.game.math.terms.impl.TermWithPoints;
import bingo.game.ribbons.RibbonResult;
import bingo.game.ships.MainArmamentType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record AchievementResult(Achievement achievement, int amount) implements Serializable {
    @Serial
    private static final long serialVersionUID = -4822147319230201720L;

    public Term getAsTerm(List<RibbonResult> ribbonResultList, MainArmamentType mainArmamentType) {
        Term flatPointValueTerm = new TermWithPoints(new Literal(achievement.getFlatPointValue()));
        Term singleAchievementValueTerm = achievement.getPointValueModifiers()
                .stream()
                .map(pointValueModifierToTerm(ribbonResultList, mainArmamentType))
                .reduce(Addition::new)
                .map(modifiersTermToFullTerm(flatPointValueTerm))
                .orElse(flatPointValueTerm);
        Term fullAchievementValueTerm = new Multiplication(new Literal(amount), singleAchievementValueTerm);
        return new LabeledTerm(achievement.getDisplayText(), fullAchievementValueTerm);
    }

    private Function<PointValueModifier, Term> pointValueModifierToTerm(
            List<RibbonResult> ribbonResultList, MainArmamentType mainArmamentType) {
        return pointValueModifier -> findMatchingRibbonResult(ribbonResultList, pointValueModifier).map(
                        ribbonResultToTerm(mainArmamentType))
                .map(ribbonResultTermToMultiplicationTerm(pointValueModifier))
                .orElse(new Literal(0));
    }

    private Optional<RibbonResult> findMatchingRibbonResult(
            List<RibbonResult> ribbonResultList, PointValueModifier pointValueModifier) {
        return ribbonResultList.stream()
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
