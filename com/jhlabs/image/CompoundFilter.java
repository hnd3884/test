package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class CompoundFilter extends AbstractBufferedImageOp
{
    private BufferedImageOp filter1;
    private BufferedImageOp filter2;
    
    public CompoundFilter(final BufferedImageOp filter1, final BufferedImageOp filter2) {
        this.filter1 = filter1;
        this.filter2 = filter2;
    }
    
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        BufferedImage image = this.filter1.filter(src, dst);
        image = this.filter2.filter(image, dst);
        return image;
    }
}
