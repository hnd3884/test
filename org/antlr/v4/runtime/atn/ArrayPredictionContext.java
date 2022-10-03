package org.antlr.v4.runtime.atn;

import java.util.Arrays;

public class ArrayPredictionContext extends PredictionContext
{
    public final PredictionContext[] parents;
    public final int[] returnStates;
    
    public ArrayPredictionContext(final SingletonPredictionContext a) {
        this(new PredictionContext[] { a.parent }, new int[] { a.returnState });
    }
    
    public ArrayPredictionContext(final PredictionContext[] parents, final int[] returnStates) {
        super(PredictionContext.calculateHashCode(parents, returnStates));
        assert parents != null && parents.length > 0;
        assert returnStates != null && returnStates.length > 0;
        this.parents = parents;
        this.returnStates = returnStates;
    }
    
    @Override
    public boolean isEmpty() {
        return this.returnStates[0] == Integer.MAX_VALUE;
    }
    
    @Override
    public int size() {
        return this.returnStates.length;
    }
    
    @Override
    public PredictionContext getParent(final int index) {
        return this.parents[index];
    }
    
    @Override
    public int getReturnState(final int index) {
        return this.returnStates[index];
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArrayPredictionContext)) {
            return false;
        }
        if (this.hashCode() != o.hashCode()) {
            return false;
        }
        final ArrayPredictionContext a = (ArrayPredictionContext)o;
        return Arrays.equals(this.returnStates, a.returnStates) && Arrays.equals(this.parents, a.parents);
    }
    
    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[]";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append("[");
        for (int i = 0; i < this.returnStates.length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            if (this.returnStates[i] == Integer.MAX_VALUE) {
                buf.append("$");
            }
            else {
                buf.append(this.returnStates[i]);
                if (this.parents[i] != null) {
                    buf.append(' ');
                    buf.append(this.parents[i].toString());
                }
                else {
                    buf.append("null");
                }
            }
        }
        buf.append("]");
        return buf.toString();
    }
}
