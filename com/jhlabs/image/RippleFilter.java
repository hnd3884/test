package com.jhlabs.image;

import com.jhlabs.math.Noise;
import java.awt.Rectangle;

public class RippleFilter extends TransformFilter
{
    public static final int SINE = 0;
    public static final int SAWTOOTH = 1;
    public static final int TRIANGLE = 2;
    public static final int NOISE = 3;
    private float xAmplitude;
    private float yAmplitude;
    private float xWavelength;
    private float yWavelength;
    private int waveType;
    
    public RippleFilter() {
        this.xAmplitude = 5.0f;
        this.yAmplitude = 0.0f;
        final float n = 16.0f;
        this.yWavelength = n;
        this.xWavelength = n;
    }
    
    public void setXAmplitude(final float xAmplitude) {
        this.xAmplitude = xAmplitude;
    }
    
    public float getXAmplitude() {
        return this.xAmplitude;
    }
    
    public void setXWavelength(final float xWavelength) {
        this.xWavelength = xWavelength;
    }
    
    public float getXWavelength() {
        return this.xWavelength;
    }
    
    public void setYAmplitude(final float yAmplitude) {
        this.yAmplitude = yAmplitude;
    }
    
    public float getYAmplitude() {
        return this.yAmplitude;
    }
    
    public void setYWavelength(final float yWavelength) {
        this.yWavelength = yWavelength;
    }
    
    public float getYWavelength() {
        return this.yWavelength;
    }
    
    public void setWaveType(final int waveType) {
        this.waveType = waveType;
    }
    
    public int getWaveType() {
        return this.waveType;
    }
    
    @Override
    protected void transformSpace(final Rectangle r) {
        if (this.edgeAction == 0) {
            r.x -= (int)this.xAmplitude;
            r.width += (int)(2.0f * this.xAmplitude);
            r.y -= (int)this.yAmplitude;
            r.height += (int)(2.0f * this.yAmplitude);
        }
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        final float nx = y / this.xWavelength;
        final float ny = x / this.yWavelength;
        float fx = 0.0f;
        float fy = 0.0f;
        switch (this.waveType) {
            default: {
                fx = (float)Math.sin(nx);
                fy = (float)Math.sin(ny);
                break;
            }
            case 1: {
                fx = ImageMath.mod(nx, 1.0f);
                fy = ImageMath.mod(ny, 1.0f);
                break;
            }
            case 2: {
                fx = ImageMath.triangle(nx);
                fy = ImageMath.triangle(ny);
                break;
            }
            case 3: {
                fx = Noise.noise1(nx);
                fy = Noise.noise1(ny);
                break;
            }
        }
        out[0] = x + this.xAmplitude * fx;
        out[1] = y + this.yAmplitude * fy;
    }
    
    @Override
    public String toString() {
        return "Distort/Ripple...";
    }
}
