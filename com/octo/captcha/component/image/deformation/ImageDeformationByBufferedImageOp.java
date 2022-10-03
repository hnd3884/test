package com.octo.captcha.component.image.deformation;

import java.util.Iterator;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.image.BufferedImageOp;
import java.util.List;

public class ImageDeformationByBufferedImageOp implements ImageDeformation
{
    private List<BufferedImageOp> ImageOperations;
    
    public void setImageOperations(final List<BufferedImageOp> imageOperations) {
        this.ImageOperations = imageOperations;
    }
    
    public ImageDeformationByBufferedImageOp(final List<BufferedImageOp> imageOperations) {
        this.ImageOperations = new ArrayList<BufferedImageOp>();
        this.ImageOperations = imageOperations;
    }
    
    public ImageDeformationByBufferedImageOp(final BufferedImageOp bufferedImageOp) {
        (this.ImageOperations = new ArrayList<BufferedImageOp>()).add(bufferedImageOp);
    }
    
    public BufferedImage deformImage(BufferedImage filter) {
        final Iterator<BufferedImageOp> iterator = this.ImageOperations.iterator();
        while (iterator.hasNext()) {
            filter = iterator.next().filter(filter, null);
        }
        return filter;
    }
}
