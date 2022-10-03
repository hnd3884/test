package com.jhlabs.math;

public class TurbulenceFunction extends CompoundFunction2D
{
    private float octaves;
    
    public TurbulenceFunction(final Function2D basis, final float octaves) {
        super(basis);
        this.octaves = octaves;
    }
    
    public void setOctaves(final float octaves) {
        this.octaves = octaves;
    }
    
    public float getOctaves() {
        return this.octaves;
    }
    
    public float evaluate(final float x, final float y) {
        float t = 0.0f;
        for (float f = 1.0f; f <= this.octaves; f *= 2.0f) {
            t += Math.abs(this.basis.evaluate(f * x, f * y)) / f;
        }
        return t;
    }
}
