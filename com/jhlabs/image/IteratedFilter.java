package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class IteratedFilter extends AbstractBufferedImageOp
{
    private BufferedImageOp filter;
    private int iterations;
    
    public IteratedFilter(final BufferedImageOp filter, final int iterations) {
        this.filter = filter;
        this.iterations = iterations;
    }
    
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        BufferedImage image = src;
        for (int i = 0; i < this.iterations; ++i) {
            image = this.filter.filter(image, dst);
        }
        return image;
    }
}
