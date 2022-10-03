package com.jhlabs.image;

public class BlurFilter extends ConvolveFilter
{
    protected static float[] blurMatrix;
    
    public BlurFilter() {
        super(BlurFilter.blurMatrix);
    }
    
    @Override
    public String toString() {
        return "Blur/Simple Blur";
    }
    
    static {
        BlurFilter.blurMatrix = new float[] { 0.071428575f, 0.14285715f, 0.071428575f, 0.14285715f, 0.14285715f, 0.14285715f, 0.071428575f, 0.14285715f, 0.071428575f };
    }
}
