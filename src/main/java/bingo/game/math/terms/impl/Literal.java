package bingo.game.math.terms.impl;

import bingo.game.math.terms.Term;

public final class Literal implements Term {
    private final double value;
    private final boolean isInteger;

    public Literal(double value) {
        this.value = value;
        this.isInteger = false;
    }

    public Literal(int value) {
        this.value = value;
        this.isInteger = true;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public String getAsString() {
        if (isInteger) {
            return Long.toString(Math.round(value));
        } else {
            return Double.toString(value);
        }
    }
}
