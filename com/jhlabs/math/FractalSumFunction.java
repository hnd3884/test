package com.jhlabs.math;

public class FractalSumFunction extends CompoundFunction2D
{
    private float octaves;
    
    public FractalSumFunction(final Function2D basis) {
        super(basis);
        this.octaves = 1.0f;
    }
    
    public float evaluate(final float x, final float y) {
        float t = 0.0f;
        for (float f = 1.0f; f <= this.octaves; f *= 2.0f) {
            t += this.basis.evaluate(f * x, f * y) / f;
        }
        return t;
    }
}
