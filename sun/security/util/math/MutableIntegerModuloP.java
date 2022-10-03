package sun.security.util.math;

import java.nio.ByteBuffer;

public interface MutableIntegerModuloP extends IntegerModuloP
{
    void conditionalSet(final IntegerModuloP p0, final int p1);
    
    void conditionalSwapWith(final MutableIntegerModuloP p0, final int p1);
    
    MutableIntegerModuloP setValue(final IntegerModuloP p0);
    
    MutableIntegerModuloP setValue(final byte[] p0, final int p1, final int p2, final byte p3);
    
    MutableIntegerModuloP setValue(final ByteBuffer p0, final int p1, final byte p2);
    
    MutableIntegerModuloP setSquare();
    
    MutableIntegerModuloP setSum(final IntegerModuloP p0);
    
    MutableIntegerModuloP setDifference(final IntegerModuloP p0);
    
    MutableIntegerModuloP setProduct(final IntegerModuloP p0);
    
    MutableIntegerModuloP setProduct(final SmallValue p0);
    
    MutableIntegerModuloP setAdditiveInverse();
    
    MutableIntegerModuloP setReduced();
}
