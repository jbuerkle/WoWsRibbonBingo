package bingo.math.terms.impl;

import bingo.math.terms.Term;
import bingo.text.TextUtility;

public final class TermWithPoints extends DelegateTerm {

    public TermWithPoints(Term term) {
        super(term);
    }

    @Override
    public String getAsString() {
        return term.getAsString() + TextUtility.getSuffixForPoints(term.getValue());
    }
}
