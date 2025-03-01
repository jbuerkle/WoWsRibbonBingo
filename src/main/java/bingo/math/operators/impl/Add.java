package bingo.math.operators.impl;

import bingo.math.operators.Operator;

public final class Add implements Operator {
    public static final Operator OPERATOR = new Add();

    private Add() {
    }

    @Override
    public double apply(double left, double right) {
        return left + right;
    }

    @Override
    public String getAsString() {
        return " + ";
    }
}
