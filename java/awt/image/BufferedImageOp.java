package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface BufferedImageOp
{
    BufferedImage filter(final BufferedImage p0, final BufferedImage p1);
    
    Rectangle2D getBounds2D(final BufferedImage p0);
    
    BufferedImage createCompatibleDestImage(final BufferedImage p0, final ColorModel p1);
    
    Point2D getPoint2D(final Point2D p0, final Point2D p1);
    
    RenderingHints getRenderingHints();
}
