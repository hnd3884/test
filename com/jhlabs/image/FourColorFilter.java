package com.jhlabs.image;

public class FourColorFilter extends PointFilter
{
    private int width;
    private int height;
    private int colorNW;
    private int colorNE;
    private int colorSW;
    private int colorSE;
    private int rNW;
    private int gNW;
    private int bNW;
    private int rNE;
    private int gNE;
    private int bNE;
    private int rSW;
    private int gSW;
    private int bSW;
    private int rSE;
    private int gSE;
    private int bSE;
    
    public FourColorFilter() {
        this.setColorNW(-65536);
        this.setColorNE(-65281);
        this.setColorSW(-16776961);
        this.setColorSE(-16711681);
    }
    
    public void setColorNW(final int color) {
        this.colorNW = color;
        this.rNW = (color >> 16 & 0xFF);
        this.gNW = (color >> 8 & 0xFF);
        this.bNW = (color & 0xFF);
    }
    
    public int getColorNW() {
        return this.colorNW;
    }
    
    public void setColorNE(final int color) {
        this.colorNE = color;
        this.rNE = (color >> 16 & 0xFF);
        this.gNE = (color >> 8 & 0xFF);
        this.bNE = (color & 0xFF);
    }
    
    public int getColorNE() {
        return this.colorNE;
    }
    
    public void setColorSW(final int color) {
        this.colorSW = color;
        this.rSW = (color >> 16 & 0xFF);
        this.gSW = (color >> 8 & 0xFF);
        this.bSW = (color & 0xFF);
    }
    
    public int getColorSW() {
        return this.colorSW;
    }
    
    public void setColorSE(final int color) {
        this.colorSE = color;
        this.rSE = (color >> 16 & 0xFF);
        this.gSE = (color >> 8 & 0xFF);
        this.bSE = (color & 0xFF);
    }
    
    public int getColorSE() {
        return this.colorSE;
    }
    
    @Override
    public void setDimensions(final int width, final int height) {
        super.setDimensions(this.width = width, this.height = height);
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final float fx = x / (float)this.width;
        final float fy = y / (float)this.height;
        float p = this.rNW + (this.rNE - this.rNW) * fx;
        float q = this.rSW + (this.rSE - this.rSW) * fx;
        final int r = (int)(p + (q - p) * fy + 0.5f);
        p = this.gNW + (this.gNE - this.gNW) * fx;
        q = this.gSW + (this.gSE - this.gSW) * fx;
        final int g = (int)(p + (q - p) * fy + 0.5f);
        p = this.bNW + (this.bNE - this.bNW) * fx;
        q = this.bSW + (this.bSE - this.bSW) * fx;
        final int b = (int)(p + (q - p) * fy + 0.5f);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
    
    @Override
    public String toString() {
        return "Texture/Four Color Fill...";
    }
}
