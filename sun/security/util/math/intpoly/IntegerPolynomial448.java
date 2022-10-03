package sun.security.util.math.intpoly;

import java.math.BigInteger;

public class IntegerPolynomial448 extends IntegerPolynomial
{
    private static final int POWER = 448;
    private static final int NUM_LIMBS = 16;
    private static final int BITS_PER_LIMB = 28;
    public static final BigInteger MODULUS;
    
    public IntegerPolynomial448() {
        super(28, 16, 1, IntegerPolynomial448.MODULUS);
    }
    
    private void modReduceIn(final long[] array, final int n, final long n2) {
        final int n3 = n - 16;
        array[n3] += n2;
        final int n4 = n - 8;
        array[n4] += n2;
    }
    
    @Override
    protected void finalCarryReduceLast(final long[] array) {
        final long n = array[this.numLimbs - 1] >> this.bitsPerLimb;
        final int n2 = this.numLimbs - 1;
        array[n2] -= n << this.bitsPerLimb;
        this.modReduceIn(array, this.numLimbs, n);
    }
    
    @Override
    protected void reduce(final long[] array) {
        final long carryValue = this.carryValue(array[14]);
        final int n = 14;
        array[n] -= carryValue << 28;
        final int n2 = 15;
        array[n2] += carryValue;
        final long carryValue2 = this.carryValue(array[15]);
        final int n3 = 15;
        array[n3] -= carryValue2 << 28;
        final int n4 = 0;
        array[n4] += carryValue2;
        final int n5 = 8;
        array[n5] += carryValue2;
        this.carry(array, 0, 15);
    }
    
