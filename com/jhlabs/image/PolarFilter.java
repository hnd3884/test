package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class PolarFilter extends TransformFilter
{
    public static final int RECT_TO_POLAR = 0;
    public static final int POLAR_TO_RECT = 1;
    public static final int INVERT_IN_CIRCLE = 2;
    private int type;
    private float width;
    private float height;
    private float centreX;
    private float centreY;
    private float radius;
    
    public PolarFilter() {
        this(0);
    }
    
    public PolarFilter(final int type) {
        this.type = type;
        this.setEdgeAction(1);
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        this.width = (float)src.getWidth();
        this.height = (float)src.getHeight();
        this.centreX = this.width / 2.0f;
        this.centreY = this.height / 2.0f;
        this.radius = Math.max(this.centreY, this.centreX);
        return super.filter(src, dst);
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    private float sqr(final float x) {
        return x * x;
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        float r = 0.0f;
        switch (this.type) {
            case 0: {
                float theta = 0.0f;
                if (x >= this.centreX) {
                    if (y > this.centreY) {
                        theta = 3.1415927f - (float)Math.atan((x - this.centreX) / (y - this.centreY));
                        r = (float)Math.sqrt(this.sqr(x - this.centreX) + this.sqr(y - this.centreY));
                    }
                    else if (y < this.centreY) {
                        theta = (float)Math.atan((x - this.centreX) / (this.centreY - y));
                        r = (float)Math.sqrt(this.sqr(x - this.centreX) + this.sqr(this.centreY - y));
                    }
                    else {
                        theta = 1.5707964f;
                        r = x - this.centreX;
                    }
                }
                else if (x < this.centreX) {
                    if (y < this.centreY) {
                        theta = 6.2831855f - (float)Math.atan((this.centreX - x) / (this.centreY - y));
                        r = (float)Math.sqrt(this.sqr(this.centreX - x) + this.sqr(this.centreY - y));
                    }
                    else if (y > this.centreY) {
                        theta = 3.1415927f + (float)Math.atan((this.centreX - x) / (y - this.centreY));
                        r = (float)Math.sqrt(this.sqr(this.centreX - x) + this.sqr(y - this.centreY));
                    }
                    else {
                        theta = 4.712389f;
                        r = this.centreX - x;
                    }
                }
                float m;
                if (x != this.centreX) {
                    m = Math.abs((y - this.centreY) / (x - this.centreX));
                }
                else {
                    m = 0.0f;
                }
                if (m <= this.height / this.width) {
                    if (x == this.centreX) {
                        final float xmax = 0.0f;
                        final float ymax = this.centreY;
                    }
                    else {
                        final float xmax = this.centreX;
                        final float ymax = m * xmax;
                    }
                }
                else {
                    final float ymax = this.centreY;
                    final float xmax = ymax / m;
                }
                out[0] = this.width - 1.0f - (this.width - 1.0f) / 6.2831855f * theta;
                out[1] = this.height * r / this.radius;
                break;
            }
            case 1: {
                final float theta = x / this.width * 6.2831855f;
                float theta2;
                if (theta >= 4.712389f) {
                    theta2 = 6.2831855f - theta;
                }
                else if (theta >= 3.1415927f) {
                    theta2 = theta - 3.1415927f;
                }
                else if (theta >= 1.5707964f) {
                    theta2 = 3.1415927f - theta;
                }
                else {
                    theta2 = theta;
                }
                final float t = (float)Math.tan(theta2);
                float m;
                if (t != 0.0f) {
                    m = 1.0f / t;
                }
                else {
                    m = 0.0f;
                }
                if (m <= this.height / this.width) {
                    if (theta2 == 0.0f) {
                        final float xmax = 0.0f;
                        final float ymax = this.centreY;
                    }
                    else {
                        final float xmax = this.centreX;
                        final float ymax = m * xmax;
                    }
                }
                else {
                    final float ymax = this.centreY;
                    final float xmax = ymax / m;
                }
                r = this.radius * (y / this.height);
                final float nx = -r * (float)Math.sin(theta2);
                final float ny = r * (float)Math.cos(theta2);
                if (theta >= 4.712389f) {
                    out[0] = this.centreX - nx;
                    out[1] = this.centreY - ny;
                    break;
                }
                if (theta >= 3.141592653589793) {
                    out[0] = this.centreX - nx;
                    out[1] = this.centreY + ny;
                    break;
                }
                if (theta >= 1.5707963267948966) {
                    out[0] = this.centreX + nx;
                    out[1] = this.centreY + ny;
                    break;
                }
                out[0] = this.centreX + nx;
                out[1] = this.centreY - ny;
                break;
            }
            case 2: {
                final float dx = x - this.centreX;
                final float dy = y - this.centreY;
                final float distance2 = dx * dx + dy * dy;
                out[0] = this.centreX + this.centreX * this.centreX * dx / distance2;
                out[1] = this.centreY + this.centreY * this.centreY * dy / distance2;
                break;
            }
        }
    }
    
    @Override
    public String toString() {
        return "Distort/Polar Coordinates...";
    }
}
