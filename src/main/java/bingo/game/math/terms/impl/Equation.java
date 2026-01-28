package bingo.game.math.terms.impl;

import bingo.game.math.terms.Term;

public final class Equation extends DelegateTerm {

    public Equation(Term term) {
        super(term);
    }

    @Override
    public double getValue() {
        return getRoundedValue();
    }

    @Override
    public String getAsString() {
        String valueAsString = Long.toString(getRoundedValue());
        if (term.isLiteral()) {
            return valueAsString;
        } else {
            return formattedAsEquation(valueAsString);
        }
    }

    private long getRoundedValue() {
        return Math.round(term.getValue());
    }

    private String formattedAsEquation(String valueAsString) {
        return "%s = %s".formatted(term.getAsString(), valueAsString);
    }
}