    @Override
    protected void mult(final long[] array, final long[] array2, final long[] array3) {
        this.carryReduce(array3, array[0] * array2[0], array[0] * array2[1] + array[1] * array2[0], array[0] * array2[2] + array[1] * array2[1] + array[2] * array2[0], array[0] * array2[3] + array[1] * array2[2] + array[2] * array2[1] + array[3] * array2[0], array[0] * array2[4] + array[1] * array2[3] + array[2] * array2[2] + array[3] * array2[1] + array[4] * array2[0], array[0] * array2[5] + array[1] * array2[4] + array[2] * array2[3] + array[3] * array2[2] + array[4] * array2[1] + array[5] * array2[0], array[0] * array2[6] + array[1] * array2[5] + array[2] * array2[4] + array[3] * array2[3] + array[4] * array2[2] + array[5] * array2[1] + array[6] * array2[0], array[0] * array2[7] + array[1] * array2[6] + array[2] * array2[5] + array[3] * array2[4] + array[4] * array2[3] + array[5] * array2[2] + array[6] * array2[1] + array[7] * array2[0], array[0] * array2[8] + array[1] * array2[7] + array[2] * array2[6] + array[3] * array2[5] + array[4] * array2[4] + array[5] * array2[3] + array[6] * array2[2] + array[7] * array2[1] + array[8] * array2[0], array[0] * array2[9] + array[1] * array2[8] + array[2] * array2[7] + array[3] * array2[6] + array[4] * array2[5] + array[5] * array2[4] + array[6] * array2[3] + array[7] * array2[2] + array[8] * array2[1] + array[9] * array2[0], array[0] * array2[10] + array[1] * array2[9] + array[2] * array2[8] + array[3] * array2[7] + array[4] * array2[6] + array[5] * array2[5] + array[6] * array2[4] + array[7] * array2[3] + array[8] * array2[2] + array[9] * array2[1] + array[10] * array2[0], array[0] * array2[11] + array[1] * array2[10] + array[2] * array2[9] + array[3] * array2[8] + array[4] * array2[7] + array[5] * array2[6] + array[6] * array2[5] + array[7] * array2[4] + array[8] * array2[3] + array[9] * array2[2] + array[10] * array2[1] + array[11] * array2[0], array[0] * array2[12] + array[1] * array2[11] + array[2] * array2[10] + array[3] * array2[9] + array[4] * array2[8] + array[5] * array2[7] + array[6] * array2[6] + array[7] * array2[5] + array[8] * array2[4] + array[9] * array2[3] + array[10] * array2[2] + array[11] * array2[1] + array[12] * array2[0], array[0] * array2[13] + array[1] * array2[12] + array[2] * array2[11] + array[3] * array2[10] + array[4] * array2[9] + array[5] * array2[8] + array[6] * array2[7] + array[7] * array2[6] + array[8] * array2[5] + array[9] * array2[4] + array[10] * array2[3] + array[11] * array2[2] + array[12] * array2[1] + array[13] * array2[0], array[0] * array2[14] + array[1] * array2[13] + array[2] * array2[12] + array[3] * array2[11] + array[4] * array2[10] + array[5] * array2[9] + array[6] * array2[8] + array[7] * array2[7] + array[8] * array2[6] + array[9] * array2[5] + array[10] * array2[4] + array[11] * array2[3] + array[12] * array2[2] + array[13] * array2[1] + array[14] * array2[0], array[0] * array2[15] + array[1] * array2[14] + array[2] * array2[13] + array[3] * array2[12] + array[4] * array2[11] + array[5] * array2[10] + array[6] * array2[9] + array[7] * array2[8] + array[8] * array2[7] + array[9] * array2[6] + array[10] * array2[5] + array[11] * array2[4] + array[12] * array2[3] + array[13] * array2[2] + array[14] * array2[1] + array[15] * array2[0], array[1] * array2[15] + array[2] * array2[14] + array[3] * array2[13] + array[4] * array2[12] + array[5] * array2[11] + array[6] * array2[10] + array[7] * array2[9] + array[8] * array2[8] + array[9] * array2[7] + array[10] * array2[6] + array[11] * array2[5] + array[12] * array2[4] + array[13] * array2[3] + array[14] * array2[2] + array[15] * array2[1], array[2] * array2[15] + array[3] * array2[14] + array[4] * array2[13] + array[5] * array2[12] + array[6] * array2[11] + array[7] * array2[10] + array[8] * array2[9] + array[9] * array2[8] + array[10] * array2[7] + array[11] * array2[6] + array[12] * array2[5] + array[13] * array2[4] + array[14] * array2[3] + array[15] * array2[2], array[3] * array2[15] + array[4] * array2[14] + array[5] * array2[13] + array[6] * array2[12] + array[7] * array2[11] + array[8] * array2[10] + array[9] * array2[9] + array[10] * array2[8] + array[11] * array2[7] + array[12] * array2[6] + array[13] * array2[5] + array[14] * array2[4] + array[15] * array2[3], array[4] * array2[15] + array[5] * array2[14] + array[6] * array2[13] + array[7] * array2[12] + array[8] * array2[11] + array[9] * array2[10] + array[10] * array2[9] + array[11] * array2[8] + array[12] * array2[7] + array[13] * array2[6] + array[14] * array2[5] + array[15] * array2[4], array[5] * array2[15] + array[6] * array2[14] + array[7] * array2[13] + array[8] * array2[12] + array[9] * array2[11] + array[10] * array2[10] + array[11] * array2[9] + array[12] * array2[8] + array[13] * array2[7] + array[14] * array2[6] + array[15] * array2[5], array[6] * array2[15] + array[7] * array2[14] + array[8] * array2[13] + array[9] * array2[12] + array[10] * array2[11] + array[11] * array2[10] + array[12] * array2[9] + array[13] * array2[8] + array[14] * array2[7] + array[15] * array2[6], array[7] * array2[15] + array[8] * array2[14] + array[9] * array2[13] + array[10] * array2[12] + array[11] * array2[11] + array[12] * array2[10] + array[13] * array2[9] + array[14] * array2[8] + array[15] * array2[7], array[8] * array2[15] + array[9] * array2[14] + array[10] * array2[13] + array[11] * array2[12] + array[12] * array2[11] + array[13] * array2[10] + array[14] * array2[9] + array[15] * array2[8], array[9] * array2[15] + array[10] * array2[14] + array[11] * array2[13] + array[12] * array2[12] + array[13] * array2[11] + array[14] * array2[10] + array[15] * array2[9], array[10] * array2[15] + array[11] * array2[14] + array[12] * array2[13] + array[13] * array2[12] + array[14] * array2[11] + array[15] * array2[10], array[11] * array2[15] + array[12] * array2[14] + array[13] * array2[13] + array[14] * array2[12] + array[15] * array2[11], array[12] * array2[15] + array[13] * array2[14] + array[14] * array2[13] + array[15] * array2[12], array[13] * array2[15] + array[14] * array2[14] + array[15] * array2[13], array[14] * array2[15] + array[15] * array2[14], array[15] * array2[15]);
    }
    
    private void carryReduce(final long[] array, final long n, final long n2, final long n3, final long n4, final long n5, final long n6, final long n7, final long n8, long n9, long n10, long n11, long n12, long n13, long n14, long n15, long n16, long n17, long n18, long n19, long n20, long n21, long n22, long n23, final long n24, final long n25, final long n26, final long n27, final long n28, final long n29, final long n30, final long n31) {
        n9 += n25;
        n17 += n25;
        n10 += n26;
        n18 += n26;
        n11 += n27;
        n19 += n27;
        n12 += n28;
        n20 += n28;
        n13 += n29;
        n21 += n29;
        n14 += n30;
        n22 += n30;
        n15 += n31;
        n23 += n31;
        array[4] = n5 + n21;
        array[12] = n13 + n21;
        array[5] = n6 + n22;
        array[13] = n14 + n22;
        array[6] = n7 + n23;
        n15 += n23;
        array[7] = n8 + n24;
        n16 += n24;
        final long carryValue = this.carryValue(n15);
        array[14] = n15 - (carryValue << 28);
        n16 += carryValue;
        final long carryValue2 = this.carryValue(n16);
        array[15] = n16 - (carryValue2 << 28);
        n17 += carryValue2;
        array[0] = n + n17;
        array[8] = n9 + n17;
        array[1] = n2 + n18;
        array[9] = n10 + n18;
        array[2] = n3 + n19;
        array[10] = n11 + n19;
        array[3] = n4 + n20;
        array[11] = n12 + n20;
        this.carry(array, 0, 15);
    }
    
