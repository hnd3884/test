package com.jhlabs.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class VariableBlurFilter extends AbstractBufferedImageOp
{
    private int hRadius;
    private int vRadius;
    private int iterations;
    private BufferedImage blurMask;
    private boolean premultiplyAlpha;
    
    public VariableBlurFilter() {
        this.hRadius = 1;
        this.vRadius = 1;
        this.iterations = 1;
        this.premultiplyAlpha = true;
    }
    
    public void setPremultiplyAlpha(final boolean premultiplyAlpha) {
        this.premultiplyAlpha = premultiplyAlpha;
    }
    
    public boolean getPremultiplyAlpha() {
        return this.premultiplyAlpha;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            dst = new BufferedImage(width, height, 2);
        }
        final int[] inPixels = new int[width * height];
        final int[] outPixels = new int[width * height];
        this.getRGB(src, 0, 0, width, height, inPixels);
        if (this.premultiplyAlpha) {
            ImageMath.premultiply(inPixels, 0, inPixels.length);
        }
        for (int i = 0; i < this.iterations; ++i) {
            this.blur(inPixels, outPixels, width, height, this.hRadius, 1);
            this.blur(outPixels, inPixels, height, width, this.vRadius, 2);
        }
        if (this.premultiplyAlpha) {
            ImageMath.unpremultiply(inPixels, 0, inPixels.length);
        }
        this.setRGB(dst, 0, 0, width, height, inPixels);
        return dst;
    }
    
    @Override
    public BufferedImage createCompatibleDestImage(final BufferedImage src, ColorModel dstCM) {
        if (dstCM == null) {
            dstCM = src.getColorModel();
        }
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
    }
    
    @Override
    public Rectangle2D getBounds2D(final BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }
    
    @Override
    public Point2D getPoint2D(final Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }
    
    public void blur(final int[] in, final int[] out, final int width, final int height, final int radius, final int pass) {
        final int widthMinus1 = width - 1;
        final int[] r = new int[width];
        final int[] g = new int[width];
        final int[] b = new int[width];
        final int[] a = new int[width];
        final int[] mask = new int[width];
        int inIndex = 0;
        for (int y = 0; y < height; ++y) {
            int outIndex = y;
            if (this.blurMask != null) {
                if (pass == 1) {
                    this.getRGB(this.blurMask, 0, y, width, 1, mask);
                }
                else {
                    this.getRGB(this.blurMask, y, 0, 1, width, mask);
                }
            }
            for (int x = 0; x < width; ++x) {
                final int argb = in[inIndex + x];
                a[x] = (argb >> 24 & 0xFF);
                r[x] = (argb >> 16 & 0xFF);
                g[x] = (argb >> 8 & 0xFF);
                b[x] = (argb & 0xFF);
                if (x != 0) {
                    final int[] array = a;
                    final int n = x;
                    array[n] += a[x - 1];
                    final int[] array2 = r;
                    final int n2 = x;
                    array2[n2] += r[x - 1];
                    final int[] array3 = g;
                    final int n3 = x;
                    array3[n3] += g[x - 1];
                    final int[] array4 = b;
                    final int n4 = x;
                    array4[n4] += b[x - 1];
                }
            }
            for (int x = 0; x < width; ++x) {
                int ra;
                if (this.blurMask != null) {
                    if (pass == 1) {
                        ra = (int)((mask[x] & 0xFF) * this.hRadius / 255.0f);
                    }
                    else {
                        ra = (int)((mask[x] & 0xFF) * this.vRadius / 255.0f);
                    }
                }
                else if (pass == 1) {
                    ra = (int)(this.blurRadiusAt(x, y, width, height) * this.hRadius);
                }
                else {
                    ra = (int)(this.blurRadiusAt(y, x, height, width) * this.vRadius);
                }
                final int divisor = 2 * ra + 1;
                int ta = 0;
                int tr = 0;
                int tg = 0;
                int tb = 0;
                int i1 = x + ra;
                if (i1 > widthMinus1) {
                    final int f = i1 - widthMinus1;
                    final int l = widthMinus1;
                    ta += (a[l] - a[l - 1]) * f;
                    tr += (r[l] - r[l - 1]) * f;
                    tg += (g[l] - g[l - 1]) * f;
                    tb += (b[l] - b[l - 1]) * f;
                    i1 = widthMinus1;
                }
                int i2 = x - ra - 1;
                if (i2 < 0) {
                    ta -= a[0] * i2;
                    tr -= r[0] * i2;
                    tg -= g[0] * i2;
                    tb -= b[0] * i2;
                    i2 = 0;
                }
                ta += a[i1] - a[i2];
                tr += r[i1] - r[i2];
                tg += g[i1] - g[i2];
                tb += b[i1] - b[i2];
                out[outIndex] = (ta / divisor << 24 | tr / divisor << 16 | tg / divisor << 8 | tb / divisor);
                outIndex += height;
            }
            inIndex += width;
        }
    }
    
    protected float blurRadiusAt(final int x, final int y, final int width, final int height) {
        return x / (float)width;
    }
    
    public void setHRadius(final int hRadius) {
        this.hRadius = hRadius;
    }
    
    public int getHRadius() {
        return this.hRadius;
    }
    
    public void setVRadius(final int vRadius) {
        this.vRadius = vRadius;
    }
    
    public int getVRadius() {
        return this.vRadius;
    }
    
    public void setRadius(final int radius) {
        this.vRadius = radius;
        this.hRadius = radius;
    }
    
    public int getRadius() {
        return this.hRadius;
    }
    
    public void setIterations(final int iterations) {
        this.iterations = iterations;
    }
    
    public int getIterations() {
        return this.iterations;
    }
    
    public void setBlurMask(final BufferedImage blurMask) {
        this.blurMask = blurMask;
    }
    
    public BufferedImage getBlurMask() {
        return this.blurMask;
    }
    
    @Override
    public String toString() {
        return "Blur/Variable Blur...";
    }
}
