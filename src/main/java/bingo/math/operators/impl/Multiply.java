package bingo.math.operators.impl;

import bingo.math.operators.Operator;

public final class Multiply implements Operator {
    public static final Operator OPERATOR = new Multiply();

    private Multiply() {
    }

    @Override
    public double apply(double left, double right) {
        return left * right;
    }

    @Override
    public String getAsString() {
        return " * ";
    }
}
