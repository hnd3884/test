package com.jhlabs.image;

import com.jhlabs.math.Noise;
import com.jhlabs.math.Function2D;

public class TextureFilter extends PointFilter
{
    private float scale;
    private float stretch;
    private float angle;
    public float amount;
    public float turbulence;
    public float gain;
    public float bias;
    public int operation;
    private float m00;
    private float m01;
    private float m10;
    private float m11;
    private Colormap colormap;
    private Function2D function;
    
    public TextureFilter() {
        this.scale = 32.0f;
        this.stretch = 1.0f;
        this.angle = 0.0f;
        this.amount = 1.0f;
        this.turbulence = 1.0f;
        this.gain = 0.5f;
        this.bias = 0.5f;
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.colormap = new Gradient();
        this.function = new Noise();
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    public void setFunction(final Function2D function) {
        this.function = function;
    }
    
    public Function2D getFunction() {
        return this.function;
    }
    
    public void setOperation(final int operation) {
        this.operation = operation;
    }
    
    public int getOperation() {
        return this.operation;
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
        float f = (this.turbulence == 1.0) ? Noise.noise2(nx, ny) : Noise.turbulence2(nx, ny, this.turbulence);
        f = f * 0.5f + 0.5f;
        f = ImageMath.gain(f, this.gain);
        f = ImageMath.bias(f, this.bias);
        f *= this.amount;
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
        if (this.operation != 0) {
            v = PixelUtils.combinePixels(rgb, v, this.operation);
        }
        return v;
    }
    
    @Override
    public String toString() {
        return "Texture/Noise...";
    }
}
