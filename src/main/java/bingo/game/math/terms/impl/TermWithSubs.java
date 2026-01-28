package bingo.game.math.terms.impl;

import bingo.game.math.terms.Term;
import bingo.game.text.TextUtility;

public final class TermWithSubs extends DelegateTerm {

    public TermWithSubs(Term term) {
        super(term);
    }

    @Override
    public String getAsString() {
        return term.getAsString() + TextUtility.getSuffixForSubs(term.getValue());
    }
}
