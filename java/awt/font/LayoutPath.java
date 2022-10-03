package java.awt.font;

import java.awt.geom.Point2D;

public abstract class LayoutPath
{
    public abstract boolean pointToPath(final Point2D p0, final Point2D p1);
    
    public abstract void pathToPoint(final Point2D p0, final boolean p1, final Point2D p2);
}
