package com.jhlabs.image;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;

public class ApplyMaskFilter extends AbstractBufferedImageOp
{
    private BufferedImage destination;
    private BufferedImage maskImage;
    
    public ApplyMaskFilter() {
    }
    
    public ApplyMaskFilter(final BufferedImage maskImage, final BufferedImage destination) {
        this.maskImage = maskImage;
        this.destination = destination;
    }
    
    public void setDestination(final BufferedImage destination) {
        this.destination = destination;
    }
    
    public BufferedImage getDestination() {
        return this.destination;
    }
    
    public void setMaskImage(final BufferedImage maskImage) {
        this.maskImage = maskImage;
    }
    
    public BufferedImage getMaskImage() {
        return this.maskImage;
    }
    
    public static void composeThroughMask(final Raster src, final WritableRaster dst, final Raster sel) {
        final int x = src.getMinX();
        int y = src.getMinY();
        final int w = src.getWidth();
        final int h = src.getHeight();
        int[] srcRGB = null;
        int[] selRGB = null;
        int[] dstRGB = null;
        for (int i = 0; i < h; ++i) {
            srcRGB = src.getPixels(x, y, w, 1, srcRGB);
            selRGB = sel.getPixels(x, y, w, 1, selRGB);
            dstRGB = dst.getPixels(x, y, w, 1, dstRGB);
            int k = x;
            for (int j = 0; j < w; ++j) {
                final int sr = srcRGB[k];
                final int dir = dstRGB[k];
                final int sg = srcRGB[k + 1];
                final int dig = dstRGB[k + 1];
                final int sb = srcRGB[k + 2];
                final int dib = dstRGB[k + 2];
                final int sa = srcRGB[k + 3];
                final int dia = dstRGB[k + 3];
                final float a = selRGB[k + 3] / 255.0f;
                final float ac = 1.0f - a;
                dstRGB[k] = (int)(a * sr + ac * dir);
                dstRGB[k + 1] = (int)(a * sg + ac * dig);
                dstRGB[k + 2] = (int)(a * sb + ac * dib);
                dstRGB[k + 3] = (int)(a * sa + ac * dia);
                k += 4;
            }
            dst.setPixels(x, y, w, 1, dstRGB);
            ++y;
        }
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final int type = src.getType();
        final WritableRaster srcRaster = src.getRaster();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final WritableRaster dstRaster = dst.getRaster();
        if (this.destination != null && this.maskImage != null) {
            composeThroughMask(src.getRaster(), dst.getRaster(), this.maskImage.getRaster());
        }
        return dst;
    }
    
    @Override
    public String toString() {
        return "Keying/Key...";
    }
}
