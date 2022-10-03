package com.jhlabs.image;

public class GammaFilter extends TransferFilter
{
    private float rGamma;
    private float gGamma;
    private float bGamma;
    
    public GammaFilter() {
        this(1.0f);
    }
    
    public GammaFilter(final float gamma) {
        this(gamma, gamma, gamma);
    }
    
    public GammaFilter(final float rGamma, final float gGamma, final float bGamma) {
        this.setGamma(rGamma, gGamma, bGamma);
    }
    
    public void setGamma(final float rGamma, final float gGamma, final float bGamma) {
        this.rGamma = rGamma;
        this.gGamma = gGamma;
        this.bGamma = bGamma;
        this.initialized = false;
    }
    
    public void setGamma(final float gamma) {
        this.setGamma(gamma, gamma, gamma);
    }
    
    public float getGamma() {
        return this.rGamma;
    }
    
    @Override
    protected void initialize() {
        this.rTable = this.makeTable(this.rGamma);
        if (this.gGamma == this.rGamma) {
            this.gTable = this.rTable;
        }
        else {
            this.gTable = this.makeTable(this.gGamma);
        }
        if (this.bGamma == this.rGamma) {
            this.bTable = this.rTable;
        }
        else if (this.bGamma == this.gGamma) {
            this.bTable = this.gTable;
        }
        else {
            this.bTable = this.makeTable(this.bGamma);
        }
    }
    
    private int[] makeTable(final float gamma) {
        final int[] table = new int[256];
        for (int i = 0; i < 256; ++i) {
            int v = (int)(255.0 * Math.pow(i / 255.0, 1.0 / gamma) + 0.5);
            if (v > 255) {
                v = 255;
            }
            table[i] = v;
        }
        return table;
    }
    
    @Override
    public String toString() {
        return "Colors/Gamma...";
    }
}
