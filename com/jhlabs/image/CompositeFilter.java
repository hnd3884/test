package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.image.RenderedImage;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.Composite;

public class CompositeFilter extends AbstractBufferedImageOp
{
    private Composite composite;
    private AffineTransform transform;
    
    public CompositeFilter() {
    }
    
    public CompositeFilter(final Composite composite) {
        this.composite = composite;
    }
    
    public CompositeFilter(final Composite composite, final AffineTransform transform) {
        this.composite = composite;
        this.transform = transform;
    }
    
    public void setComposite(final Composite composite) {
        this.composite = composite;
    }
    
    public Composite getComposite() {
        return this.composite;
    }
    
    public void setTransform(final AffineTransform transform) {
        this.transform = transform;
    }
    
    public AffineTransform getTransform() {
        return this.transform;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setComposite(this.composite);
        g.drawRenderedImage(src, this.transform);
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Composite";
    }
}
