package sun.security.util.math.intpoly;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.math.BigInteger;

public class IntegerPolynomial1305 extends IntegerPolynomial
{
    protected static final int SUBTRAHEND = 5;
    protected static final int NUM_LIMBS = 5;
    private static final int POWER = 130;
    private static final int BITS_PER_LIMB = 26;
    private static final BigInteger MODULUS;
    
    public IntegerPolynomial1305() {
        super(26, 5, 1, IntegerPolynomial1305.MODULUS);
    }
    
    @Override
    protected void mult(final long[] array, final long[] array2, final long[] array3) {
        this.carryReduce(array3, array[0] * array2[0], array[0] * array2[1] + array[1] * array2[0], array[0] * array2[2] + array[1] * array2[1] + array[2] * array2[0], array[0] * array2[3] + array[1] * array2[2] + array[2] * array2[1] + array[3] * array2[0], array[0] * array2[4] + array[1] * array2[3] + array[2] * array2[2] + array[3] * array2[1] + array[4] * array2[0], array[1] * array2[4] + array[2] * array2[3] + array[3] * array2[2] + array[4] * array2[1], array[2] * array2[4] + array[3] * array2[3] + array[4] * array2[2], array[3] * array2[4] + array[4] * array2[3], array[4] * array2[4]);
    }
    
    private void carryReduce(final long[] array, final long n, final long n2, final long n3, long n4, long n5, long n6, final long n7, final long n8, final long n9) {
        array[2] = n3 + n8 * 5L;
        n4 += n9 * 5L;
        final long carryValue = this.carryValue(n4);
        array[3] = n4 - (carryValue << 26);
        n5 += carryValue;
        final long carryValue2 = this.carryValue(n5);
        array[4] = n5 - (carryValue2 << 26);
        n6 += carryValue2;
        array[0] = n + n6 * 5L;
        array[1] = n2 + n7 * 5L;
        this.carry(array);
    }
    
    @Override
    protected void square(final long[] array, final long[] array2) {
        this.carryReduce(array2, array[0] * array[0], 2L * (array[0] * array[1]), 2L * (array[0] * array[2]) + array[1] * array[1], 2L * (array[0] * array[3] + array[1] * array[2]), 2L * (array[0] * array[4] + array[1] * array[3]) + array[2] * array[2], 2L * (array[1] * array[4] + array[2] * array[3]), 2L * (array[2] * array[4]) + array[3] * array[3], 2L * (array[3] * array[4]), array[4] * array[4]);
    }
    
    @Override
    protected void encode(final ByteBuffer byteBuffer, final int n, final byte b, final long[] array) {
        if (n == 16) {
            this.encode(byteBuffer.getLong(), byteBuffer.getLong(), b, array);
        }
        else {
            super.encode(byteBuffer, n, b, array);
        }
    }
    
    protected void encode(final long n, final long n2, final byte b, final long[] array) {
        array[0] = (n2 & 0x3FFFFFFL);
        array[1] = (n2 >>> 26 & 0x3FFFFFFL);
        array[2] = (n2 >>> 52) + ((n & 0x3FFFL) << 12);
        array[3] = (n >>> 14 & 0x3FFFFFFL);
        array[4] = (n >>> 40) + (b << 24);
    }
    
    @Override
    protected void encode(final byte[] array, final int n, final int n2, final byte b, final long[] array2) {
        if (n2 == 16) {
            final ByteBuffer order = ByteBuffer.wrap(array, n, n2).order(ByteOrder.LITTLE_ENDIAN);
            this.encode(order.getLong(), order.getLong(), b, array2);
        }
        else {
            super.encode(array, n, n2, b, array2);
        }
    }
    
    private void modReduceIn(final long[] array, final int n, final long n2) {
        final long n3 = n2 * 5L;
        final int n4 = n - 5;
        array[n4] += n3;
    }
    
    @Override
    protected void finalCarryReduceLast(final long[] array) {
        final long n = array[this.numLimbs - 1] >> this.bitsPerLimb;
        final int n2 = this.numLimbs - 1;
        array[n2] -= n << this.bitsPerLimb;
        this.modReduceIn(array, this.numLimbs, n);
    }
    
    protected final void modReduce(final long[] array, final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            this.modReduceIn(array, i, array[i]);
            array[i] = 0L;
        }
    }
    
    protected void modReduce(final long[] array) {
        this.modReduce(array, 5, 4);
    }
    
    @Override
    protected long carryValue(final long n) {
        return n >> 26;
    }
    
    @Override
    protected void postEncodeCarry(final long[] array) {
    }
    
    @Override
    protected void reduce(final long[] array) {
        final long n = this.carryOut(array, 3) + array[4];
        final long carryValue = this.carryValue(n);
        array[4] = n - (carryValue << 26);
        this.modReduceIn(array, 5, carryValue);
        this.carry(array);
    }
    
    static {
        MODULUS = IntegerPolynomial1305.TWO.pow(130).subtract(BigInteger.valueOf(5L));
    }
}
