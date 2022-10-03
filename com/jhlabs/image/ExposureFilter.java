package com.jhlabs.image;

public class ExposureFilter extends TransferFilter
{
    private float exposure;
    
    public ExposureFilter() {
        this.exposure = 1.0f;
    }
    
    @Override
    protected float transferFunction(final float f) {
        return 1.0f - (float)Math.exp(-f * this.exposure);
    }
    
    public void setExposure(final float exposure) {
        this.exposure = exposure;
        this.initialized = false;
    }
    
    public float getExposure() {
        return this.exposure;
    }
    
    @Override
    public String toString() {
        return "Colors/Exposure...";
    }
}
