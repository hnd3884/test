package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface RasterOp
{
    WritableRaster filter(final Raster p0, final WritableRaster p1);
    
    Rectangle2D getBounds2D(final Raster p0);
    
    WritableRaster createCompatibleDestRaster(final Raster p0);
    
    Point2D getPoint2D(final Point2D p0, final Point2D p1);
    
    RenderingHints getRenderingHints();
}
