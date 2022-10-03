package com.jhlabs.image;

import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.ImageProducer;
import java.awt.Image;

public class ImageCombiningFilter
{
    public int filterRGB(final int x, final int y, final int rgb1, final int rgb2) {
        final int a1 = rgb1 >> 24 & 0xFF;
        final int r1 = rgb1 >> 16 & 0xFF;
        final int g1 = rgb1 >> 8 & 0xFF;
        final int b1 = rgb1 & 0xFF;
        final int a2 = rgb2 >> 24 & 0xFF;
        final int r2 = rgb2 >> 16 & 0xFF;
        final int g2 = rgb2 >> 8 & 0xFF;
        final int b2 = rgb2 & 0xFF;
        final int r3 = PixelUtils.clamp(r1 + r2);
        final int g3 = PixelUtils.clamp(r1 + r2);
        final int b3 = PixelUtils.clamp(r1 + r2);
        return a1 << 24 | r3 << 16 | g3 << 8 | b3;
    }
    
    public ImageProducer filter(final Image image1, final Image image2, final int x, final int y, final int w, final int h) {
        final int[] pixels1 = new int[w * h];
        final int[] pixels2 = new int[w * h];
        final int[] pixels3 = new int[w * h];
        final PixelGrabber pg1 = new PixelGrabber(image1, x, y, w, h, pixels1, 0, w);
        final PixelGrabber pg2 = new PixelGrabber(image2, x, y, w, h, pixels2, 0, w);
        try {
            pg1.grabPixels();
            pg2.grabPixels();
        }
        catch (final InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
            return null;
        }
        if ((pg1.status() & 0x80) != 0x0) {
            System.err.println("image fetch aborted or errored");
            return null;
        }
        if ((pg2.status() & 0x80) != 0x0) {
            System.err.println("image fetch aborted or errored");
            return null;
        }
        for (int j = 0; j < h; ++j) {
            for (int i = 0; i < w; ++i) {
                final int k = j * w + i;
                pixels3[k] = this.filterRGB(x + i, y + j, pixels1[k], pixels2[k]);
            }
        }
        return new MemoryImageSource(w, h, pixels3, 0, w);
    }
}
