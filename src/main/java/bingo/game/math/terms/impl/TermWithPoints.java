package bingo.game.math.terms.impl;

import bingo.game.math.terms.Term;
import bingo.game.text.TextUtility;

public final class TermWithPoints extends DelegateTerm {

    public TermWithPoints(Term term) {
        super(term);
    }

    @Override
    public String getAsString() {
        return term.getAsString() + TextUtility.getSuffixForPoints(term.getValue());
    }
}
