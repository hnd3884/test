package org.antlr.v4.runtime.atn;

public class SingletonPredictionContext extends PredictionContext
{
    public final PredictionContext parent;
    public final int returnState;
    
    SingletonPredictionContext(final PredictionContext parent, final int returnState) {
        super((parent != null) ? PredictionContext.calculateHashCode(parent, returnState) : PredictionContext.calculateEmptyHashCode());
        assert returnState != -1;
        this.parent = parent;
        this.returnState = returnState;
    }
    
    public static SingletonPredictionContext create(final PredictionContext parent, final int returnState) {
        if (returnState == Integer.MAX_VALUE && parent == null) {
            return SingletonPredictionContext.EMPTY;
        }
        return new SingletonPredictionContext(parent, returnState);
    }
    
    @Override
    public int size() {
        return 1;
    }
    
    @Override
    public PredictionContext getParent(final int index) {
        assert index == 0;
        return this.parent;
    }
    
    @Override
    public int getReturnState(final int index) {
        assert index == 0;
        return this.returnState;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SingletonPredictionContext)) {
            return false;
        }
        if (this.hashCode() != o.hashCode()) {
            return false;
        }
        final SingletonPredictionContext s = (SingletonPredictionContext)o;
        return this.returnState == s.returnState && this.parent != null && this.parent.equals(s.parent);
    }
    
    @Override
    public String toString() {
        final String up = (this.parent != null) ? this.parent.toString() : "";
        if (up.length() != 0) {
            return String.valueOf(this.returnState) + " " + up;
        }
        if (this.returnState == Integer.MAX_VALUE) {
            return "$";
        }
        return String.valueOf(this.returnState);
    }
}
