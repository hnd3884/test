package com.jhlabs.image;

import java.awt.Rectangle;
import java.util.Date;
import java.util.Random;

public class QuiltFilter extends WholeImageFilter
{
    private Random randomGenerator;
    private long seed;
    private int iterations;
    private float a;
    private float b;
    private float c;
    private float d;
    private int k;
    private Colormap colormap;
    
    public QuiltFilter() {
        this.seed = 567L;
        this.iterations = 25000;
        this.a = -0.59f;
        this.b = 0.2f;
        this.c = 0.1f;
        this.d = 0.0f;
        this.k = 0;
        this.colormap = new LinearColormap();
        this.randomGenerator = new Random();
    }
    
    public void randomize() {
        this.seed = new Date().getTime();
        this.randomGenerator.setSeed(this.seed);
        this.a = this.randomGenerator.nextFloat();
        this.b = this.randomGenerator.nextFloat();
        this.c = this.randomGenerator.nextFloat();
        this.d = this.randomGenerator.nextFloat();
        this.k = this.randomGenerator.nextInt() % 20 - 10;
    }
    
    public void setIterations(final int iterations) {
        this.iterations = iterations;
    }
    
    public int getIterations() {
        return this.iterations;
    }
    
    public void setA(final float a) {
        this.a = a;
    }
    
    public float getA() {
        return this.a;
    }
    
    public void setB(final float b) {
        this.b = b;
    }
    
    public float getB() {
        return this.b;
    }
    
    public void setC(final float c) {
        this.c = c;
    }
    
    public float getC() {
        return this.c;
    }
    
    public void setD(final float d) {
        this.d = d;
    }
    
    public float getD() {
        return this.d;
    }
    
    public void setK(final int k) {
        this.k = k;
    }
    
    public int getK() {
        return this.k;
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final int[] outPixels = new int[width * height];
        final int i = 0;
        int max = 0;
        float x = 0.1f;
        float y = 0.3f;
        for (int n = 0; n < 20; ++n) {
            final float mx = 3.1415927f * x;
            final float my = 3.1415927f * y;
            final float smx2 = (float)Math.sin(2.0f * mx);
            final float smy2 = (float)Math.sin(2.0f * my);
            float x2 = (float)(this.a * smx2 + this.b * smx2 * Math.cos(2.0f * my) + this.c * Math.sin(4.0f * mx) + this.d * Math.sin(6.0f * mx) * Math.cos(4.0f * my) + this.k * x);
            x2 = ((x2 >= 0.0f) ? (x2 - (int)x2) : (x2 - (int)x2 + 1.0f));
            float y2 = (float)(this.a * smy2 + this.b * smy2 * Math.cos(2.0f * mx) + this.c * Math.sin(4.0f * my) + this.d * Math.sin(6.0f * my) * Math.cos(4.0f * mx) + this.k * y);
            y2 = ((y2 >= 0.0f) ? (y2 - (int)y2) : (y2 - (int)y2 + 1.0f));
            x = x2;
            y = y2;
        }
        for (int n = 0; n < this.iterations; ++n) {
            final float mx = 3.1415927f * x;
            final float my = 3.1415927f * y;
            float x3 = (float)(this.a * Math.sin(2.0f * mx) + this.b * Math.sin(2.0f * mx) * Math.cos(2.0f * my) + this.c * Math.sin(4.0f * mx) + this.d * Math.sin(6.0f * mx) * Math.cos(4.0f * my) + this.k * x);
            x3 = ((x3 >= 0.0f) ? (x3 - (int)x3) : (x3 - (int)x3 + 1.0f));
            float y3 = (float)(this.a * Math.sin(2.0f * my) + this.b * Math.sin(2.0f * my) * Math.cos(2.0f * mx) + this.c * Math.sin(4.0f * my) + this.d * Math.sin(6.0f * my) * Math.cos(4.0f * mx) + this.k * y);
            y3 = ((y3 >= 0.0f) ? (y3 - (int)y3) : (y3 - (int)y3 + 1.0f));
            x = x3;
            y = y3;
            final int ix = (int)(width * x);
            final int iy = (int)(height * y);
            if (ix >= 0 && ix < width && iy >= 0 && iy < height) {
                final int t = outPixels[width * iy + ix]++;
                if (t > max) {
                    max = t;
                }
            }
        }
        if (this.colormap != null) {
            int index = 0;
            for (y = 0.0f; y < height; ++y) {
                for (x = 0.0f; x < width; ++x) {
                    outPixels[index] = this.colormap.getColor(outPixels[index] / (float)max);
                    ++index;
                }
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Texture/Chaotic Quilt...";
    }
}
