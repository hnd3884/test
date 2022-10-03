package com.jhlabs.image;

import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public abstract class PointFilter extends AbstractBufferedImageOp
{
    protected boolean canFilterIndexColorModel;
    
    public PointFilter() {
        this.canFilterIndexColorModel = false;
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
        this.setDimensions(width, height);
        final int[] inPixels = new int[width];
        for (int y = 0; y < height; ++y) {
            if (type == 2) {
                srcRaster.getDataElements(0, y, width, 1, inPixels);
                for (int x = 0; x < width; ++x) {
                    inPixels[x] = this.filterRGB(x, y, inPixels[x]);
                }
                dstRaster.setDataElements(0, y, width, 1, inPixels);
            }
            else {
                src.getRGB(0, y, width, 1, inPixels, 0, width);
                for (int x = 0; x < width; ++x) {
                    inPixels[x] = this.filterRGB(x, y, inPixels[x]);
                }
                dst.setRGB(0, y, width, 1, inPixels, 0, width);
            }
        }
        return dst;
    }
    
    public void setDimensions(final int width, final int height) {
    }
    
    public abstract int filterRGB(final int p0, final int p1, final int p2);
}
