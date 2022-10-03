package sun.security.ec.point;

import sun.security.util.math.IntegerFieldModuloP;

public interface Point
{
    IntegerFieldModuloP getField();
    
    AffinePoint asAffine();
    
    ImmutablePoint fixed();
    
    MutablePoint mutable();
}
