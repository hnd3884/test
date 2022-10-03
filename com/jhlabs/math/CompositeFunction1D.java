package com.jhlabs.math;

public class CompositeFunction1D implements Function1D
{
    private Function1D f1;
    private Function1D f2;
    
    public CompositeFunction1D(final Function1D f1, final Function1D f2) {
        this.f1 = f1;
        this.f2 = f2;
    }
    
    public float evaluate(final float v) {
        return this.f1.evaluate(this.f2.evaluate(v));
    }
}
