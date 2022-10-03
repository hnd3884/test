package sun.security.util.math.intpoly;

import java.math.BigInteger;

public class IntegerPolynomialP256 extends IntegerPolynomial
{
    private static final int BITS_PER_LIMB = 26;
    private static final int NUM_LIMBS = 10;
    private static final int MAX_ADDS = 2;
    public static final BigInteger MODULUS;
    private static final long CARRY_ADD = 33554432L;
    private static final int LIMB_MASK = 67108863;
    
    public IntegerPolynomialP256() {
        super(26, 10, 2, IntegerPolynomialP256.MODULUS);
    }
    
    private static BigInteger evaluateModulus() {
        return BigInteger.valueOf(2L).pow(256).subtract(BigInteger.valueOf(2L).pow(224)).add(BigInteger.valueOf(2L).pow(192)).add(BigInteger.valueOf(2L).pow(96)).subtract(BigInteger.valueOf(1L));
    }
    
    @Override
    protected void finalCarryReduceLast(final long[] array) {
        final long n = array[9] >> 22;
        final int n2 = 9;
        array[n2] -= n << 22;
        final int n3 = 8;
        array[n3] += (n << 16 & 0x3FFFFFFL);
        final int n4 = 9;
        array[n4] += n >> 10;
        final int n5 = 7;
        array[n5] -= (n << 10 & 0x3FFFFFFL);
        final int n6 = 8;
        array[n6] -= n >> 16;
        final int n7 = 3;
        array[n7] -= (n << 18 & 0x3FFFFFFL);
        final int n8 = 4;
        array[n8] -= n >> 8;
        final int n9 = 0;
        array[n9] += n;
    }
    
    private void carryReduce(final long[] array, long n, long n2, long n3, long n4, long n5, long n6, long n7, long n8, long n9, long n10, long n11, long n12, long n13, long n14, long n15, long n16, long n17, long n18, final long n19) {
        final long n20 = 0L;
        n17 += (n19 << 20 & 0x3FFFFFFL);
        n18 += n19 >> 6;
        n16 -= (n19 << 14 & 0x3FFFFFFL);
        n17 -= n19 >> 12;
        n12 -= (n19 << 22 & 0x3FFFFFFL);
        n13 -= n19 >> 4;
        n9 += (n19 << 4 & 0x3FFFFFFL);
        n10 += n19 >> 22;
        n16 += (n18 << 20 & 0x3FFFFFFL);
        n17 += n18 >> 6;
        n15 -= (n18 << 14 & 0x3FFFFFFL);
        n16 -= n18 >> 12;
        n11 -= (n18 << 22 & 0x3FFFFFFL);
        n12 -= n18 >> 4;
        n8 += (n18 << 4 & 0x3FFFFFFL);
        n9 += n18 >> 22;
        n15 += (n17 << 20 & 0x3FFFFFFL);
        n16 += n17 >> 6;
        n14 -= (n17 << 14 & 0x3FFFFFFL);
        n15 -= n17 >> 12;
        n10 -= (n17 << 22 & 0x3FFFFFFL);
        n11 -= n17 >> 4;
        n7 += (n17 << 4 & 0x3FFFFFFL);
        n8 += n17 >> 22;
        n14 += (n16 << 20 & 0x3FFFFFFL);
        n15 += n16 >> 6;
        n13 -= (n16 << 14 & 0x3FFFFFFL);
        n14 -= n16 >> 12;
        n9 -= (n16 << 22 & 0x3FFFFFFL);
        n10 -= n16 >> 4;
        n6 += (n16 << 4 & 0x3FFFFFFL);
        n7 += n16 >> 22;
        n13 += (n15 << 20 & 0x3FFFFFFL);
        n14 += n15 >> 6;
        n12 -= (n15 << 14 & 0x3FFFFFFL);
        n13 -= n15 >> 12;
        n8 -= (n15 << 22 & 0x3FFFFFFL);
        n9 -= n15 >> 4;
        n5 += (n15 << 4 & 0x3FFFFFFL);
        n6 += n15 >> 22;
        n12 += (n14 << 20 & 0x3FFFFFFL);
        n13 += n14 >> 6;
        n11 -= (n14 << 14 & 0x3FFFFFFL);
        n12 -= n14 >> 12;
        n7 -= (n14 << 22 & 0x3FFFFFFL);
        n8 -= n14 >> 4;
        n4 += (n14 << 4 & 0x3FFFFFFL);
        n5 += n14 >> 22;
        n11 += (n13 << 20 & 0x3FFFFFFL);
        n12 += n13 >> 6;
        n10 -= (n13 << 14 & 0x3FFFFFFL);
        n11 -= n13 >> 12;
        n6 -= (n13 << 22 & 0x3FFFFFFL);
        n7 -= n13 >> 4;
        n3 += (n13 << 4 & 0x3FFFFFFL);
        n4 += n13 >> 22;
        n10 += (n12 << 20 & 0x3FFFFFFL);
        n11 += n12 >> 6;
        n9 -= (n12 << 14 & 0x3FFFFFFL);
        n10 -= n12 >> 12;
        n5 -= (n12 << 22 & 0x3FFFFFFL);
        n6 -= n12 >> 4;
        n2 += (n12 << 4 & 0x3FFFFFFL);
        n3 += n12 >> 22;
        n9 += (n11 << 20 & 0x3FFFFFFL);
        n10 += n11 >> 6;
        n8 -= (n11 << 14 & 0x3FFFFFFL);
        n9 -= n11 >> 12;
        n4 -= (n11 << 22 & 0x3FFFFFFL);
        n5 -= n11 >> 4;
        n += (n11 << 4 & 0x3FFFFFFL);
        n2 += n11 >> 22;
        n11 = 0L;
        this.carryReduce0(array, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20);
    }
    
