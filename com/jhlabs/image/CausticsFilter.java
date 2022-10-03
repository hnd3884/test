package com.jhlabs.image;

import com.jhlabs.math.Noise;
import java.util.Random;
import java.awt.Rectangle;

public class CausticsFilter extends WholeImageFilter
{
    private float scale;
    private float angle;
    private int brightness;
    private float amount;
    private float turbulence;
    private float dispersion;
    private float time;
    private int samples;
    private int bgColor;
    private float s;
    private float c;
    
    public CausticsFilter() {
        this.scale = 32.0f;
        this.angle = 0.0f;
        this.brightness = 10;
        this.amount = 1.0f;
        this.turbulence = 1.0f;
        this.dispersion = 0.0f;
        this.time = 0.0f;
        this.samples = 2;
        this.bgColor = -8806401;
    }
    
    public void setScale(final float scale) {
        this.scale = scale;
    }
    
    public float getScale() {
        return this.scale;
    }
    
    public void setBrightness(final int brightness) {
        this.brightness = brightness;
    }
    
    public int getBrightness() {
        return this.brightness;
    }
    
    public void setTurbulence(final float turbulence) {
        this.turbulence = turbulence;
    }
    
    public float getTurbulence() {
        return this.turbulence;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    public void setDispersion(final float dispersion) {
        this.dispersion = dispersion;
    }
    
    public float getDispersion() {
        return this.dispersion;
    }
    
    public void setTime(final float time) {
        this.time = time;
    }
    
    public float getTime() {
        return this.time;
    }
    
    public void setSamples(final int samples) {
        this.samples = samples;
    }
    
    public int getSamples() {
        return this.samples;
    }
    
    public void setBgColor(final int c) {
        this.bgColor = c;
    }
    
    public int getBgColor() {
        return this.bgColor;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final Random random = new Random(0L);
        this.s = (float)Math.sin(0.1);
        this.c = (float)Math.cos(0.1);
        final int srcWidth = this.originalSpace.width;
        final int srcHeight = this.originalSpace.height;
        final int outWidth = transformedSpace.width;
        final int outHeight = transformedSpace.height;
        int index = 0;
        final int[] pixels = new int[outWidth * outHeight];
        for (int y = 0; y < outHeight; ++y) {
            for (int x = 0; x < outWidth; ++x) {
                pixels[index++] = this.bgColor;
            }
        }
        int v = this.brightness / this.samples;
        if (v == 0) {
            v = 1;
        }
        final float rs = 1.0f / this.scale;
        final float d = 0.95f;
        index = 0;
        for (int y2 = 0; y2 < outHeight; ++y2) {
            for (int x2 = 0; x2 < outWidth; ++x2) {
                for (int s = 0; s < this.samples; ++s) {
                    final float sx = x2 + random.nextFloat();
                    final float sy = y2 + random.nextFloat();
                    final float nx = sx * rs;
                    final float ny = sy * rs;
                    final float focus = 0.1f + this.amount;
                    final float xDisplacement = this.evaluate(nx - d, ny) - this.evaluate(nx + d, ny);
                    final float yDisplacement = this.evaluate(nx, ny + d) - this.evaluate(nx, ny - d);
                    if (this.dispersion > 0.0f) {
                        for (int c = 0; c < 3; ++c) {
                            final float ca = 1.0f + c * this.dispersion;
                            final float srcX = sx + this.scale * focus * xDisplacement * ca;
                            final float srcY = sy + this.scale * focus * yDisplacement * ca;
                            if (srcX >= 0.0f && srcX < outWidth - 1 && srcY >= 0.0f) {
                                if (srcY < outHeight - 1) {
                                    final int i = (int)srcY * outWidth + (int)srcX;
                                    final int rgb = pixels[i];
                                    int r = rgb >> 16 & 0xFF;
                                    int g = rgb >> 8 & 0xFF;
                                    int b = rgb & 0xFF;
                                    if (c == 2) {
                                        r += v;
                                    }
                                    else if (c == 1) {
                                        g += v;
                                    }
                                    else {
                                        b += v;
                                    }
                                    if (r > 255) {
                                        r = 255;
                                    }
                                    if (g > 255) {
                                        g = 255;
                                    }
                                    if (b > 255) {
                                        b = 255;
                                    }
                                    pixels[i] = (0xFF000000 | r << 16 | g << 8 | b);
                                }
                            }
                        }
                    }
                    else {
                        final float srcX2 = sx + this.scale * focus * xDisplacement;
                        final float srcY2 = sy + this.scale * focus * yDisplacement;
                        if (srcX2 >= 0.0f && srcX2 < outWidth - 1 && srcY2 >= 0.0f) {
                            if (srcY2 < outHeight - 1) {
                                final int j = (int)srcY2 * outWidth + (int)srcX2;
                                final int rgb2 = pixels[j];
                                int r2 = rgb2 >> 16 & 0xFF;
                                int g2 = rgb2 >> 8 & 0xFF;
                                int b2 = rgb2 & 0xFF;
                                r2 += v;
                                g2 += v;
                                b2 += v;
                                if (r2 > 255) {
                                    r2 = 255;
                                }
                                if (g2 > 255) {
                                    g2 = 255;
                                }
                                if (b2 > 255) {
                                    b2 = 255;
                                }
                                pixels[j] = (0xFF000000 | r2 << 16 | g2 << 8 | b2);
                            }
                        }
                    }
                }
            }
        }
        return pixels;
    }
    
    private static int add(final int rgb, final float brightness) {
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        r += (int)brightness;
        g += (int)brightness;
        b += (int)brightness;
        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
    
    private static int add(final int rgb, final float brightness, final int c) {
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        if (c == 2) {
            r += (int)brightness;
        }
        else if (c == 1) {
            g += (int)brightness;
        }
        else {
            b += (int)brightness;
        }
        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
    
    private static float turbulence2(float x, float y, final float time, final float octaves) {
        float value = 0.0f;
        final float lacunarity = 2.0f;
        float f = 1.0f;
        x += 371.0f;
        y += 529.0f;
        for (int i = 0; i < (int)octaves; ++i) {
            value += Noise.noise3(x, y, time) / f;
            x *= lacunarity;
            y *= lacunarity;
            f *= 2.0f;
        }
        final float remainder = octaves - (int)octaves;
        if (remainder != 0.0f) {
            value += remainder * Noise.noise3(x, y, time) / f;
        }
        return value;
    }
    
    private float evaluate(final float x, final float y) {
        final float xt = this.s * x + this.c * this.time;
        final float tt = this.c * x - this.c * this.time;
        final float f = (this.turbulence == 0.0) ? Noise.noise3(xt, y, tt) : turbulence2(xt, y, tt, this.turbulence);
        return f;
    }
    
    @Override
    public String toString() {
        return "Texture/Caustics...";
    }
}
