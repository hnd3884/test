package org.antlr.v4.runtime.atn;

public class EmptyPredictionContext extends SingletonPredictionContext
{
    public EmptyPredictionContext() {
        super(null, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @Override
    public int size() {
        return 1;
    }
    
    @Override
    public PredictionContext getParent(final int index) {
        return null;
    }
    
    @Override
    public int getReturnState(final int index) {
        return this.returnState;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o;
    }
    
    @Override
    public String toString() {
        return "$";
    }
}
