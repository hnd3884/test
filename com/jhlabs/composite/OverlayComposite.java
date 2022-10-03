package com.jhlabs.composite;

import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public final class OverlayComposite extends RGBComposite
{
    public OverlayComposite(final float alpha) {
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
                if (dir < 128) {
                    final int t = dir * sr + 128;
                    dor = 2 * ((t >> 8) + t >> 8);
                }
                else {
                    final int t = (255 - dir) * (255 - sr) + 128;
                    dor = 2 * (255 - ((t >> 8) + t >> 8));
                }
                int dog;
                if (dig < 128) {
                    final int t = dig * sg + 128;
                    dog = 2 * ((t >> 8) + t >> 8);
                }
                else {
                    final int t = (255 - dig) * (255 - sg) + 128;
                    dog = 2 * (255 - ((t >> 8) + t >> 8));
                }
                int dob;
                if (dib < 128) {
                    final int t = dib * sb + 128;
                    dob = 2 * ((t >> 8) + t >> 8);
                }
                else {
                    final int t = (255 - dib) * (255 - sb) + 128;
                    dob = 2 * (255 - ((t >> 8) + t >> 8));
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
