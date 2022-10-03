package com.jhlabs.image;

import java.awt.Rectangle;
import java.util.Date;
import java.util.Random;

public class SmearFilter extends WholeImageFilter
{
    public static final int CROSSES = 0;
    public static final int LINES = 1;
    public static final int CIRCLES = 2;
    public static final int SQUARES = 3;
    private Colormap colormap;
    private float angle;
    private float density;
    private float scatter;
    private int distance;
    private Random randomGenerator;
    private long seed;
    private int shape;
    private float mix;
    private int fadeout;
    private boolean background;
    
    public SmearFilter() {
        this.colormap = new LinearColormap();
        this.angle = 0.0f;
        this.density = 0.5f;
        this.scatter = 0.0f;
        this.distance = 8;
        this.seed = 567L;
        this.shape = 1;
        this.mix = 0.5f;
        this.fadeout = 0;
        this.background = false;
        this.randomGenerator = new Random();
    }
    
    public void setShape(final int shape) {
        this.shape = shape;
    }
    
    public int getShape() {
        return this.shape;
    }
    
    public void setDistance(final int distance) {
        this.distance = distance;
    }
    
    public int getDistance() {
        return this.distance;
    }
    
    public void setDensity(final float density) {
        this.density = density;
    }
    
    public float getDensity() {
        return this.density;
    }
    
    public void setScatter(final float scatter) {
        this.scatter = scatter;
    }
    
    public float getScatter() {
        return this.scatter;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setMix(final float mix) {
        this.mix = mix;
    }
    
    public float getMix() {
        return this.mix;
    }
    
    public void setFadeout(final int fadeout) {
        this.fadeout = fadeout;
    }
    
    public int getFadeout() {
        return this.fadeout;
    }
    
    public void setBackground(final boolean background) {
        this.background = background;
    }
    
    public boolean getBackground() {
        return this.background;
    }
    
    public void randomize() {
        this.seed = new Date().getTime();
    }
    
    private float random(final float low, final float high) {
        return low + (high - low) * this.randomGenerator.nextFloat();
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final int[] outPixels = new int[width * height];
        this.randomGenerator.setSeed(this.seed);
        final float sinAngle = (float)Math.sin(this.angle);
        final float cosAngle = (float)Math.cos(this.angle);
        int i = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                outPixels[i] = (this.background ? -1 : inPixels[i]);
                ++i;
            }
        }
        switch (this.shape) {
            case 0: {
                for (final int numShapes = (int)(2.0f * this.density * width * height / (this.distance + 1)), i = 0; i < numShapes; ++i) {
                    final int x2 = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % width;
                    final int y2 = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % height;
                    final int length = this.randomGenerator.nextInt() % this.distance + 1;
                    final int rgb = inPixels[y2 * width + x2];
                    for (int x3 = x2 - length; x3 < x2 + length + 1; ++x3) {
                        if (x3 >= 0 && x3 < width) {
                            final int rgb2 = this.background ? -1 : outPixels[y2 * width + x3];
                            outPixels[y2 * width + x3] = ImageMath.mixColors(this.mix, rgb2, rgb);
                        }
                    }
                    for (int y3 = y2 - length; y3 < y2 + length + 1; ++y3) {
                        if (y3 >= 0 && y3 < height) {
                            final int rgb2 = this.background ? -1 : outPixels[y3 * width + x2];
                            outPixels[y3 * width + x2] = ImageMath.mixColors(this.mix, rgb2, rgb);
                        }
                    }
                }
                break;
            }
            case 1: {
                for (final int numShapes = (int)(2.0f * this.density * width * height / 2.0f), i = 0; i < numShapes; ++i) {
                    final int sx = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % width;
                    final int sy = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % height;
                    final int rgb3 = inPixels[sy * width + sx];
                    final int length2 = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % this.distance;
                    int dx = (int)(length2 * cosAngle);
                    int dy = (int)(length2 * sinAngle);
                    final int x4 = sx - dx;
                    final int y4 = sy - dy;
                    final int x5 = sx + dx;
                    final int y5 = sy + dy;
                    int ddx;
                    if (x5 < x4) {
                        ddx = -1;
                    }
                    else {
                        ddx = 1;
                    }
                    int ddy;
                    if (y5 < y4) {
                        ddy = -1;
                    }
                    else {
                        ddy = 1;
                    }
                    dx = x5 - x4;
                    dy = y5 - y4;
                    dx = Math.abs(dx);
                    dy = Math.abs(dy);
                    int x6 = x4;
                    int y6 = y4;
                    if (x6 < width && x6 >= 0 && y6 < height && y6 >= 0) {
                        final int rgb4 = this.background ? -1 : outPixels[y6 * width + x6];
                        outPixels[y6 * width + x6] = ImageMath.mixColors(this.mix, rgb4, rgb3);
                    }
                    if (Math.abs(dx) > Math.abs(dy)) {
                        int d = 2 * dy - dx;
                        final int incrE = 2 * dy;
                        final int incrNE = 2 * (dy - dx);
                        while (x6 != x5) {
                            if (d <= 0) {
                                d += incrE;
                            }
                            else {
                                d += incrNE;
                                y6 += ddy;
                            }
                            x6 += ddx;
                            if (x6 < width && x6 >= 0 && y6 < height && y6 >= 0) {
                                final int rgb4 = this.background ? -1 : outPixels[y6 * width + x6];
                                outPixels[y6 * width + x6] = ImageMath.mixColors(this.mix, rgb4, rgb3);
                            }
                        }
                    }
                    else {
                        int d = 2 * dx - dy;
                        final int incrE = 2 * dx;
                        final int incrNE = 2 * (dx - dy);
                        while (y6 != y5) {
                            if (d <= 0) {
                                d += incrE;
                            }
                            else {
                                d += incrNE;
                                x6 += ddx;
                            }
                            y6 += ddy;
                            if (x6 < width && x6 >= 0 && y6 < height && y6 >= 0) {
                                final int rgb4 = this.background ? -1 : outPixels[y6 * width + x6];
                                outPixels[y6 * width + x6] = ImageMath.mixColors(this.mix, rgb4, rgb3);
                            }
                        }
                    }
                }
                break;
            }
            case 2:
            case 3: {
                final int radius = this.distance + 1;
                final int radius2 = radius * radius;
                for (final int numShapes = (int)(2.0f * this.density * width * height / radius), i = 0; i < numShapes; ++i) {
                    final int sx2 = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % width;
                    final int sy2 = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % height;
                    final int rgb5 = inPixels[sy2 * width + sx2];
                    for (int x7 = sx2 - radius; x7 < sx2 + radius + 1; ++x7) {
                        for (int y7 = sy2 - radius; y7 < sy2 + radius + 1; ++y7) {
                            int f;
                            if (this.shape == 2) {
                                f = (x7 - sx2) * (x7 - sx2) + (y7 - sy2) * (y7 - sy2);
                            }
                            else {
                                f = 0;
                            }
                            if (x7 >= 0 && x7 < width && y7 >= 0 && y7 < height && f <= radius2) {
                                final int rgb6 = this.background ? -1 : outPixels[y7 * width + x7];
                                outPixels[y7 * width + x7] = ImageMath.mixColors(this.mix, rgb6, rgb5);
                            }
                        }
                    }
                }
                break;
            }
        }
        return outPixels;
    }
    
    @Override
    public String toString() {
        return "Effects/Smear...";
    }
}
