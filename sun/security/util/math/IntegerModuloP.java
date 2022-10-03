package sun.security.util.math;

import java.math.BigInteger;

public interface IntegerModuloP
{
    IntegerFieldModuloP getField();
    
    BigInteger asBigInteger();
    
    ImmutableIntegerModuloP fixed();
    
    MutableIntegerModuloP mutable();
    
    ImmutableIntegerModuloP add(final IntegerModuloP p0);
    
    ImmutableIntegerModuloP additiveInverse();
    
    ImmutableIntegerModuloP multiply(final IntegerModuloP p0);
    
    default byte[] addModPowerTwo(final IntegerModuloP integerModuloP, final int n) {
        final byte[] array = new byte[n];
        this.addModPowerTwo(integerModuloP, array);
        return array;
    }
    
    void addModPowerTwo(final IntegerModuloP p0, final byte[] p1);
    
    default byte[] asByteArray(final int n) {
        final byte[] array = new byte[n];
        this.asByteArray(array);
        return array;
    }
    
    void asByteArray(final byte[] p0);
    
    default ImmutableIntegerModuloP multiplicativeInverse() {
        return this.pow(this.getField().getSize().subtract(BigInteger.valueOf(2L)));
    }
    
    default ImmutableIntegerModuloP subtract(final IntegerModuloP integerModuloP) {
        return this.add(integerModuloP.additiveInverse());
    }
    
    default ImmutableIntegerModuloP square() {
        return this.multiply(this);
    }
    
    default ImmutableIntegerModuloP pow(final BigInteger bigInteger) {
        final MutableIntegerModuloP mutable = this.getField().get1().mutable();
        final MutableIntegerModuloP mutable2 = this.mutable();
        for (int bitLength = bigInteger.bitLength(), i = 0; i < bitLength; ++i) {
            if (bigInteger.testBit(i)) {
                mutable.setProduct(mutable2);
            }
            mutable2.setSquare();
        }
        return mutable.fixed();
    }
}
