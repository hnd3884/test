package com.jhlabs.image;

import java.awt.Rectangle;

public class ShearFilter extends TransformFilter
{
    private float xangle;
    private float yangle;
    private float shx;
    private float shy;
    private float xoffset;
    private float yoffset;
    private boolean resize;
    
    public ShearFilter() {
        this.xangle = 0.0f;
        this.yangle = 0.0f;
        this.shx = 0.0f;
        this.shy = 0.0f;
        this.xoffset = 0.0f;
        this.yoffset = 0.0f;
        this.resize = true;
    }
    
    public void setResize(final boolean resize) {
        this.resize = resize;
    }
    
    public boolean isResize() {
        return this.resize;
    }
    
    public void setXAngle(final float xangle) {
        this.xangle = xangle;
        this.initialize();
    }
    
    public float getXAngle() {
        return this.xangle;
    }
    
    public void setYAngle(final float yangle) {
        this.yangle = yangle;
        this.initialize();
    }
    
    public float getYAngle() {
        return this.yangle;
    }
    
    private void initialize() {
        this.shx = (float)Math.sin(this.xangle);
        this.shy = (float)Math.sin(this.yangle);
    }
    
    @Override
    protected void transformSpace(final Rectangle r) {
        float tangent = (float)Math.tan(this.xangle);
        this.xoffset = -r.height * tangent;
        if (tangent < 0.0) {
            tangent = -tangent;
        }
        r.width = (int)(r.height * tangent + r.width + 0.999999f);
        tangent = (float)Math.tan(this.yangle);
        this.yoffset = -r.width * tangent;
        if (tangent < 0.0) {
            tangent = -tangent;
        }
        r.height = (int)(r.width * tangent + r.height + 0.999999f);
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        out[0] = x + this.xoffset + y * this.shx;
        out[1] = y + this.yoffset + x * this.shy;
    }
    
    @Override
    public String toString() {
        return "Distort/Shear...";
    }
}
