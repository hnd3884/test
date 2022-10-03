package sun.security.ec.point;

import java.util.Objects;
import sun.security.util.math.ImmutableIntegerModuloP;

public class AffinePoint
{
    private final ImmutableIntegerModuloP x;
    private final ImmutableIntegerModuloP y;
    
    public AffinePoint(final ImmutableIntegerModuloP x, final ImmutableIntegerModuloP y) {
        this.x = x;
        this.y = y;
    }
    
    public ImmutableIntegerModuloP getX() {
        return this.x;
    }
    
    public ImmutableIntegerModuloP getY() {
        return this.y;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof AffinePoint)) {
            return false;
        }
        final AffinePoint affinePoint = (AffinePoint)o;
        final boolean equals = this.x.asBigInteger().equals(affinePoint.x.asBigInteger());
        final boolean equals2 = this.y.asBigInteger().equals(affinePoint.y.asBigInteger());
        return equals && equals2;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
    
    @Override
    public String toString() {
        return "(" + this.x.asBigInteger().toString() + "," + this.y.asBigInteger().toString() + ")";
    }
}