    void carryReduce0(final long[] array, long n, long n2, long n3, long n4, long n5, long n6, long n7, long n8, long n9, long n10, long n11, final long n12, final long n13, final long n14, final long n15, final long n16, final long n17, final long n18, final long n19, final long n20) {
        final long n21 = n9 + 33554432L >> 26;
        n9 -= n21 << 26;
        n10 += n21;
        final long n22 = n10 + 33554432L >> 26;
        n10 -= n22 << 26;
        n11 += n22;
        n9 += (n11 << 20 & 0x3FFFFFFL);
        n10 += n11 >> 6;
        n8 -= (n11 << 14 & 0x3FFFFFFL);
        n9 -= n11 >> 12;
        n4 -= (n11 << 22 & 0x3FFFFFFL);
        n5 -= n11 >> 4;
        n += (n11 << 4 & 0x3FFFFFFL);
        n2 += n11 >> 22;
        final long n23 = n + 33554432L >> 26;
        n -= n23 << 26;
        n2 += n23;
        final long n24 = n2 + 33554432L >> 26;
        n2 -= n24 << 26;
        n3 += n24;
        final long n25 = n3 + 33554432L >> 26;
        n3 -= n25 << 26;
        n4 += n25;
        final long n26 = n4 + 33554432L >> 26;
        n4 -= n26 << 26;
        n5 += n26;
        final long n27 = n5 + 33554432L >> 26;
        n5 -= n27 << 26;
        n6 += n27;
        final long n28 = n6 + 33554432L >> 26;
        n6 -= n28 << 26;
        n7 += n28;
        final long n29 = n7 + 33554432L >> 26;
        n7 -= n29 << 26;
        n8 += n29;
        final long n30 = n8 + 33554432L >> 26;
        n8 -= n30 << 26;
        n9 += n30;
        final long n31 = n9 + 33554432L >> 26;
        n9 -= n31 << 26;
        n10 += n31;
        array[0] = n;
        array[1] = n2;
        array[2] = n3;
        array[3] = n4;
        array[4] = n5;
        array[5] = n6;
        array[6] = n7;
        array[7] = n8;
        array[8] = n9;
        array[9] = n10;
    }
    
    private void carryReduce(final long[] array, long n, long n2, long n3, long n4, long n5, long n6, long n7, long n8, long n9, long n10) {
        final long n11 = 0L;
        final long n12 = n9 + 33554432L >> 26;
        n9 -= n12 << 26;
        n10 += n12;
        final long n13 = n10 + 33554432L >> 26;
        n10 -= n13 << 26;
        final int n14 = (int)(n11 + n13);
        n9 += (n14 << 20 & 0x3FFFFFFL);
        n10 += n14 >> 6;
        n8 -= (n14 << 14 & 0x3FFFFFFL);
        n9 -= n14 >> 12;
        n4 -= (n14 << 22 & 0x3FFFFFFL);
        n5 -= n14 >> 4;
        n += (n14 << 4 & 0x3FFFFFFL);
        n2 += n14 >> 22;
        final long n15 = n + 33554432L >> 26;
        n -= n15 << 26;
        n2 += n15;
        final long n16 = n2 + 33554432L >> 26;
        n2 -= n16 << 26;
        n3 += n16;
        final long n17 = n3 + 33554432L >> 26;
        n3 -= n17 << 26;
        n4 += n17;
        final long n18 = n4 + 33554432L >> 26;
        n4 -= n18 << 26;
        n5 += n18;
        final long n19 = n5 + 33554432L >> 26;
        n5 -= n19 << 26;
        n6 += n19;
        final long n20 = n6 + 33554432L >> 26;
        n6 -= n20 << 26;
        n7 += n20;
        final long n21 = n7 + 33554432L >> 26;
        n7 -= n21 << 26;
        n8 += n21;
        final long n22 = n8 + 33554432L >> 26;
        n8 -= n22 << 26;
        n9 += n22;
        final long n23 = n9 + 33554432L >> 26;
        n9 -= n23 << 26;
        n10 += n23;
        array[0] = n;
        array[1] = n2;
        array[2] = n3;
        array[3] = n4;
        array[4] = n5;
        array[5] = n6;
        array[6] = n7;
        array[7] = n8;
        array[8] = n9;
        array[9] = n10;
    }
    
