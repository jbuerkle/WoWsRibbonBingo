package bingo.ribbons;

import bingo.math.Term;
import bingo.math.impl.LabeledTerm;
import bingo.math.impl.Literal;
import bingo.math.impl.Multiplication;
import bingo.math.impl.TermWithPoints;
import bingo.ships.MainArmamentType;

public record RibbonResult(Ribbon ribbon, int amount) {

    public Term getAsTerm(MainArmamentType mainArmamentType) {
        Term pointValueTerm = new Literal(ribbon.getPointValue(mainArmamentType));
        Term multiplicationTerm = new Multiplication(new Literal(amount), pointValueTerm);
        return new LabeledTerm(ribbon.getDisplayText(), new TermWithPoints(multiplicationTerm));
    }
}
