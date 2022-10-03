package com.jhlabs.image;

import com.jhlabs.math.Noise;

public class WoodFilter extends PointFilter
{
    private float scale;
    private float stretch;
    private float angle;
    private float rings;
    private float turbulence;
    private float fibres;
    private float gain;
    private float m00;
    private float m01;
    private float m10;
    private float m11;
    private Colormap colormap;
    
    public WoodFilter() {
        this.scale = 200.0f;
        this.stretch = 10.0f;
        this.angle = 1.5707964f;
        this.rings = 0.5f;
        this.turbulence = 0.0f;
        this.fibres = 0.5f;
        this.gain = 0.8f;
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.colormap = new LinearColormap(-1719148, -6784175);
    }
    
    public void setRings(final float rings) {
        this.rings = rings;
    }
    
    public float getRings() {
        return this.rings;
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
    
    public void setFibres(final float fibres) {
        this.fibres = fibres;
    }
    
    public float getFibres() {
        return this.fibres;
    }
    
    public void setGain(final float gain) {
        this.gain = gain;
    }
    
    public float getGain() {
        return this.gain;
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        float nx = this.m00 * x + this.m01 * y;
        float ny = this.m10 * x + this.m11 * y;
        nx /= this.scale;
        ny /= this.scale * this.stretch;
        float f = Noise.noise2(nx, ny);
        f += 0.1f * this.turbulence * Noise.noise2(nx * 0.05f, ny * 20.0f);
        f = f * 0.5f + 0.5f;
        f *= this.rings * 50.0f;
        f -= (int)f;
        f *= 1.0f - ImageMath.smoothStep(this.gain, 1.0f, f);
        f += this.fibres * Noise.noise2(nx * this.scale, ny * 50.0f);
        final int a = rgb & 0xFF000000;
        int v;
        if (this.colormap != null) {
            v = this.colormap.getColor(f);
        }
        else {
            v = PixelUtils.clamp((int)(f * 255.0f));
            final int r = v << 16;
            final int g = v << 8;
            final int b = v;
            v = (a | r | g | b);
        }
        return v;
    }
    
    @Override
    public String toString() {
        return "Texture/Wood...";
    }
}
