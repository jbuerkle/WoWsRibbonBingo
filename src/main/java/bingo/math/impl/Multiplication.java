package bingo.math.impl;

import bingo.math.Term;
import bingo.math.operator.impl.Multiply;

public final class Multiplication extends TermWithOperator {

    public Multiplication(Term left, Term right) {
        super(left, right, Multiply.OPERATOR);
    }

    @Override
    protected boolean isIdentity(Term term) {
        return term.getValue() == 1;
    }

    @Override
    protected String subTermAsString(Term term) {
        String termAsString = term.getAsString();
        if (term.isLiteral() || term instanceof Multiplication) {
            return termAsString;
        } else {
            return parenthesize(termAsString);
        }
    }

    private String parenthesize(String termAsString) {
        return "(%s)".formatted(termAsString);
    }
}
