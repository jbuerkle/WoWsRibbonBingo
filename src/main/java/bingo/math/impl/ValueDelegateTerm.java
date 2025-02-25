package bingo.math.impl;

import bingo.math.Term;

public abstract class ValueDelegateTerm implements Term {
    protected final Term term;

    protected ValueDelegateTerm(Term term) {
        this.term = term;
    }

    @Override
    public double getValue() {
        return term.getValue();
    }

    @Override
    public boolean isLiteral() {
        return false;
    }
}
