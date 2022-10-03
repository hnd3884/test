package sun.security.util.math;

import java.math.BigInteger;

public interface IntegerFieldModuloP
{
    BigInteger getSize();
    
    ImmutableIntegerModuloP get0();
    
    ImmutableIntegerModuloP get1();
    
    ImmutableIntegerModuloP getElement(final BigInteger p0);
    
    SmallValue getSmallValue(final int p0);
    
    default ImmutableIntegerModuloP getElement(final byte[] array) {
        return this.getElement(array, 0, array.length, (byte)0);
    }
    
    ImmutableIntegerModuloP getElement(final byte[] p0, final int p1, final int p2, final byte p3);
}
