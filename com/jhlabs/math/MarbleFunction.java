package com.jhlabs.math;

public class MarbleFunction extends CompoundFunction2D
{
    public MarbleFunction() {
        super(new TurbulenceFunction(new Noise(), 6.0f));
    }
    
    public MarbleFunction(final Function2D basis) {
        super(basis);
    }
    
    public float evaluate(final float x, final float y) {
        return (float)Math.pow(0.5 * (Math.sin(8.0 * this.basis.evaluate(x, y)) + 1.0), 0.77);
    }
}
