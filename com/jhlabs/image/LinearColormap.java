package com.jhlabs.image;

public class LinearColormap implements Colormap
{
    private int color1;
    private int color2;
    
    public LinearColormap() {
        this(-16777216, -1);
    }
    
    public LinearColormap(final int color1, final int color2) {
        this.color1 = color1;
        this.color2 = color2;
    }
    
    public void setColor1(final int color1) {
        this.color1 = color1;
    }
    
    public int getColor1() {
        return this.color1;
    }
    
    public void setColor2(final int color2) {
        this.color2 = color2;
    }
    
    public int getColor2() {
        return this.color2;
    }
    
    public int getColor(final float v) {
        return ImageMath.mixColors(ImageMath.clamp(v, 0.0f, 1.0f), this.color1, this.color2);
    }
}
