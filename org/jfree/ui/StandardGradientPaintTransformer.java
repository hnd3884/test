package org.jfree.ui;

import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.GradientPaint;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class StandardGradientPaintTransformer implements GradientPaintTransformer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -8155025776964678320L;
    private GradientPaintTransformType type;
    
    public StandardGradientPaintTransformer() {
        this(GradientPaintTransformType.VERTICAL);
    }
    
    public StandardGradientPaintTransformer(final GradientPaintTransformType type) {
        this.type = type;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardGradientPaintTransformer)) {
            return false;
        }
        final StandardGradientPaintTransformer that = (StandardGradientPaintTransformer)obj;
        return this.type == that.type;
    }
    
    public int hashCode() {
        return (this.type != null) ? this.type.hashCode() : 0;
    }
    
    public GradientPaint transform(final GradientPaint paint, final Shape target) {
        GradientPaint result = paint;
        final Rectangle2D bounds = target.getBounds2D();
        if (this.type.equals(GradientPaintTransformType.VERTICAL)) {
            result = new GradientPaint((float)bounds.getCenterX(), (float)bounds.getMinY(), paint.getColor1(), (float)bounds.getCenterX(), (float)bounds.getMaxY(), paint.getColor2());
        }
        else if (this.type.equals(GradientPaintTransformType.HORIZONTAL)) {
            result = new GradientPaint((float)bounds.getMinX(), (float)bounds.getCenterY(), paint.getColor1(), (float)bounds.getMaxX(), (float)bounds.getCenterY(), paint.getColor2());
        }
        else if (this.type.equals(GradientPaintTransformType.CENTER_HORIZONTAL)) {
            result = new GradientPaint((float)bounds.getCenterX(), (float)bounds.getCenterY(), paint.getColor1(), (float)bounds.getMaxX(), (float)bounds.getCenterY(), paint.getColor2(), true);
        }
        else if (this.type.equals(GradientPaintTransformType.CENTER_VERTICAL)) {
            result = new GradientPaint((float)bounds.getCenterX(), (float)bounds.getMinY(), paint.getColor1(), (float)bounds.getCenterX(), (float)bounds.getCenterY(), paint.getColor2(), true);
        }
        return result;
    }
}
