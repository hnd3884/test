package com.jhlabs.image;

import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.Point;

public class GradientFilter extends AbstractBufferedImageOp
{
    public static final int LINEAR = 0;
    public static final int BILINEAR = 1;
    public static final int RADIAL = 2;
    public static final int CONICAL = 3;
    public static final int BICONICAL = 4;
    public static final int SQUARE = 5;
    public static final int INT_LINEAR = 0;
    public static final int INT_CIRCLE_UP = 1;
    public static final int INT_CIRCLE_DOWN = 2;
    public static final int INT_SMOOTH = 3;
    private float angle;
    private int color1;
    private int color2;
    private Point p1;
    private Point p2;
    private boolean repeat;
    private float x1;
    private float y1;
    private float dx;
    private float dy;
    private Colormap colormap;
    private int type;
    private int interpolation;
    private int paintMode;
    
    public GradientFilter() {
        this.angle = 0.0f;
        this.color1 = -16777216;
        this.color2 = -1;
        this.p1 = new Point(0, 0);
        this.p2 = new Point(64, 64);
        this.repeat = false;
        this.colormap = null;
        this.interpolation = 0;
        this.paintMode = 1;
    }
    
    public GradientFilter(final Point p1, final Point p2, final int color1, final int color2, final boolean repeat, final int type, final int interpolation) {
        this.angle = 0.0f;
        this.color1 = -16777216;
        this.color2 = -1;
        this.p1 = new Point(0, 0);
        this.p2 = new Point(64, 64);
        this.repeat = false;
        this.colormap = null;
        this.interpolation = 0;
        this.paintMode = 1;
        this.p1 = p1;
        this.p2 = p2;
        this.color1 = color1;
        this.color2 = color2;
        this.repeat = repeat;
        this.type = type;
        this.interpolation = interpolation;
        this.colormap = new LinearColormap(color1, color2);
    }
    
    public void setPoint1(final Point point1) {
        this.p1 = point1;
    }
    
    public Point getPoint1() {
        return this.p1;
    }
    
    public void setPoint2(final Point point2) {
        this.p2 = point2;
    }
    
