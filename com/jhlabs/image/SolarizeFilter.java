package com.jhlabs.image;

public class SolarizeFilter extends TransferFilter
{
    @Override
    protected float transferFunction(final float v) {
        return (v > 0.5f) ? (2.0f * (v - 0.5f)) : (2.0f * (0.5f - v));
    }
    
    @Override
    public String toString() {
        return "Colors/Solarize";
    }
}
