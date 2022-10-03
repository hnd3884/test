package com.jhlabs.composite;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.CompositeContext;

class ContourCompositeContext implements CompositeContext
{
    private int offset;
    
    public ContourCompositeContext(final int offset, final ColorModel srcColorModel, final ColorModel dstColorModel) {
        this.offset = offset;
    }
    
    public void dispose() {
    }
    
    public void compose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
        final int x = src.getMinX();
        int y = src.getMinY();
        final int w = src.getWidth();
        final int h = src.getHeight();
        int[] srcPix = null;
        int[] srcPix2 = null;
        int[] dstInPix = null;
        final int[] dstOutPix = new int[w * 4];
        for (int i = 0; i < h; ++i) {
            srcPix = src.getPixels(x, y, w, 1, srcPix);
            dstInPix = dstIn.getPixels(x, y, w, 1, dstInPix);
            int lastAlpha = 0;
            int k = 0;
            for (int j = 0; j < w; ++j) {
                final int alpha = srcPix[k + 3];
                final int alphaAbove = (i != 0) ? srcPix2[k + 3] : alpha;
                if ((i != 0 && j != 0 && ((alpha ^ lastAlpha) & 0x80) != 0x0) || ((alpha ^ alphaAbove) & 0x80) != 0x0) {
                    if ((this.offset + i + j) % 10 > 4) {
                        dstOutPix[k] = 0;
                        dstOutPix[k + 2] = (dstOutPix[k + 1] = 0);
                    }
                    else {
                        dstOutPix[k + 1] = (dstOutPix[k] = 255);
                        dstOutPix[k + 2] = 127;
                    }
                    dstOutPix[k + 3] = 255;
                }
                else {
                    dstOutPix[k] = dstInPix[k];
                    dstOutPix[k + 1] = dstInPix[k + 1];
                    dstOutPix[k + 2] = dstInPix[k + 2];
                    dstOutPix[k] = 255;
                    dstOutPix[k + 1] = 0;
                    dstOutPix[k + 3] = (dstOutPix[k + 2] = 0);
                }
                lastAlpha = alpha;
                k += 4;
            }
            dstOut.setPixels(x, y, w, 1, dstOutPix);
            final int[] t = srcPix;
            srcPix = srcPix2;
            srcPix2 = t;
            ++y;
        }
    }
}
