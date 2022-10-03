package com.jhlabs.composite;

import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public final class HardLightComposite extends RGBComposite
{
    public HardLightComposite(final float alpha) {
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
                int dor;
                if (sr > 127) {
                    dor = 255 - 2 * RGBCompositeContext.multiply255(255 - sr, 255 - dir);
                }
                else {
                    dor = 2 * RGBCompositeContext.multiply255(sr, dir);
                }
                int dog;
                if (sg > 127) {
                    dog = 255 - 2 * RGBCompositeContext.multiply255(255 - sg, 255 - dig);
                }
                else {
                    dog = 2 * RGBCompositeContext.multiply255(sg, dig);
                }
                int dob;
                if (sb > 127) {
                    dob = 255 - 2 * RGBCompositeContext.multiply255(255 - sb, 255 - dib);
                }
                else {
                    dob = 2 * RGBCompositeContext.multiply255(sb, dib);
                }
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
