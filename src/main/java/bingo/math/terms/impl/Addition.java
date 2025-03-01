package bingo.math.terms.impl;

import bingo.math.operators.impl.Add;
import bingo.math.terms.Term;

public final class Addition extends TermWithOperator {

    public Addition(Term left, Term right) {
        super(left, right, Add.OPERATOR);
    }

    @Override
    protected boolean isIdentity(Term term) {
        return term.getValue() == 0;
    }

    @Override
    protected String subTermAsString(Term term) {
        return term.getAsString();
    }
}
