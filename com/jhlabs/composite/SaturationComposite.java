package com.jhlabs.composite;

import java.awt.Color;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public final class SaturationComposite extends RGBComposite
{
    public SaturationComposite(final float alpha) {
        super(alpha);
    }
    
    public CompositeContext createContext(final ColorModel srcColorModel, final ColorModel dstColorModel, final RenderingHints hints) {
        return new Context(this.extraAlpha, srcColorModel, dstColorModel);
    }
    
    static class Context extends RGBCompositeContext
    {
        private float[] sHSB;
        private float[] dHSB;
        
        public Context(final float alpha, final ColorModel srcColorModel, final ColorModel dstColorModel) {
            super(alpha, srcColorModel, dstColorModel);
            this.sHSB = new float[3];
            this.dHSB = new float[3];
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
                Color.RGBtoHSB(sr, sg, sb, this.sHSB);
                Color.RGBtoHSB(dir, dig, dib, this.dHSB);
                this.dHSB[1] = this.sHSB[1];
                final int doRGB = Color.HSBtoRGB(this.dHSB[0], this.dHSB[1], this.dHSB[2]);
                final int dor = (doRGB & 0xFF0000) >> 16;
                final int dog = (doRGB & 0xFF00) >> 8;
                final int dob = doRGB & 0xFF;
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
