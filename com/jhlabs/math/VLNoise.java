package com.jhlabs.math;

public class VLNoise implements Function2D
{
    private float distortion;
    
    public VLNoise() {
        this.distortion = 10.0f;
    }
    
    public void setDistortion(final float distortion) {
        this.distortion = distortion;
    }
    
    public float getDistortion() {
        return this.distortion;
    }
    
    public float evaluate(final float x, final float y) {
        final float ox = Noise.noise2(x + 0.5f, y) * this.distortion;
        final float oy = Noise.noise2(x, y + 0.5f) * this.distortion;
        return Noise.noise2(x + ox, y + oy);
    }
}
