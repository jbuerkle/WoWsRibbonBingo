package bingo.math.impl;

import bingo.math.Term;

public abstract class DelegateTerm implements Term {
    protected final Term term;

    protected DelegateTerm(Term term) {
        this.term = term;
    }

    @Override
    public double getValue() {
        return term.getValue();
    }

    @Override
    public boolean isLiteral() {
        return term.isLiteral();
    }
}
