package java.awt.image.renderable;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;

public class RenderContext implements Cloneable
{
    RenderingHints hints;
    AffineTransform usr2dev;
    Shape aoi;
    
    public RenderContext(final AffineTransform affineTransform, final Shape aoi, final RenderingHints hints) {
        this.hints = hints;
        this.aoi = aoi;
        this.usr2dev = (AffineTransform)affineTransform.clone();
    }
    
    public RenderContext(final AffineTransform affineTransform) {
        this(affineTransform, null, null);
    }
    
    public RenderContext(final AffineTransform affineTransform, final RenderingHints renderingHints) {
        this(affineTransform, null, renderingHints);
    }
    
    public RenderContext(final AffineTransform affineTransform, final Shape shape) {
        this(affineTransform, shape, null);
    }
    
    public RenderingHints getRenderingHints() {
        return this.hints;
    }
    
    public void setRenderingHints(final RenderingHints hints) {
        this.hints = hints;
    }
    
    public void setTransform(final AffineTransform affineTransform) {
        this.usr2dev = (AffineTransform)affineTransform.clone();
    }
    
    public void preConcatenateTransform(final AffineTransform affineTransform) {
        this.preConcetenateTransform(affineTransform);
    }
    
    @Deprecated
    public void preConcetenateTransform(final AffineTransform affineTransform) {
        this.usr2dev.preConcatenate(affineTransform);
    }
    
    public void concatenateTransform(final AffineTransform affineTransform) {
        this.concetenateTransform(affineTransform);
    }
    
    @Deprecated
    public void concetenateTransform(final AffineTransform affineTransform) {
        this.usr2dev.concatenate(affineTransform);
    }
    
    public AffineTransform getTransform() {
        return (AffineTransform)this.usr2dev.clone();
    }
    
    public void setAreaOfInterest(final Shape aoi) {
        this.aoi = aoi;
    }
    
    public Shape getAreaOfInterest() {
        return this.aoi;
    }
    
    public Object clone() {
        return new RenderContext(this.usr2dev, this.aoi, this.hints);
    }
}
