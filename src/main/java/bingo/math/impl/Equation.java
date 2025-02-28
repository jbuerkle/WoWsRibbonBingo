package bingo.math.impl;

import bingo.math.Term;

public final class Equation extends DelegateTerm {

    public Equation(Term term) {
        super(term);
    }

    @Override
    public String getAsString() {
        String valueAsString = getValueAsString();
        if (term.isLiteral()) {
            return valueAsString;
        } else {
            return formattedAsEquation(valueAsString);
        }
    }

    private String getValueAsString() {
        return Long.toString(Math.round(term.getValue()));
    }

    private String formattedAsEquation(String valueAsString) {
        return "%s = %s".formatted(term.getAsString(), valueAsString);
    }
}
