package com.jhlabs.image;

public class SharpenFilter extends ConvolveFilter
{
    private static float[] sharpenMatrix;
    
    public SharpenFilter() {
        super(SharpenFilter.sharpenMatrix);
    }
    
    @Override
    public String toString() {
        return "Blur/Sharpen";
    }
    
    static {
        SharpenFilter.sharpenMatrix = new float[] { 0.0f, -0.2f, 0.0f, -0.2f, 1.8f, -0.2f, 0.0f, -0.2f, 0.0f };
    }
}
