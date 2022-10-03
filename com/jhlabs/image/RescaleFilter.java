package com.jhlabs.image;

public class RescaleFilter extends TransferFilter
{
    private float scale;
    
    public RescaleFilter() {
        this.scale = 1.0f;
    }
    
    public RescaleFilter(final float scale) {
        this.scale = 1.0f;
        this.scale = scale;
    }
    
    @Override
    protected float transferFunction(final float v) {
        return v * this.scale;
    }
    
    public void setScale(final float scale) {
        this.scale = scale;
        this.initialized = false;
    }
    
    public float getScale() {
        return this.scale;
    }
    
    @Override
    public String toString() {
        return "Colors/Rescale...";
    }
}
