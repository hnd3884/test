package java.awt;

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface Shape
{
    Rectangle getBounds();
    
    Rectangle2D getBounds2D();
    
    boolean contains(final double p0, final double p1);
    
    boolean contains(final Point2D p0);
    
    boolean intersects(final double p0, final double p1, final double p2, final double p3);
    
    boolean intersects(final Rectangle2D p0);
    
    boolean contains(final double p0, final double p1, final double p2, final double p3);
    
    boolean contains(final Rectangle2D p0);
    
    PathIterator getPathIterator(final AffineTransform p0);
    
    PathIterator getPathIterator(final AffineTransform p0, final double p1);
}
