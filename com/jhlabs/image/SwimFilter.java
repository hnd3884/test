package com.jhlabs.image;

import com.jhlabs.math.Noise;

public class SwimFilter extends TransformFilter
{
    private float scale;
    private float stretch;
    private float angle;
    private float amount;
    private float turbulence;
    private float time;
    private float m00;
    private float m01;
    private float m10;
    private float m11;
    
    public SwimFilter() {
        this.scale = 32.0f;
        this.stretch = 1.0f;
        this.angle = 0.0f;
        this.amount = 1.0f;
        this.turbulence = 1.0f;
        this.time = 0.0f;
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    public void setScale(final float scale) {
        this.scale = scale;
    }
    
    public float getScale() {
        return this.scale;
    }
    
    public void setStretch(final float stretch) {
        this.stretch = stretch;
    }
    
    public float getStretch() {
        return this.stretch;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
        final float cos = (float)Math.cos(angle);
        final float sin = (float)Math.sin(angle);
        this.m00 = cos;
        this.m01 = sin;
        this.m10 = -sin;
        this.m11 = cos;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setTurbulence(final float turbulence) {
        this.turbulence = turbulence;
    }
    
    public float getTurbulence() {
        return this.turbulence;
    }
    
    public void setTime(final float time) {
        this.time = time;
    }
    
    public float getTime() {
        return this.time;
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        float nx = this.m00 * x + this.m01 * y;
        float ny = this.m10 * x + this.m11 * y;
        nx /= this.scale;
        ny /= this.scale * this.stretch;
        if (this.turbulence == 1.0f) {
            out[0] = x + this.amount * Noise.noise3(nx + 0.5f, ny, this.time);
            out[1] = y + this.amount * Noise.noise3(nx, ny + 0.5f, this.time);
        }
        else {
            out[0] = x + this.amount * Noise.turbulence3(nx + 0.5f, ny, this.turbulence, this.time);
            out[1] = y + this.amount * Noise.turbulence3(nx, ny + 0.5f, this.turbulence, this.time);
        }
    }
    
    @Override
    public String toString() {
        return "Distort/Swim...";
    }
}
