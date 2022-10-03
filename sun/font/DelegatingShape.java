package sun.font;

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.Shape;

public final class DelegatingShape implements Shape
{
    Shape delegate;
    
    public DelegatingShape(final Shape delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public Rectangle getBounds() {
        return this.delegate.getBounds();
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return this.delegate.getBounds2D();
    }
    
    @Override
    public boolean contains(final double n, final double n2) {
        return this.delegate.contains(n, n2);
    }
    
    @Override
    public boolean contains(final Point2D point2D) {
        return this.delegate.contains(point2D);
    }
    
    @Override
    public boolean intersects(final double n, final double n2, final double n3, final double n4) {
        return this.delegate.intersects(n, n2, n3, n4);
    }
    
    @Override
    public boolean intersects(final Rectangle2D rectangle2D) {
        return this.delegate.intersects(rectangle2D);
    }
    
    @Override
    public boolean contains(final double n, final double n2, final double n3, final double n4) {
        return this.delegate.contains(n, n2, n3, n4);
    }
    
    @Override
    public boolean contains(final Rectangle2D rectangle2D) {
        return this.delegate.contains(rectangle2D);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform) {
        return this.delegate.getPathIterator(affineTransform);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform, final double n) {
        return this.delegate.getPathIterator(affineTransform, n);
    }
}
