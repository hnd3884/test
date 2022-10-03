package com.jhlabs.math;

public abstract class CompoundFunction2D implements Function2D
{
    protected Function2D basis;
    
    public CompoundFunction2D(final Function2D basis) {
        this.basis = basis;
    }
    
    public void setBasis(final Function2D basis) {
        this.basis = basis;
    }
    
    public Function2D getBasis() {
        return this.basis;
    }
}
