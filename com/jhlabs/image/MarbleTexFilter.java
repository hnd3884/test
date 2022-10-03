package com.jhlabs.image;

import com.jhlabs.math.Noise;

public class MarbleTexFilter extends PointFilter
{
    private float scale;
    private float stretch;
    private float angle;
    private float turbulence;
    private float turbulenceFactor;
    private Colormap colormap;
    private float m00;
    private float m01;
    private float m10;
    private float m11;
    
    public MarbleTexFilter() {
        this.scale = 32.0f;
        this.stretch = 1.0f;
        this.angle = 0.0f;
        this.turbulence = 1.0f;
        this.turbulenceFactor = 0.5f;
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
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
    
    public void setTurbulenceFactor(final float turbulenceFactor) {
        this.turbulenceFactor = turbulenceFactor;
    }
    
    public float getTurbulenceFactor() {
        return this.turbulenceFactor;
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
        nx /= this.scale * this.stretch;
        ny /= this.scale;
        final int a = rgb & 0xFF000000;
        if (this.colormap != null) {
            final float chaos = this.turbulenceFactor * Noise.turbulence2(nx, ny, this.turbulence);
            float f = 3.0f * this.turbulenceFactor * chaos + ny;
            f = (float)Math.sin(f * 3.141592653589793);
            final float perturb = (float)Math.sin(40.0 * chaos);
            f += (float)(0.2 * perturb);
            return this.colormap.getColor(f);
        }
        final float chaos2 = this.turbulenceFactor * Noise.turbulence2(nx, ny, this.turbulence);
        final float t = (float)Math.sin(Math.sin(8.0 * chaos2 + 7.0f * nx + 3.0 * ny));
        float greenLayer;
        float brownLayer = greenLayer = Math.abs(t);
        float perturb2 = (float)Math.sin(40.0 * chaos2);
        perturb2 = Math.abs(perturb2);
        final float brownPerturb = 0.6f * perturb2 + 0.3f;
        final float greenPerturb = 0.2f * perturb2 + 0.8f;
        final float grnPerturb = 0.15f * perturb2 + 0.85f;
        float grn = 0.5f * (float)Math.pow(Math.abs(brownLayer), 0.3);
        brownLayer = (float)Math.pow(0.5 * (brownLayer + 1.0), 0.6) * brownPerturb;
        greenLayer = (float)Math.pow(0.5 * (greenLayer + 1.0), 0.6) * greenPerturb;
        final float red = (0.5f * brownLayer + 0.35f * greenLayer) * 2.0f * grn;
        final float blu = (0.25f * brownLayer + 0.35f * greenLayer) * 2.0f * grn;
        grn *= Math.max(brownLayer, greenLayer) * grnPerturb;
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        r = PixelUtils.clamp((int)(r * red));
        g = PixelUtils.clamp((int)(g * grn));
        b = PixelUtils.clamp((int)(b * blu));
        return (rgb & 0xFF000000) | r << 16 | g << 8 | b;
    }
    
    @Override
    public String toString() {
        return "Texture/Marble Texture...";
    }
}
