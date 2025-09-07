package bingo.math.terms.impl;

import bingo.math.terms.Term;
import bingo.text.TextUtility;

public final class TermWithSubs extends DelegateTerm {

    public TermWithSubs(Term term) {
        super(term);
    }

    @Override
    public String getAsString() {
        return term.getAsString() + TextUtility.getSuffixForSubs(term.getValue());
    }
}
