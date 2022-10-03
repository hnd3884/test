package com.jhlabs.image;

import java.awt.Rectangle;

public class ShapeFilter extends WholeImageFilter
{
    public static final int LINEAR = 0;
    public static final int CIRCLE_UP = 1;
    public static final int CIRCLE_DOWN = 2;
    public static final int SMOOTH = 3;
    private float factor;
    protected Colormap colormap;
    private boolean useAlpha;
    private boolean invert;
    private boolean merge;
    private int type;
    private static final int one = 41;
    private static final int sqrt2;
    private static final int sqrt5;
    
    public ShapeFilter() {
        this.factor = 1.0f;
        this.useAlpha = true;
        this.invert = false;
        this.merge = false;
        this.colormap = new LinearColormap();
    }
    
    public void setFactor(final float factor) {
        this.factor = factor;
    }
    
    public float getFactor() {
        return this.factor;
    }
    
    public void setColormap(final Colormap colormap) {
        this.colormap = colormap;
    }
    
    public Colormap getColormap() {
        return this.colormap;
    }
    
    public void setUseAlpha(final boolean useAlpha) {
        this.useAlpha = useAlpha;
    }
    
    public boolean getUseAlpha() {
        return this.useAlpha;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setInvert(final boolean invert) {
        this.invert = invert;
    }
    
    public boolean getInvert() {
        return this.invert;
    }
    
    public void setMerge(final boolean merge) {
        this.merge = merge;
    }
    
    public boolean getMerge() {
        return this.merge;
    }
    
    @Override
    protected int[] filterPixels(final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final int[] map = new int[width * height];
        this.makeMap(inPixels, map, width, height);
        final int max = this.distanceMap(map, width, height);
        this.applyMap(map, inPixels, width, height, max);
        return inPixels;
    }
    
    public int distanceMap(final int[] map, final int width, final int height) {
        final int xmax = width - 3;
        final int ymax = height - 3;
        int max = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int offset = x + y * width;
                if (map[offset] > 0) {
                    int v;
                    if (x < 2 || x > xmax || y < 2 || y > ymax) {
                        v = this.setEdgeValue(x, y, map, width, offset, xmax, ymax);
                    }
                    else {
                        v = this.setValue(map, width, offset);
                    }
                    if (v > max) {
                        max = v;
                    }
                }
            }
        }
        for (int y = height - 1; y >= 0; --y) {
            for (int x = width - 1; x >= 0; --x) {
                final int offset = x + y * width;
                if (map[offset] > 0) {
                    int v;
                    if (x < 2 || x > xmax || y < 2 || y > ymax) {
                        v = this.setEdgeValue(x, y, map, width, offset, xmax, ymax);
                    }
                    else {
                        v = this.setValue(map, width, offset);
                    }
                    if (v > max) {
                        max = v;
                    }
                }
            }
        }
        return max;
    }
    
    private void makeMap(final int[] pixels, final int[] map, final int width, final int height) {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int offset = x + y * width;
                final int b = this.useAlpha ? (pixels[offset] >> 24 & 0xFF) : PixelUtils.brightness(pixels[offset]);
                map[offset] = b * 41 / 10;
            }
        }
    }
    
    private void applyMap(final int[] map, final int[] pixels, final int width, final int height, int max) {
        if (max == 0) {
            max = 1;
        }
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int offset = x + y * width;
                final int m = map[offset];
                float v = 0.0f;
                int sa = 0;
                int sr = 0;
                int sg = 0;
                int sb = 0;
                if (m == 0) {
                    sr = (sa = (sg = (sb = 0)));
                    sa = (pixels[offset] >> 24 & 0xFF);
                }
                else {
                    v = ImageMath.clamp(this.factor * m / max, 0.0f, 1.0f);
                    switch (this.type) {
                        case 1: {
                            v = ImageMath.circleUp(v);
                            break;
                        }
                        case 2: {
                            v = ImageMath.circleDown(v);
                            break;
                        }
                        case 3: {
                            v = ImageMath.smoothStep(0.0f, 1.0f, v);
                            break;
                        }
                    }
                    if (this.colormap == null) {
                        sg = (sr = (sb = (int)(v * 255.0f)));
                    }
                    else {
                        final int c = this.colormap.getColor(v);
                        sr = (c >> 16 & 0xFF);
                        sg = (c >> 8 & 0xFF);
                        sb = (c & 0xFF);
                    }
                    sa = (this.useAlpha ? (pixels[offset] >> 24 & 0xFF) : PixelUtils.brightness(pixels[offset]));
                    if (this.invert) {
                        sr = 255 - sr;
                        sg = 255 - sg;
                        sb = 255 - sb;
                    }
                }
                if (this.merge) {
                    final int transp = 255;
                    final int col = pixels[offset];
                    final int a = (col & 0xFF000000) >> 24;
                    int r = (col & 0xFF0000) >> 16;
                    int g = (col & 0xFF00) >> 8;
                    int b = col & 0xFF;
                    r = sr * r / transp;
                    g = sg * g / transp;
                    b = sb * b / transp;
                    if (r < 0) {
                        r = 0;
                    }
                    if (r > 255) {
                        r = 255;
                    }
                    if (g < 0) {
                        g = 0;
                    }
                    if (g > 255) {
                        g = 255;
                    }
                    if (b < 0) {
                        b = 0;
                    }
                    if (b > 255) {
                        b = 255;
                    }
                    pixels[offset] = (a << 24 | r << 16 | g << 8 | b);
                }
                else {
                    pixels[offset] = (sa << 24 | sr << 16 | sg << 8 | sb);
                }
            }
        }
    }
    
    private int setEdgeValue(final int x, final int y, final int[] map, final int width, final int offset, final int xmax, final int ymax) {
        final int r1 = offset - width - width - 2;
        final int r2 = r1 + width;
        final int r3 = r2 + width;
        final int r4 = r3 + width;
        final int r5 = r4 + width;
        if (y == 0 || x == 0 || y == ymax + 2 || x == xmax + 2) {
            return map[offset] = 41;
        }
        int min;
        int v = min = map[r2 + 2] + 41;
        v = map[r3 + 1] + 41;
        if (v < min) {
            min = v;
        }
        v = map[r3 + 3] + 41;
        if (v < min) {
            min = v;
        }
        v = map[r4 + 2] + 41;
        if (v < min) {
            min = v;
        }
        v = map[r2 + 1] + ShapeFilter.sqrt2;
        if (v < min) {
            min = v;
        }
        v = map[r2 + 3] + ShapeFilter.sqrt2;
        if (v < min) {
            min = v;
        }
        v = map[r4 + 1] + ShapeFilter.sqrt2;
        if (v < min) {
            min = v;
        }
        v = map[r4 + 3] + ShapeFilter.sqrt2;
        if (v < min) {
            min = v;
        }
        if (y == 1 || x == 1 || y == ymax + 1 || x == xmax + 1) {
            return map[offset] = min;
        }
        v = map[r1 + 1] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r1 + 3] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r2 + 4] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r4 + 4] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r5 + 3] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r5 + 1] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r4] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r2] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        return map[offset] = min;
    }
    
    private int setValue(final int[] map, final int width, final int offset) {
        final int r1 = offset - width - width - 2;
        final int r2 = r1 + width;
        final int r3 = r2 + width;
        final int r4 = r3 + width;
        final int r5 = r4 + width;
        int min;
        int v = min = map[r2 + 2] + 41;
        v = map[r3 + 1] + 41;
        if (v < min) {
            min = v;
        }
        v = map[r3 + 3] + 41;
        if (v < min) {
            min = v;
        }
        v = map[r4 + 2] + 41;
        if (v < min) {
            min = v;
        }
        v = map[r2 + 1] + ShapeFilter.sqrt2;
        if (v < min) {
            min = v;
        }
        v = map[r2 + 3] + ShapeFilter.sqrt2;
        if (v < min) {
            min = v;
        }
        v = map[r4 + 1] + ShapeFilter.sqrt2;
        if (v < min) {
            min = v;
        }
        v = map[r4 + 3] + ShapeFilter.sqrt2;
        if (v < min) {
            min = v;
        }
        v = map[r1 + 1] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r1 + 3] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r2 + 4] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r4 + 4] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r5 + 3] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r5 + 1] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r4] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        v = map[r2] + ShapeFilter.sqrt5;
        if (v < min) {
            min = v;
        }
        return map[offset] = min;
    }
    
    @Override
    public String toString() {
        return "Stylize/Shapeburst...";
    }
    
    static {
        sqrt2 = (int)(41.0 * Math.sqrt(2.0));
        sqrt5 = (int)(41.0 * Math.sqrt(5.0));
    }
}
