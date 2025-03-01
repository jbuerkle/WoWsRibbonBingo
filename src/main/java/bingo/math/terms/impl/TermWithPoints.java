package bingo.math.terms.impl;

import bingo.math.terms.Term;

public final class TermWithPoints extends DelegateTerm {

    public TermWithPoints(Term term) {
        super(term);
    }

    @Override
    public String getAsString() {
        return term.getAsString() + getPointSuffix(term.getValue());
    }

    private String getPointSuffix(double pointValue) {
        return pointValue == 1 ? " point" : " points";
    }
}
