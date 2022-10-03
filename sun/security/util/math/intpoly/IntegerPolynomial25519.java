package sun.security.util.math.intpoly;

import java.math.BigInteger;

public class IntegerPolynomial25519 extends IntegerPolynomial
{
    private static final int POWER = 255;
    private static final int SUBTRAHEND = 19;
    private static final int NUM_LIMBS = 10;
    private static final int BITS_PER_LIMB = 26;
    public static final BigInteger MODULUS;
    private static final int BIT_OFFSET = 5;
    private static final int LIMB_MASK = 67108863;
    private static final int RIGHT_BIT_OFFSET = 21;
    
    public IntegerPolynomial25519() {
        super(26, 10, 1, IntegerPolynomial25519.MODULUS);
    }
    
    @Override
    protected void finalCarryReduceLast(final long[] array) {
        final long n = array[this.numLimbs - 1] >> 21;
        final int n2 = this.numLimbs - 1;
        array[n2] -= n << 21;
        final int n3 = 0;
        array[n3] += n * 19L;
    }
    
    @Override
    protected void reduce(final long[] array) {
        final long carryValue = this.carryValue(array[8]);
        final int n = 8;
        array[n] -= carryValue << 26;
        final int n2 = 9;
        array[n2] += carryValue;
        final long carryValue2 = this.carryValue(array[9]);
        final int n3 = 9;
        array[n3] -= carryValue2 << 26;
        final long n4 = carryValue2 * 19L;
        final int n5 = 0;
        array[n5] += (n4 << 5 & 0x3FFFFFFL);
        final int n6 = 1;
        array[n6] += n4 >> 21;
        this.carry(array, 0, 9);
    }
    
    @Override
    protected void mult(final long[] array, final long[] array2, final long[] array3) {
        this.carryReduce(array3, array[0] * array2[0], array[0] * array2[1] + array[1] * array2[0], array[0] * array2[2] + array[1] * array2[1] + array[2] * array2[0], array[0] * array2[3] + array[1] * array2[2] + array[2] * array2[1] + array[3] * array2[0], array[0] * array2[4] + array[1] * array2[3] + array[2] * array2[2] + array[3] * array2[1] + array[4] * array2[0], array[0] * array2[5] + array[1] * array2[4] + array[2] * array2[3] + array[3] * array2[2] + array[4] * array2[1] + array[5] * array2[0], array[0] * array2[6] + array[1] * array2[5] + array[2] * array2[4] + array[3] * array2[3] + array[4] * array2[2] + array[5] * array2[1] + array[6] * array2[0], array[0] * array2[7] + array[1] * array2[6] + array[2] * array2[5] + array[3] * array2[4] + array[4] * array2[3] + array[5] * array2[2] + array[6] * array2[1] + array[7] * array2[0], array[0] * array2[8] + array[1] * array2[7] + array[2] * array2[6] + array[3] * array2[5] + array[4] * array2[4] + array[5] * array2[3] + array[6] * array2[2] + array[7] * array2[1] + array[8] * array2[0], array[0] * array2[9] + array[1] * array2[8] + array[2] * array2[7] + array[3] * array2[6] + array[4] * array2[5] + array[5] * array2[4] + array[6] * array2[3] + array[7] * array2[2] + array[8] * array2[1] + array[9] * array2[0], array[1] * array2[9] + array[2] * array2[8] + array[3] * array2[7] + array[4] * array2[6] + array[5] * array2[5] + array[6] * array2[4] + array[7] * array2[3] + array[8] * array2[2] + array[9] * array2[1], array[2] * array2[9] + array[3] * array2[8] + array[4] * array2[7] + array[5] * array2[6] + array[6] * array2[5] + array[7] * array2[4] + array[8] * array2[3] + array[9] * array2[2], array[3] * array2[9] + array[4] * array2[8] + array[5] * array2[7] + array[6] * array2[6] + array[7] * array2[5] + array[8] * array2[4] + array[9] * array2[3], array[4] * array2[9] + array[5] * array2[8] + array[6] * array2[7] + array[7] * array2[6] + array[8] * array2[5] + array[9] * array2[4], array[5] * array2[9] + array[6] * array2[8] + array[7] * array2[7] + array[8] * array2[6] + array[9] * array2[5], array[6] * array2[9] + array[7] * array2[8] + array[8] * array2[7] + array[9] * array2[6], array[7] * array2[9] + array[8] * array2[8] + array[9] * array2[7], array[8] * array2[9] + array[9] * array2[8], array[9] * array2[9]);
    }
    
