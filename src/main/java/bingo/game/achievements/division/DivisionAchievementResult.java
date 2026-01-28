package bingo.game.achievements.division;

import bingo.game.math.terms.Term;
import bingo.game.math.terms.impl.LabeledTerm;
import bingo.game.math.terms.impl.Literal;
import bingo.game.math.terms.impl.Multiplication;
import bingo.game.math.terms.impl.TermWithPoints;

import java.io.Serial;
import java.io.Serializable;

public record DivisionAchievementResult(DivisionAchievement achievement, int amount) implements Serializable {
    @Serial
    private static final long serialVersionUID = -4862960703741141960L;

    public Term getAsTerm(int numberOfPlayers) {
        Term pointValueTerm = new Literal(achievement.getPointValue(numberOfPlayers));
        Term multiplicationTerm = new Multiplication(new Literal(amount), pointValueTerm);
        return new LabeledTerm(achievement.getDisplayText(), new TermWithPoints(multiplicationTerm));
    }
}
