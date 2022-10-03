package com.jhlabs.math;

public class FBM implements Function2D
{
    protected float[] exponents;
    protected float H;
    protected float lacunarity;
    protected float octaves;
    protected Function2D basis;
    
    public FBM(final float H, final float lacunarity, final float octaves) {
        this(H, lacunarity, octaves, new Noise());
    }
    
    public FBM(final float H, final float lacunarity, final float octaves, final Function2D basis) {
        this.H = H;
        this.lacunarity = lacunarity;
        this.octaves = octaves;
        this.basis = basis;
        this.exponents = new float[(int)octaves + 1];
        float frequency = 1.0f;
        for (int i = 0; i <= (int)octaves; ++i) {
            this.exponents[i] = (float)Math.pow(frequency, -H);
            frequency *= lacunarity;
        }
    }
    
    public void setBasis(final Function2D basis) {
        this.basis = basis;
    }
    
    public Function2D getBasisType() {
        return this.basis;
    }
    
    public float evaluate(float x, float y) {
        float value = 0.0f;
        x += 371.0f;
        y += 529.0f;
        int i;
        for (i = 0; i < (int)this.octaves; ++i) {
            value += this.basis.evaluate(x, y) * this.exponents[i];
            x *= this.lacunarity;
            y *= this.lacunarity;
        }
        final float remainder = this.octaves - (int)this.octaves;
        if (remainder != 0.0f) {
            value += remainder * this.basis.evaluate(x, y) * this.exponents[i];
        }
        return value;
    }
}