    @Override
    protected void square(final long[] array, final long[] array2) {
        this.carryReduce(array2, array[0] * array[0], 2L * array[0] * array[1], array[1] * array[1] + 2L * array[0] * array[2], 2L * (array[0] * array[3] + array[1] * array[2]), array[2] * array[2] + 2L * (array[0] * array[4] + array[1] * array[3]), 2L * (array[0] * array[5] + array[1] * array[4] + array[2] * array[3]), array[3] * array[3] + 2L * (array[0] * array[6] + array[1] * array[5] + array[2] * array[4]), 2L * (array[0] * array[7] + array[1] * array[6] + array[2] * array[5] + array[3] * array[4]), array[4] * array[4] + 2L * (array[0] * array[8] + array[1] * array[7] + array[2] * array[6] + array[3] * array[5]), 2L * (array[0] * array[9] + array[1] * array[8] + array[2] * array[7] + array[3] * array[6] + array[4] * array[5]), array[5] * array[5] + 2L * (array[0] * array[10] + array[1] * array[9] + array[2] * array[8] + array[3] * array[7] + array[4] * array[6]), 2L * (array[0] * array[11] + array[1] * array[10] + array[2] * array[9] + array[3] * array[8] + array[4] * array[7] + array[5] * array[6]), array[6] * array[6] + 2L * (array[0] * array[12] + array[1] * array[11] + array[2] * array[10] + array[3] * array[9] + array[4] * array[8] + array[5] * array[7]), 2L * (array[0] * array[13] + array[1] * array[12] + array[2] * array[11] + array[3] * array[10] + array[4] * array[9] + array[5] * array[8] + array[6] * array[7]), array[7] * array[7] + 2L * (array[0] * array[14] + array[1] * array[13] + array[2] * array[12] + array[3] * array[11] + array[4] * array[10] + array[5] * array[9] + array[6] * array[8]), 2L * (array[0] * array[15] + array[1] * array[14] + array[2] * array[13] + array[3] * array[12] + array[4] * array[11] + array[5] * array[10] + array[6] * array[9] + array[7] * array[8]), array[8] * array[8] + 2L * (array[1] * array[15] + array[2] * array[14] + array[3] * array[13] + array[4] * array[12] + array[5] * array[11] + array[6] * array[10] + array[7] * array[9]), 2L * (array[2] * array[15] + array[3] * array[14] + array[4] * array[13] + array[5] * array[12] + array[6] * array[11] + array[7] * array[10] + array[8] * array[9]), array[9] * array[9] + 2L * (array[3] * array[15] + array[4] * array[14] + array[5] * array[13] + array[6] * array[12] + array[7] * array[11] + array[8] * array[10]), 2L * (array[4] * array[15] + array[5] * array[14] + array[6] * array[13] + array[7] * array[12] + array[8] * array[11] + array[9] * array[10]), array[10] * array[10] + 2L * (array[5] * array[15] + array[6] * array[14] + array[7] * array[13] + array[8] * array[12] + array[9] * array[11]), 2L * (array[6] * array[15] + array[7] * array[14] + array[8] * array[13] + array[9] * array[12] + array[10] * array[11]), array[11] * array[11] + 2L * (array[7] * array[15] + array[8] * array[14] + array[9] * array[13] + array[10] * array[12]), 2L * (array[8] * array[15] + array[9] * array[14] + array[10] * array[13] + array[11] * array[12]), array[12] * array[12] + 2L * (array[9] * array[15] + array[10] * array[14] + array[11] * array[13]), 2L * (array[10] * array[15] + array[11] * array[14] + array[12] * array[13]), array[13] * array[13] + 2L * (array[11] * array[15] + array[12] * array[14]), 2L * (array[12] * array[15] + array[13] * array[14]), array[14] * array[14] + 2L * array[13] * array[15], 2L * array[14] * array[15], array[15] * array[15]);
    }
    
    static {
        MODULUS = IntegerPolynomial448.TWO.pow(448).subtract(IntegerPolynomial448.TWO.pow(224)).subtract(BigInteger.valueOf(1L));
    }
}
