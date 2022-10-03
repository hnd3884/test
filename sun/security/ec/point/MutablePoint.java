package sun.security.ec.point;

public interface MutablePoint extends Point
{
    MutablePoint setValue(final AffinePoint p0);
    
    MutablePoint setValue(final Point p0);
    
    MutablePoint conditionalSet(final Point p0, final int p1);
}
