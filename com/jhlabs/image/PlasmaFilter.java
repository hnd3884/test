package com.jhlabs.image;

import java.awt.Rectangle;
import java.util.Date;
import java.util.Random;

public class PlasmaFilter extends WholeImageFilter
{
    public float turbulence;
    private float scaling;
    private Colormap colormap;
    private Random randomGenerator;
    private long seed;
    private boolean useColormap;
    private boolean useImageColors;
    
    public PlasmaFilter() {
        this.turbulence = 1.0f;
        this.scaling = 0.0f;
        this.colormap = new LinearColormap();
        this.seed = 567L;
        this.useColormap = false;
        this.useImageColors = false;
        this.randomGenerator = new Random();
    }
    
    public void setTurbulence(final float turbulence) {
        this.turbulence = turbulence;
    }
    
    public float getTurbulence() {
        return this.turbulence;
    }
    
    public void setScaling(final float scaling) {
        this.scaling = scaling;
    }
    
    public float getScaling() {
        return this.scaling;
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    public void setUseColormap(final boolean useColormap) {
        this.useColormap = useColormap;
    }
    
    public boolean getUseColormap() {
        return this.useColormap;
    }
    
    public void setUseImageColors(final boolean useImageColors) {
        this.useImageColors = useImageColors;
    }
    
    public boolean getUseImageColors() {
        return this.useImageColors;
    }
    
    public void setSeed(final int seed) {
        this.seed = seed;
    }
    
    public int getSeed() {
        return (int)this.seed;
    }
    
    public void randomize() {
        this.seed = new Date().getTime();
    }
    
    private int randomRGB(final int[] inPixels, final int x, final int y) {
        if (this.useImageColors) {
            return inPixels[y * this.originalSpace.width + x];
        }
        final int r = (int)(255.0f * this.randomGenerator.nextFloat());
        final int g = (int)(255.0f * this.randomGenerator.nextFloat());
        final int b = (int)(255.0f * this.randomGenerator.nextFloat());
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
    
    private int displace(final int rgb, final float amount) {
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        r = PixelUtils.clamp(r + (int)(amount * (this.randomGenerator.nextFloat() - 0.5)));
        g = PixelUtils.clamp(g + (int)(amount * (this.randomGenerator.nextFloat() - 0.5)));
        b = PixelUtils.clamp(b + (int)(amount * (this.randomGenerator.nextFloat() - 0.5)));
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
    
    private int average(final int rgb1, final int rgb2) {
        return PixelUtils.combinePixels(rgb1, rgb2, 13);
    }
    
    private int getPixel(final int x, final int y, final int[] pixels, final int stride) {
        return pixels[y * stride + x];
    }
    
    private void putPixel(final int x, final int y, final int rgb, final int[] pixels, final int stride) {
        pixels[y * stride + x] = rgb;
    }
    
    private boolean doPixel(final int x1, final int y1, final int x2, final int y2, final int[] pixels, final int stride, final int depth, final int scale) {
        if (depth != 0) {
            final int mx = (x1 + x2) / 2;
            final int my = (y1 + y2) / 2;
            this.doPixel(x1, y1, mx, my, pixels, stride, depth - 1, scale + 1);
            this.doPixel(x1, my, mx, y2, pixels, stride, depth - 1, scale + 1);
            this.doPixel(mx, y1, x2, my, pixels, stride, depth - 1, scale + 1);
            return this.doPixel(mx, my, x2, y2, pixels, stride, depth - 1, scale + 1);
        }
        final int tl = this.getPixel(x1, y1, pixels, stride);
        final int bl = this.getPixel(x1, y2, pixels, stride);
        final int tr = this.getPixel(x2, y1, pixels, stride);
        final int br = this.getPixel(x2, y2, pixels, stride);
        final float amount = 256.0f / (2.0f * scale) * this.turbulence;
        final int mx = (x1 + x2) / 2;
        final int my = (y1 + y2) / 2;
        if (mx == x1 && mx == x2 && my == y1 && my == y2) {
            return true;
        }
        if (mx != x1 || mx != x2) {
            int ml = this.average(tl, bl);
            ml = this.displace(ml, amount);
            this.putPixel(x1, my, ml, pixels, stride);
            if (x1 != x2) {
                int mr = this.average(tr, br);
                mr = this.displace(mr, amount);
                this.putPixel(x2, my, mr, pixels, stride);
            }
        }
        if (my != y1 || my != y2) {
            if (x1 != mx || my != y2) {
                int mb = this.average(bl, br);
                mb = this.displace(mb, amount);
                this.putPixel(mx, y2, mb, pixels, stride);
            }
            if (y1 != y2) {
                int mt = this.average(tl, tr);
                mt = this.displace(mt, amount);
                this.putPixel(mx, y1, mt, pixels, stride);
            }
        }
        if (y1 != y2 || x1 != x2) {
            int mm = this.average(tl, br);
            final int t = this.average(bl, tr);
            mm = this.average(mm, t);
            mm = this.displace(mm, amount);
            this.putPixel(mx, my, mm, pixels, stride);
        }
        return x2 - x1 >= 3 || y2 - y1 >= 3;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final int[] outPixels = new int[width * height];
        this.randomGenerator.setSeed(this.seed);
        final int w1 = width - 1;
        final int h1 = height - 1;
        this.putPixel(0, 0, this.randomRGB(inPixels, 0, 0), outPixels, width);
        this.putPixel(w1, 0, this.randomRGB(inPixels, w1, 0), outPixels, width);
        this.putPixel(0, h1, this.randomRGB(inPixels, 0, h1), outPixels, width);
        this.putPixel(w1, h1, this.randomRGB(inPixels, w1, h1), outPixels, width);
        this.putPixel(w1 / 2, h1 / 2, this.randomRGB(inPixels, w1 / 2, h1 / 2), outPixels, width);
        this.putPixel(0, h1 / 2, this.randomRGB(inPixels, 0, h1 / 2), outPixels, width);
        this.putPixel(w1, h1 / 2, this.randomRGB(inPixels, w1, h1 / 2), outPixels, width);
        this.putPixel(w1 / 2, 0, this.randomRGB(inPixels, w1 / 2, 0), outPixels, width);
        this.putPixel(w1 / 2, h1, this.randomRGB(inPixels, w1 / 2, h1), outPixels, width);
        for (int depth = 1; this.doPixel(0, 0, width - 1, height - 1, outPixels, width, depth, 0); ++depth) {}
        if (this.useColormap && this.colormap != null) {
            int index = 0;
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    outPixels[index] = this.colormap.getColor((outPixels[index] & 0xFF) / 255.0f);
                    ++index;
                }
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Texture/Plasma...";
    }
}
