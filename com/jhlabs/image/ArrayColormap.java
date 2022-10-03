package com.jhlabs.image;

public class ArrayColormap implements Colormap, Cloneable
{
    protected int[] map;
    
    public ArrayColormap() {
        this.map = new int[256];
    }
    
    public ArrayColormap(final int[] map) {
        this.map = map;
    }
    
    public Object clone() {
        try {
            final ArrayColormap g = (ArrayColormap)super.clone();
            g.map = this.map.clone();
            return g;
        }
        catch (final CloneNotSupportedException e) {
            return null;
        }
    }
    
    public void setMap(final int[] map) {
        this.map = map;
    }
    
    public int[] getMap() {
        return this.map;
    }
    
    public int getColor(final float v) {
        int n = (int)(v * 255.0f);
        if (n < 0) {
            n = 0;
        }
        else if (n > 255) {
            n = 255;
        }
        return this.map[n];
    }
    
    public void setColorInterpolated(final int index, final int firstIndex, final int lastIndex, final int color) {
        final int firstColor = this.map[firstIndex];
        final int lastColor = this.map[lastIndex];
        for (int i = firstIndex; i <= index; ++i) {
            this.map[i] = ImageMath.mixColors((i - firstIndex) / (float)(index - firstIndex), firstColor, color);
        }
        for (int i = index; i < lastIndex; ++i) {
            this.map[i] = ImageMath.mixColors((i - index) / (float)(lastIndex - index), color, lastColor);
        }
    }
    
    public void setColorRange(final int firstIndex, final int lastIndex, final int color1, final int color2) {
        for (int i = firstIndex; i <= lastIndex; ++i) {
            this.map[i] = ImageMath.mixColors((i - firstIndex) / (float)(lastIndex - firstIndex), color1, color2);
        }
    }
    
    public void setColorRange(final int firstIndex, final int lastIndex, final int color) {
        for (int i = firstIndex; i <= lastIndex; ++i) {
            this.map[i] = color;
        }
    }
    
    public void setColor(final int index, final int color) {
        this.map[index] = color;
    }
}
