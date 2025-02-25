package bingo.math.impl;

import bingo.math.Term;
import bingo.math.operator.Operator;

public abstract class TermWithOperator implements Term {
    private final Term left;
    private final Term right;
    private final Operator operator;
    private final double value;

    protected TermWithOperator(Term left, Term right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.value = operator.apply(left.getValue(), right.getValue());
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public boolean isLiteral() {
        return (left.isLiteral() && rightIsIdentity()) || (leftIsIdentity() && right.isLiteral()) ||
                (leftIsIdentity() && rightIsIdentity());
    }

    @Override
    public String getAsString() {
        if (leftIsIdentity()) {
            return right.getAsString();
        }
        if (rightIsIdentity()) {
            return left.getAsString();
        }
        return subTermAsString(left) + operator.getAsString() + subTermAsString(right);
    }

    private boolean leftIsIdentity() {
        return isIdentity(left);
    }

    private boolean rightIsIdentity() {
        return isIdentity(right);
    }

    protected abstract boolean isIdentity(Term term);

    protected abstract String subTermAsString(Term term);
}
