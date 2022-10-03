package com.jhlabs.composite;

import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public final class ExclusionComposite extends RGBComposite
{
    public ExclusionComposite(final float alpha) {
        super(alpha);
    }
    
    public CompositeContext createContext(final ColorModel srcColorModel, final ColorModel dstColorModel, final RenderingHints hints) {
        return new Context(this.extraAlpha, srcColorModel, dstColorModel);
    }
    
    static class Context extends RGBCompositeContext
    {
        public Context(final float alpha, final ColorModel srcColorModel, final ColorModel dstColorModel) {
            super(alpha, srcColorModel, dstColorModel);
        }
        
        @Override
        public void composeRGB(final int[] src, final int[] dst, final float alpha) {
            for (int w = src.length, i = 0; i < w; i += 4) {
                final int sr = src[i];
                final int dir = dst[i];
                final int sg = src[i + 1];
                final int dig = dst[i + 1];
                final int sb = src[i + 2];
                final int dib = dst[i + 2];
                final int sa = src[i + 3];
                final int dia = dst[i + 3];
                final int dor = dir + RGBCompositeContext.multiply255(sr, 255 - dir - dir);
                final int dog = dig + RGBCompositeContext.multiply255(sg, 255 - dig - dig);
                final int dob = dib + RGBCompositeContext.multiply255(sb, 255 - dib - dib);
                final float a = alpha * sa / 255.0f;
                final float ac = 1.0f - a;
                dst[i] = (int)(a * dor + ac * dir);
                dst[i + 1] = (int)(a * dog + ac * dig);
                dst[i + 2] = (int)(a * dob + ac * dib);
                dst[i + 3] = (int)(sa * alpha + dia * ac);
            }
        }
    }
}