    @Override
    protected void mult(final long[] array, final long[] array2, final long[] array3) {
        this.carryReduce(array3, array[0] * array2[0], array[0] * array2[1] + array[1] * array2[0], array[0] * array2[2] + array[1] * array2[1] + array[2] * array2[0], array[0] * array2[3] + array[1] * array2[2] + array[2] * array2[1] + array[3] * array2[0], array[0] * array2[4] + array[1] * array2[3] + array[2] * array2[2] + array[3] * array2[1] + array[4] * array2[0], array[0] * array2[5] + array[1] * array2[4] + array[2] * array2[3] + array[3] * array2[2] + array[4] * array2[1] + array[5] * array2[0], array[0] * array2[6] + array[1] * array2[5] + array[2] * array2[4] + array[3] * array2[3] + array[4] * array2[2] + array[5] * array2[1] + array[6] * array2[0], array[0] * array2[7] + array[1] * array2[6] + array[2] * array2[5] + array[3] * array2[4] + array[4] * array2[3] + array[5] * array2[2] + array[6] * array2[1] + array[7] * array2[0], array[0] * array2[8] + array[1] * array2[7] + array[2] * array2[6] + array[3] * array2[5] + array[4] * array2[4] + array[5] * array2[3] + array[6] * array2[2] + array[7] * array2[1] + array[8] * array2[0], array[0] * array2[9] + array[1] * array2[8] + array[2] * array2[7] + array[3] * array2[6] + array[4] * array2[5] + array[5] * array2[4] + array[6] * array2[3] + array[7] * array2[2] + array[8] * array2[1] + array[9] * array2[0], array[1] * array2[9] + array[2] * array2[8] + array[3] * array2[7] + array[4] * array2[6] + array[5] * array2[5] + array[6] * array2[4] + array[7] * array2[3] + array[8] * array2[2] + array[9] * array2[1], array[2] * array2[9] + array[3] * array2[8] + array[4] * array2[7] + array[5] * array2[6] + array[6] * array2[5] + array[7] * array2[4] + array[8] * array2[3] + array[9] * array2[2], array[3] * array2[9] + array[4] * array2[8] + array[5] * array2[7] + array[6] * array2[6] + array[7] * array2[5] + array[8] * array2[4] + array[9] * array2[3], array[4] * array2[9] + array[5] * array2[8] + array[6] * array2[7] + array[7] * array2[6] + array[8] * array2[5] + array[9] * array2[4], array[5] * array2[9] + array[6] * array2[8] + array[7] * array2[7] + array[8] * array2[6] + array[9] * array2[5], array[6] * array2[9] + array[7] * array2[8] + array[8] * array2[7] + array[9] * array2[6], array[7] * array2[9] + array[8] * array2[8] + array[9] * array2[7], array[8] * array2[9] + array[9] * array2[8], array[9] * array2[9]);
    }
    
    @Override
    protected void reduce(final long[] array) {
        this.carryReduce(array, array[0], array[1], array[2], array[3], array[4], array[5], array[6], array[7], array[8], array[9]);
    }
    
    @Override
    protected void square(final long[] array, final long[] array2) {
        this.carryReduce(array2, array[0] * array[0], 2L * (array[0] * array[1]), 2L * (array[0] * array[2]) + array[1] * array[1], 2L * (array[0] * array[3] + array[1] * array[2]), 2L * (array[0] * array[4] + array[1] * array[3]) + array[2] * array[2], 2L * (array[0] * array[5] + array[1] * array[4] + array[2] * array[3]), 2L * (array[0] * array[6] + array[1] * array[5] + array[2] * array[4]) + array[3] * array[3], 2L * (array[0] * array[7] + array[1] * array[6] + array[2] * array[5] + array[3] * array[4]), 2L * (array[0] * array[8] + array[1] * array[7] + array[2] * array[6] + array[3] * array[5]) + array[4] * array[4], 2L * (array[0] * array[9] + array[1] * array[8] + array[2] * array[7] + array[3] * array[6] + array[4] * array[5]), 2L * (array[1] * array[9] + array[2] * array[8] + array[3] * array[7] + array[4] * array[6]) + array[5] * array[5], 2L * (array[2] * array[9] + array[3] * array[8] + array[4] * array[7] + array[5] * array[6]), 2L * (array[3] * array[9] + array[4] * array[8] + array[5] * array[7]) + array[6] * array[6], 2L * (array[4] * array[9] + array[5] * array[8] + array[6] * array[7]), 2L * (array[5] * array[9] + array[6] * array[8]) + array[7] * array[7], 2L * (array[6] * array[9] + array[7] * array[8]), 2L * (array[7] * array[9]) + array[8] * array[8], 2L * (array[8] * array[9]), array[9] * array[9]);
    }
    
    static {
        MODULUS = evaluateModulus();
    }
}
