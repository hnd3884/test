package com.jhlabs.vecmath;

import java.awt.Color;

public class Color4f extends Tuple4f
{
    public Color4f() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public Color4f(final float[] x) {
        this.x = x[0];
        this.y = x[1];
        this.z = x[2];
        this.w = x[3];
    }
    
    public Color4f(final float x, final float y, final float z, final float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Color4f(final Color4f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        this.w = t.w;
    }
    
    public Color4f(final Tuple4f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        this.w = t.w;
    }
    
    public Color4f(final Color c) {
        this.set(c);
    }
    
    public void set(final Color c) {
        this.set(c.getRGBComponents(null));
    }
    
    public Color get() {
        return new Color(this.x, this.y, this.z, this.w);
    }
}
