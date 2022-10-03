package com.jhlabs.image;

import com.jhlabs.math.FFT;
import java.awt.image.BufferedImage;

public class LensBlurFilter extends AbstractBufferedImageOp
{
    private float radius;
    private float bloom;
    private float bloomThreshold;
    private float angle;
    private int sides;
    
    public LensBlurFilter() {
        this.radius = 10.0f;
        this.bloom = 2.0f;
        this.bloomThreshold = 192.0f;
        this.angle = 0.0f;
        this.sides = 5;
    }
    
    public void setRadius(final float radius) {
        this.radius = radius;
    }
    
    public float getRadius() {
        return this.radius;
    }
    
    public void setSides(final int sides) {
        this.sides = sides;
    }
    
    public int getSides() {
        return this.sides;
    }
    
    public void setBloom(final float bloom) {
        this.bloom = bloom;
    }
    
    public float getBloom() {
        return this.bloom;
    }
    
    public void setBloomThreshold(final float bloomThreshold) {
        this.bloomThreshold = bloomThreshold;
    }
    
    public float getBloomThreshold() {
        return this.bloomThreshold;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        int rows = 1;
        int cols = 1;
        int log2rows = 0;
        int log2cols = 0;
        final int iradius = (int)Math.ceil(this.radius);
        int tileHeight;
        int tileWidth = tileHeight = 128;
        final int adjustedWidth = width + iradius * 2;
        final int adjustedHeight = height + iradius * 2;
        tileWidth = ((iradius < 32) ? Math.min(128, width + 2 * iradius) : Math.min(256, width + 2 * iradius));
        tileHeight = ((iradius < 32) ? Math.min(128, height + 2 * iradius) : Math.min(256, height + 2 * iradius));
        if (dst == null) {
            dst = new BufferedImage(width, height, 2);
        }
        while (rows < tileHeight) {
            rows *= 2;
            ++log2rows;
        }
        while (cols < tileWidth) {
            cols *= 2;
            ++log2cols;
        }
        final int w = cols;
        final int h = rows;
        tileWidth = w;
        tileHeight = h;
        final FFT fft = new FFT(Math.max(log2rows, log2cols));
        final int[] rgb = new int[w * h];
        final float[][] mask = new float[2][w * h];
        final float[][] gb = new float[2][w * h];
        final float[][] ar = new float[2][w * h];
        final double polyAngle = 3.141592653589793 / this.sides;
        final double polyScale = 1.0 / Math.cos(polyAngle);
        final double r2 = this.radius * this.radius;
        final double rangle = Math.toRadians(this.angle);
        float total = 0.0f;
        int i = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final double dx = x - w / 2.0f;
                final double dy = y - h / 2.0f;
                double r3 = dx * dx + dy * dy;
                double f = (r3 < r2) ? 1.0 : 0.0;
                if (f != 0.0) {
                    r3 = Math.sqrt(r3);
                    if (this.sides != 0) {
                        double a = Math.atan2(dy, dx) + rangle;
                        a = ImageMath.mod(a, polyAngle * 2.0) - polyAngle;
                        f = Math.cos(a) * polyScale;
                    }
                    else {
                        f = 1.0;
                    }
                    f = ((f * r3 < this.radius) ? 1.0 : 0.0);
                }
                total += (float)f;
                mask[0][i] = (float)f;
                mask[1][i] = 0.0f;
                ++i;
            }
        }
        i = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final float[] array = mask[0];
                final int n = i;
                array[n] /= total;
                ++i;
            }
        }
        fft.transform2D(mask[0], mask[1], w, h, true);
        for (int tileY = -iradius; tileY < height; tileY += tileHeight - 2 * iradius) {
            for (int tileX = -iradius; tileX < width; tileX += tileWidth - 2 * iradius) {
                int tx = tileX;
                int ty = tileY;
                int tw = tileWidth;
                int th = tileHeight;
                int fx = 0;
                int fy = 0;
                if (tx < 0) {
                    tw += tx;
                    fx -= tx;
                    tx = 0;
                }
                if (ty < 0) {
                    th += ty;
                    fy -= ty;
                    ty = 0;
                }
                if (tx + tw > width) {
                    tw = width - tx;
                }
                if (ty + th > height) {
                    th = height - ty;
                }
                src.getRGB(tx, ty, tw, th, rgb, fy * w + fx, w);
                i = 0;
                for (int y2 = 0; y2 < h; ++y2) {
                    final int imageY = y2 + tileY;
                    int j;
                    if (imageY < 0) {
                        j = fy;
                    }
                    else if (imageY > height) {
                        j = fy + th - 1;
                    }
                    else {
                        j = y2;
                    }
                    j *= w;
                    for (int x2 = 0; x2 < w; ++x2) {
                        final int imageX = x2 + tileX;
                        int k;
                        if (imageX < 0) {
                            k = fx;
                        }
                        else if (imageX > width) {
                            k = fx + tw - 1;
                        }
                        else {
                            k = x2;
                        }
                        k += j;
                        ar[0][i] = (float)(rgb[k] >> 24 & 0xFF);
                        float r4 = (float)(rgb[k] >> 16 & 0xFF);
                        float g = (float)(rgb[k] >> 8 & 0xFF);
                        float b = (float)(rgb[k] & 0xFF);
                        if (r4 > this.bloomThreshold) {
                            r4 *= this.bloom;
                        }
                        if (g > this.bloomThreshold) {
                            g *= this.bloom;
                        }
                        if (b > this.bloomThreshold) {
                            b *= this.bloom;
                        }
                        ar[1][i] = r4;
                        gb[0][i] = g;
                        gb[1][i] = b;
                        ++i;
                        ++k;
                    }
                }
                fft.transform2D(ar[0], ar[1], cols, rows, true);
                fft.transform2D(gb[0], gb[1], cols, rows, true);
                i = 0;
                for (int y2 = 0; y2 < h; ++y2) {
                    for (int x3 = 0; x3 < w; ++x3) {
                        float re = ar[0][i];
                        float im = ar[1][i];
                        final float rem = mask[0][i];
                        final float imm = mask[1][i];
                        ar[0][i] = re * rem - im * imm;
                        ar[1][i] = re * imm + im * rem;
                        re = gb[0][i];
                        im = gb[1][i];
                        gb[0][i] = re * rem - im * imm;
                        gb[1][i] = re * imm + im * rem;
                        ++i;
                    }
                }
                fft.transform2D(ar[0], ar[1], cols, rows, false);
                fft.transform2D(gb[0], gb[1], cols, rows, false);
                final int row_flip = w >> 1;
                final int col_flip = h >> 1;
                int index = 0;
                for (int y3 = 0; y3 < w; ++y3) {
                    final int ym = y3 ^ row_flip;
                    final int yi = ym * cols;
                    for (int x4 = 0; x4 < w; ++x4) {
                        final int xm = yi + (x4 ^ col_flip);
                        final int a2 = (int)ar[0][xm];
                        int r5 = (int)ar[1][xm];
                        int g2 = (int)gb[0][xm];
                        int b2 = (int)gb[1][xm];
                        if (r5 > 255) {
                            r5 = 255;
                        }
                        if (g2 > 255) {
                            g2 = 255;
                        }
                        if (b2 > 255) {
                            b2 = 255;
                        }
                        final int argb = a2 << 24 | r5 << 16 | g2 << 8 | b2;
                        rgb[index++] = argb;
                    }
                }
                tx = tileX + iradius;
                ty = tileY + iradius;
                tw = tileWidth - 2 * iradius;
                th = tileHeight - 2 * iradius;
                if (tx + tw > width) {
                    tw = width - tx;
                }
                if (ty + th > height) {
                    th = height - ty;
                }
                dst.setRGB(tx, ty, tw, th, rgb, iradius * w + iradius, w);
            }
        }
        return dst;
    }
    
    @Override
    public String toString() {
        return "Blur/Lens Blur...";
    }
}
