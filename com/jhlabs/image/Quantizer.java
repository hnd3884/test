package com.jhlabs.image;

public interface Quantizer
{
    void setup(final int p0);
    
    void addPixels(final int[] p0, final int p1, final int p2);
    
    int[] buildColorTable();
    
    int getIndexForColor(final int p0);
}
