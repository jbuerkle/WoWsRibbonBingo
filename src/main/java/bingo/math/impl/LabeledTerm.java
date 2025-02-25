package bingo.math.impl;

import bingo.math.Term;

public final class LabeledTerm extends ValueDelegateTerm {
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
