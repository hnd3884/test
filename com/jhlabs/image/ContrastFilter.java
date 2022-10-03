package com.jhlabs.image;

public class ContrastFilter extends TransferFilter
{
    private float brightness;
    private float contrast;
    
    public ContrastFilter() {
        this.brightness = 1.0f;
        this.contrast = 1.0f;
    }
    
    @Override
    protected float transferFunction(float f) {
        f *= this.brightness;
        f = (f - 0.5f) * this.contrast + 0.5f;
        return f;
    }
    
    public void setBrightness(final float brightness) {
        this.brightness = brightness;
        this.initialized = false;
    }
    
    public float getBrightness() {
        return this.brightness;
    }
    
    public void setContrast(final float contrast) {
        this.contrast = contrast;
        this.initialized = false;
    }
    
    public float getContrast() {
        return this.contrast;
    }
    
    @Override
    public String toString() {
        return "Colors/Contrast...";
    }
}