    public Point getPoint2() {
        return this.p2;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setInterpolation(final int interpolation) {
        this.interpolation = interpolation;
    }
    
    public int getInterpolation() {
        return this.interpolation;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
        this.p2 = new Point((int)(64.0 * Math.cos(angle)), (int)(64.0 * Math.sin(angle)));
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    public void setPaintMode(final int paintMode) {
        this.paintMode = paintMode;
    }
    
    public int getPaintMode() {
        return this.paintMode;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        float x1 = (float)this.p1.x;
        float x2 = (float)this.p2.x;
        float y1;
        float y2;
        if (x1 > x2 && this.type != 2) {
            y1 = x1;
            x1 = x2;
            x2 = y1;
            y1 = (float)this.p2.y;
            y2 = (float)this.p1.y;
            final int rgb1 = this.color2;
            final int rgb2 = this.color1;
        }
        else {
            y1 = (float)this.p1.y;
            y2 = (float)this.p2.y;
            final int rgb1 = this.color1;
            final int rgb2 = this.color2;
        }
        float dx = x2 - x1;
        float dy = y2 - y1;
        final float lenSq = dx * dx + dy * dy;
        this.x1 = x1;
        this.y1 = y1;
        if (lenSq >= Float.MIN_VALUE) {
            dx /= lenSq;
            dy /= lenSq;
            if (this.repeat) {
                dx %= 1.0f;
                dy %= 1.0f;
            }
        }
        this.dx = dx;
        this.dy = dy;
        final int[] pixels = new int[width];
        for (int y3 = 0; y3 < height; ++y3) {
            this.getRGB(src, 0, y3, width, 1, pixels);
            switch (this.type) {
                case 0:
                case 1: {
                    this.linearGradient(pixels, y3, width, 1);
                    break;
                }
                case 2: {
                    this.radialGradient(pixels, y3, width, 1);
                    break;
                }
                case 3:
                case 4: {
                    this.conicalGradient(pixels, y3, width, 1);
                    break;
                }
                case 5: {
                    this.squareGradient(pixels, y3, width, 1);
                    break;
                }
            }
            this.setRGB(dst, 0, y3, width, 1, pixels);
        }
        return dst;
    }
    
    private void repeatGradient(final int[] pixels, final int w, final int h, float rowrel, final float dx, final float dy) {
        int off = 0;
        for (int y = 0; y < h; ++y) {
            float colrel = rowrel;
            int j = w;
            while (--j >= 0) {
                int rgb;
                if (this.type == 1) {
                    rgb = this.colormap.getColor(this.map(ImageMath.triangle(colrel)));
                }
                else {
                    rgb = this.colormap.getColor(this.map(ImageMath.mod(colrel, 1.0f)));
                }
                pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
                ++off;
                colrel += dx;
            }
            rowrel += dy;
        }
    }
    
    private void singleGradient(final int[] pixels, final int w, final int h, float rowrel, final float dx, final float dy) {
        int off = 0;
        for (int y = 0; y < h; ++y) {
            float colrel = rowrel;
            int j = w;
            if (colrel <= 0.0) {
                final int rgb = this.colormap.getColor(0.0f);
                do {
                    pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
                    ++off;
                    colrel += dx;
                } while (--j > 0 && colrel <= 0.0);
            }
            while (colrel < 1.0 && --j >= 0) {
                int rgb;
                if (this.type == 1) {
                    rgb = this.colormap.getColor(this.map(ImageMath.triangle(colrel)));
                }
                else {
                    rgb = this.colormap.getColor(this.map(colrel));
                }
                pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
                ++off;
                colrel += dx;
            }
            if (j > 0) {
                int rgb;
                if (this.type == 1) {
                    rgb = this.colormap.getColor(0.0f);
                }
                else {
                    rgb = this.colormap.getColor(1.0f);
                }
                do {
                    pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
                    ++off;
                } while (--j > 0);
            }
            rowrel += dy;
        }
    }
    
    private void linearGradient(final int[] pixels, final int y, final int w, final int h) {
        final int x = 0;
        final float rowrel = (x - this.x1) * this.dx + (y - this.y1) * this.dy;
        if (this.repeat) {
            this.repeatGradient(pixels, w, h, rowrel, this.dx, this.dy);
        }
        else {
            this.singleGradient(pixels, w, h, rowrel, this.dx, this.dy);
        }
    }
    
    private void radialGradient(final int[] pixels, final int y, final int w, final int h) {
        int off = 0;
        final float radius = this.distance((float)(this.p2.x - this.p1.x), (float)(this.p2.y - this.p1.y));
        for (int x = 0; x < w; ++x) {
            final float distance = this.distance((float)(x - this.p1.x), (float)(y - this.p1.y));
            float ratio = distance / radius;
            if (this.repeat) {
                ratio %= 2.0f;
            }
            else if (ratio > 1.0) {
                ratio = 1.0f;
            }
            final int rgb = this.colormap.getColor(this.map(ratio));
            pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
            ++off;
        }
    }
    
    private void squareGradient(final int[] pixels, final int y, final int w, final int h) {
        int off = 0;
        final float radius = (float)Math.max(Math.abs(this.p2.x - this.p1.x), Math.abs(this.p2.y - this.p1.y));
        for (int x = 0; x < w; ++x) {
            final float distance = (float)Math.max(Math.abs(x - this.p1.x), Math.abs(y - this.p1.y));
            float ratio = distance / radius;
            if (this.repeat) {
                ratio %= 2.0f;
            }
            else if (ratio > 1.0) {
                ratio = 1.0f;
            }
            final int rgb = this.colormap.getColor(this.map(ratio));
            pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
            ++off;
        }
    }
    
    private void conicalGradient(final int[] pixels, final int y, final int w, final int h) {
        int off = 0;
        final float angle0 = (float)Math.atan2(this.p2.x - this.p1.x, this.p2.y - this.p1.y);
        for (int x = 0; x < w; ++x) {
            float angle2 = (float)(Math.atan2(x - this.p1.x, y - this.p1.y) - angle0) / 6.2831855f;
            ++angle2;
            angle2 %= 1.0f;
            if (this.type == 4) {
                angle2 = ImageMath.triangle(angle2);
            }
            final int rgb = this.colormap.getColor(this.map(angle2));
            pixels[off] = PixelUtils.combinePixels(rgb, pixels[off], this.paintMode);
            ++off;
        }
    }
    
    private float map(float v) {
        if (this.repeat) {
            v = ((v > 1.0) ? (2.0f - v) : v);
        }
        switch (this.interpolation) {
            case 1: {
                v = ImageMath.circleUp(ImageMath.clamp(v, 0.0f, 1.0f));
                break;
            }
            case 2: {
                v = ImageMath.circleDown(ImageMath.clamp(v, 0.0f, 1.0f));
                break;
            }
            case 3: {
                v = ImageMath.smoothStep(0.0f, 1.0f, v);
                break;
            }
        }
        return v;
    }
    
    private float distance(final float a, final float b) {
        return (float)Math.sqrt(a * a + b * b);
    }
    
    @Override
    public String toString() {
        return "Other/Gradient Fill...";
    }
}