    private void carryReduce(final long[] array, final long n, long n2, long n3, long n4, long n5, long n6, long n7, long n8, long n9, long n10, long n11, final long n12, final long n13, final long n14, final long n15, final long n16, final long n17, final long n18, final long n19) {
        final long n20 = n18 * 19L;
        n8 += (n20 << 5 & 0x3FFFFFFL);
        n9 += n20 >> 21;
        final long n21 = n19 * 19L;
        n9 += (n21 << 5 & 0x3FFFFFFL);
        n10 += n21 >> 21;
        final long carryValue = this.carryValue(n9);
        array[8] = n9 - (carryValue << 26);
        n10 += carryValue;
        final long carryValue2 = this.carryValue(n10);
        array[9] = n10 - (carryValue2 << 26);
        n11 += carryValue2;
        final long n22 = n11 * 19L;
        array[0] = n + (n22 << 5 & 0x3FFFFFFL);
        n2 += n22 >> 21;
        final long n23 = n12 * 19L;
        array[1] = n2 + (n23 << 5 & 0x3FFFFFFL);
        n3 += n23 >> 21;
        final long n24 = n13 * 19L;
        array[2] = n3 + (n24 << 5 & 0x3FFFFFFL);
        n4 += n24 >> 21;
        final long n25 = n14 * 19L;
        array[3] = n4 + (n25 << 5 & 0x3FFFFFFL);
        n5 += n25 >> 21;
        final long n26 = n15 * 19L;
        array[4] = n5 + (n26 << 5 & 0x3FFFFFFL);
        n6 += n26 >> 21;
        final long n27 = n16 * 19L;
        array[5] = n6 + (n27 << 5 & 0x3FFFFFFL);
        n7 += n27 >> 21;
        final long n28 = n17 * 19L;
        array[6] = n7 + (n28 << 5 & 0x3FFFFFFL);
        array[7] = n8 + (n28 >> 21);
        this.carry(array, 0, 9);
    }
    
    @Override
    protected void square(final long[] array, final long[] array2) {
        this.carryReduce(array2, array[0] * array[0], 2L * array[0] * array[1], array[1] * array[1] + 2L * array[0] * array[2], 2L * (array[0] * array[3] + array[1] * array[2]), array[2] * array[2] + 2L * (array[0] * array[4] + array[1] * array[3]), 2L * (array[0] * array[5] + array[1] * array[4] + array[2] * array[3]), array[3] * array[3] + 2L * (array[0] * array[6] + array[1] * array[5] + array[2] * array[4]), 2L * (array[0] * array[7] + array[1] * array[6] + array[2] * array[5] + array[3] * array[4]), array[4] * array[4] + 2L * (array[0] * array[8] + array[1] * array[7] + array[2] * array[6] + array[3] * array[5]), 2L * (array[0] * array[9] + array[1] * array[8] + array[2] * array[7] + array[3] * array[6] + array[4] * array[5]), array[5] * array[5] + 2L * (array[1] * array[9] + array[2] * array[8] + array[3] * array[7] + array[4] * array[6]), 2L * (array[2] * array[9] + array[3] * array[8] + array[4] * array[7] + array[5] * array[6]), array[6] * array[6] + 2L * (array[3] * array[9] + array[4] * array[8] + array[5] * array[7]), 2L * (array[4] * array[9] + array[5] * array[8] + array[6] * array[7]), array[7] * array[7] + 2L * (array[5] * array[9] + array[6] * array[8]), 2L * (array[6] * array[9] + array[7] * array[8]), array[8] * array[8] + 2L * array[7] * array[9], 2L * array[8] * array[9], array[9] * array[9]);
    }
    
    static {
        MODULUS = IntegerPolynomial25519.TWO.pow(255).subtract(BigInteger.valueOf(19L));
    }
}
