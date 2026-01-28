package bingo.game.math.terms.impl;

import bingo.game.math.operators.impl.Add;
import bingo.game.math.terms.Term;

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
