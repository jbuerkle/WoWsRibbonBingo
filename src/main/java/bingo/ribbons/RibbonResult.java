package bingo.ribbons;

import bingo.math.terms.Term;
import bingo.math.terms.impl.LabeledTerm;
import bingo.math.terms.impl.Literal;
import bingo.math.terms.impl.Multiplication;
import bingo.math.terms.impl.TermWithPoints;
import bingo.ships.MainArmamentType;

public record RibbonResult(Ribbon ribbon, int amount) {

    public Term getAsTerm(MainArmamentType mainArmamentType) {
        Term pointValueTerm = new Literal(ribbon.getPointValue(mainArmamentType));
        Term multiplicationTerm = new Multiplication(new Literal(amount), pointValueTerm);
        return new LabeledTerm(ribbon.getDisplayText(), new TermWithPoints(multiplicationTerm));
    }
}
