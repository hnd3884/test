package com.jhlabs.composite;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.CompositeContext;
import java.awt.Composite;

public abstract class RGBComposite implements Composite
{
    protected float extraAlpha;
    
    public RGBComposite() {
        this(1.0f);
    }
    
    public RGBComposite(final float alpha) {
        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException("RGBComposite: alpha must be between 0 and 1");
        }
        this.extraAlpha = alpha;
    }
    
    public float getAlpha() {
        return this.extraAlpha;
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.extraAlpha);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RGBComposite)) {
            return false;
        }
        final RGBComposite c = (RGBComposite)o;
        return this.extraAlpha == c.extraAlpha;
    }
    
    public abstract static class RGBCompositeContext implements CompositeContext
    {
        private float alpha;
        private ColorModel srcColorModel;
        private ColorModel dstColorModel;
        
        public RGBCompositeContext(final float alpha, final ColorModel srcColorModel, final ColorModel dstColorModel) {
            this.alpha = alpha;
            this.srcColorModel = srcColorModel;
            this.dstColorModel = dstColorModel;
        }
        
        public void dispose() {
        }
        
        static int multiply255(final int a, final int b) {
            final int t = a * b + 128;
            return (t >> 8) + t >> 8;
        }
        
        static int clamp(final int a) {
            return (a < 0) ? 0 : ((a > 255) ? 255 : a);
        }
        
        public abstract void composeRGB(final int[] p0, final int[] p1, final float p2);
        
        public void compose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
            final float alpha = this.alpha;
            int[] srcPix = null;
            int[] dstPix = null;
            final int x = dstOut.getMinX();
            final int w = dstOut.getWidth();
            final int y0 = dstOut.getMinY();
            for (int y2 = y0 + dstOut.getHeight(), y3 = y0; y3 < y2; ++y3) {
                srcPix = src.getPixels(x, y3, w, 1, srcPix);
                dstPix = dstIn.getPixels(x, y3, w, 1, dstPix);
                this.composeRGB(srcPix, dstPix, alpha);
                dstOut.setPixels(x, y3, w, 1, dstPix);
            }
        }
    }
}
