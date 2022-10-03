package com.jhlabs.image;

public class BumpFilter extends ConvolveFilter
{
    private static float[] embossMatrix;
    
    public BumpFilter() {
        super(BumpFilter.embossMatrix);
    }
    
    @Override
    public String toString() {
        return "Blur/Emboss Edges";
    }
    
    static {
        BumpFilter.embossMatrix = new float[] { -1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f };
    }
}
