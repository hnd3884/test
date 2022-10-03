package com.jhlabs.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.image.BufferedImageOp;

public class BrushedMetalFilter implements BufferedImageOp
{
    private int radius;
    private float amount;
    private int color;
    private float shine;
    private boolean monochrome;
    private Random randomNumbers;
    
    public BrushedMetalFilter() {
        this.radius = 10;
        this.amount = 0.1f;
        this.color = -7829368;
        this.shine = 0.1f;
        this.monochrome = true;
    }
    
    public BrushedMetalFilter(final int color, final int radius, final float amount, final boolean monochrome, final float shine) {
        this.radius = 10;
        this.amount = 0.1f;
        this.color = -7829368;
        this.shine = 0.1f;
        this.monochrome = true;
        this.color = color;
        this.radius = radius;
        this.amount = amount;
        this.monochrome = monochrome;
        this.shine = shine;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final int[] inPixels = new int[width];
        final int[] outPixels = new int[width];
        this.randomNumbers = new Random(0L);
        final int a = this.color & 0xFF000000;
        final int r = this.color >> 16 & 0xFF;
        final int g = this.color >> 8 & 0xFF;
        final int b = this.color & 0xFF;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int tr = r;
                int tg = g;
                int tb = b;
                if (this.shine != 0.0f) {
                    final int f = (int)(255.0f * this.shine * Math.sin(x / (double)width * 3.141592653589793));
                    tr += f;
                    tg += f;
                    tb += f;
                }
                if (this.monochrome) {
                    final int n = (int)(255.0f * (2.0f * this.randomNumbers.nextFloat() - 1.0f) * this.amount);
                    inPixels[x] = (a | clamp(tr + n) << 16 | clamp(tg + n) << 8 | clamp(tb + n));
                }
                else {
                    inPixels[x] = (a | this.random(tr) << 16 | this.random(tg) << 8 | this.random(tb));
                }
            }
            if (this.radius != 0) {
                this.blur(inPixels, outPixels, width, this.radius);
                this.setRGB(dst, 0, y, width, 1, outPixels);
            }
            else {
                this.setRGB(dst, 0, y, width, 1, inPixels);
            }
        }
        return dst;
    }
    
    private int random(int x) {
        x += (int)(255.0f * (2.0f * this.randomNumbers.nextFloat() - 1.0f) * this.amount);
        if (x < 0) {
            x = 0;
        }
        else if (x > 255) {
            x = 255;
        }
        return x;
    }
    
    private static int clamp(final int c) {
        if (c < 0) {
            return 0;
        }
        if (c > 255) {
            return 255;
        }
        return c;
    }
    
    private static int mod(int a, final int b) {
        final int n = a / b;
        a -= n * b;
        if (a < 0) {
            return a + b;
        }
        return a;
    }
    
    public void blur(final int[] in, final int[] out, final int width, final int radius) {
        final int widthMinus1 = width - 1;
        final int r2 = 2 * radius + 1;
        int tr = 0;
        int tg = 0;
        int tb = 0;
        for (int i = -radius; i <= radius; ++i) {
            final int rgb = in[mod(i, width)];
            tr += (rgb >> 16 & 0xFF);
            tg += (rgb >> 8 & 0xFF);
            tb += (rgb & 0xFF);
        }
        for (int x = 0; x < width; ++x) {
            out[x] = (0xFF000000 | tr / r2 << 16 | tg / r2 << 8 | tb / r2);
            int i2 = x + radius + 1;
            if (i2 > widthMinus1) {
                i2 = mod(i2, width);
            }
            int i3 = x - radius;
            if (i3 < 0) {
                i3 = mod(i3, width);
            }
            final int rgb2 = in[i2];
            final int rgb3 = in[i3];
            tr += (rgb2 & 0xFF0000) - (rgb3 & 0xFF0000) >> 16;
            tg += (rgb2 & 0xFF00) - (rgb3 & 0xFF00) >> 8;
            tb += (rgb2 & 0xFF) - (rgb3 & 0xFF);
        }
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    public void setShine(final float shine) {
        this.shine = shine;
    }
    
    public float getShine() {
        return this.shine;
    }
    
    public void setColor(final int color) {
        this.color = color;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public void setMonochrome(final boolean monochrome) {
        this.monochrome = monochrome;
    }
    
    public boolean getMonochrome() {
        return this.monochrome;
    }
    
    public BufferedImage createCompatibleDestImage(final BufferedImage src, ColorModel dstCM) {
        if (dstCM == null) {
            dstCM = src.getColorModel();
        }
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
    }
    
    public Rectangle2D getBounds2D(final BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }
    
    public Point2D getPoint2D(final Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }
    
    public RenderingHints getRenderingHints() {
        return null;
    }
    
    private void setRGB(final BufferedImage image, final int x, final int y, final int width, final int height, final int[] pixels) {
        final int type = image.getType();
        if (type == 2 || type == 1) {
            image.getRaster().setDataElements(x, y, width, height, pixels);
        }
        else {
            image.setRGB(x, y, width, height, pixels, 0, width);
        }
    }
    
    @Override
    public String toString() {
        return "Texture/Brushed Metal...";
    }
}
