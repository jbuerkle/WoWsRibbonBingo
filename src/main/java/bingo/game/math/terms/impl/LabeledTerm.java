package bingo.game.math.terms.impl;

import bingo.game.math.terms.Term;

public final class LabeledTerm extends DelegateTerm {
    private final String label;

    public LabeledTerm(String label, Term term) {
        super(term);
        this.label = label;
    }

    @Override
    public String getAsString() {
        return label + ": " + term.getAsString();
    }
}
