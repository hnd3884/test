package com.jhlabs.image;

public class GainFilter extends TransferFilter
{
    private float gain;
    private float bias;
    
    public GainFilter() {
        this.gain = 0.5f;
        this.bias = 0.5f;
    }
    
    @Override
    protected float transferFunction(float f) {
        f = ImageMath.gain(f, this.gain);
        f = ImageMath.bias(f, this.bias);
        return f;
    }
    
    public void setGain(final float gain) {
        this.gain = gain;
        this.initialized = false;
    }
    
    public float getGain() {
        return this.gain;
    }
    
    public void setBias(final float bias) {
        this.bias = bias;
        this.initialized = false;
    }
    
    public float getBias() {
        return this.bias;
    }
    
    @Override
    public String toString() {
        return "Colors/Gain...";
    }
}
