package com.jhlabs.image;

import java.awt.Graphics2D;
import com.jhlabs.composite.MiscComposite;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class RaysFilter extends MotionBlurOp
{
    private float opacity;
    private float threshold;
    private float strength;
    private boolean raysOnly;
    private Colormap colormap;
    
    public RaysFilter() {
        this.opacity = 1.0f;
        this.threshold = 0.0f;
        this.strength = 0.5f;
        this.raysOnly = false;
    }
    
    public void setOpacity(final float opacity) {
        this.opacity = opacity;
    }
    
    public float getOpacity() {
        return this.opacity;
    }
    
    public void setThreshold(final float threshold) {
        this.threshold = threshold;
    }
    
    public float getThreshold() {
        return this.threshold;
    }
    
    public void setStrength(final float strength) {
        this.strength = strength;
    }
    
    public float getStrength() {
        return this.strength;
    }
    
    public void setRaysOnly(final boolean raysOnly) {
        this.raysOnly = raysOnly;
    }
    
    public boolean getRaysOnly() {
        return this.raysOnly;
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final int[] pixels = new int[width];
        final int[] srcPixels = new int[width];
        BufferedImage rays = new BufferedImage(width, height, 2);
        final int threshold3 = (int)(this.threshold * 3.0f * 255.0f);
        for (int y = 0; y < height; ++y) {
            this.getRGB(src, 0, y, width, 1, pixels);
            for (int x = 0; x < width; ++x) {
                final int rgb = pixels[x];
                final int a = rgb & 0xFF000000;
                final int r = rgb >> 16 & 0xFF;
                final int g = rgb >> 8 & 0xFF;
                final int b = rgb & 0xFF;
                int l = r + g + b;
                if (l < threshold3) {
                    pixels[x] = -16777216;
                }
                else {
                    l /= 3;
                    pixels[x] = (a | l << 16 | l << 8 | l);
                }
            }
            this.setRGB(rays, 0, y, width, 1, pixels);
        }
        rays = super.filter(rays, null);
        for (int y = 0; y < height; ++y) {
            this.getRGB(rays, 0, y, width, 1, pixels);
            this.getRGB(src, 0, y, width, 1, srcPixels);
            for (int x = 0; x < width; ++x) {
                int rgb = pixels[x];
                final int a = rgb & 0xFF000000;
                int r = rgb >> 16 & 0xFF;
                int g = rgb >> 8 & 0xFF;
                int b = rgb & 0xFF;
                if (this.colormap != null) {
                    final int l = r + g + b;
                    rgb = this.colormap.getColor(l * this.strength * 0.33333334f);
                }
                else {
                    r = PixelUtils.clamp((int)(r * this.strength));
                    g = PixelUtils.clamp((int)(g * this.strength));
                    b = PixelUtils.clamp((int)(b * this.strength));
                    rgb = (a | r << 16 | g << 8 | b);
                }
                pixels[x] = rgb;
            }
            this.setRGB(rays, 0, y, width, 1, pixels);
        }
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final Graphics2D g2 = dst.createGraphics();
        if (!this.raysOnly) {
            g2.setComposite(AlphaComposite.SrcOver);
            g2.drawRenderedImage(src, null);
        }
        g2.setComposite(MiscComposite.getInstance(1, this.opacity));
        g2.drawRenderedImage(rays, null);
        g2.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Stylize/Rays...";
    }
}
