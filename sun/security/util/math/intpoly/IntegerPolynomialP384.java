package sun.security.util.math.intpoly;

import java.math.BigInteger;

public class IntegerPolynomialP384 extends IntegerPolynomial
{
    private static final int BITS_PER_LIMB = 28;
    private static final int NUM_LIMBS = 14;
    private static final int MAX_ADDS = 2;
    public static final BigInteger MODULUS;
    private static final long CARRY_ADD = 134217728L;
    private static final int LIMB_MASK = 268435455;
    
    public IntegerPolynomialP384() {
        super(28, 14, 2, IntegerPolynomialP384.MODULUS);
    }
    
    private static BigInteger evaluateModulus() {
        return BigInteger.valueOf(2L).pow(384).subtract(BigInteger.valueOf(2L).pow(128)).subtract(BigInteger.valueOf(2L).pow(96)).add(BigInteger.valueOf(2L).pow(32)).subtract(BigInteger.valueOf(1L));
    }
    
    @Override
    protected void finalCarryReduceLast(final long[] array) {
        final long n = array[13] >> 20;
        final int n2 = 13;
        array[n2] -= n << 20;
        final int n3 = 4;
        array[n3] += (n << 16 & 0xFFFFFFFL);
        final int n4 = 5;
        array[n4] += n >> 12;
        final int n5 = 3;
        array[n5] += (n << 12 & 0xFFFFFFFL);
        final int n6 = 4;
        array[n6] += n >> 16;
        final int n7 = 1;
        array[n7] -= (n << 4 & 0xFFFFFFFL);
        final int n8 = 2;
        array[n8] -= n >> 24;
        final int n9 = 0;
        array[n9] += n;
    }
    
    private void carryReduce(final long[] array, long n, long n2, long n3, long n4, long n5, long n6, long n7, long n8, long n9, long n10, long n11, long n12, long n13, long n14, long n15, long n16, long n17, long n18, final long n19, final long n20, final long n21, final long n22, final long n23, final long n24, final long n25, final long n26, final long n27) {
        final long n28 = 0L;
        n17 += (n27 << 24 & 0xFFFFFFFL);
        n18 += n27 >> 4;
        n16 += (n27 << 20 & 0xFFFFFFFL);
        n17 += n27 >> 8;
        n14 -= (n27 << 12 & 0xFFFFFFFL);
        n15 -= n27 >> 16;
        n13 += (n27 << 8 & 0xFFFFFFFL);
        n14 += n27 >> 20;
        n16 += (n26 << 24 & 0xFFFFFFFL);
        n17 += n26 >> 4;
        n15 += (n26 << 20 & 0xFFFFFFFL);
        n16 += n26 >> 8;
        n13 -= (n26 << 12 & 0xFFFFFFFL);
        n14 -= n26 >> 16;
        n12 += (n26 << 8 & 0xFFFFFFFL);
        n13 += n26 >> 20;
        n15 += (n25 << 24 & 0xFFFFFFFL);
        n16 += n25 >> 4;
        n14 += (n25 << 20 & 0xFFFFFFFL);
        n15 += n25 >> 8;
        n12 -= (n25 << 12 & 0xFFFFFFFL);
        n13 -= n25 >> 16;
        n11 += (n25 << 8 & 0xFFFFFFFL);
        n12 += n25 >> 20;
        n14 += (n24 << 24 & 0xFFFFFFFL);
        n15 += n24 >> 4;
        n13 += (n24 << 20 & 0xFFFFFFFL);
        n14 += n24 >> 8;
        n11 -= (n24 << 12 & 0xFFFFFFFL);
        n12 -= n24 >> 16;
        n10 += (n24 << 8 & 0xFFFFFFFL);
        n11 += n24 >> 20;
        n13 += (n23 << 24 & 0xFFFFFFFL);
        n14 += n23 >> 4;
        n12 += (n23 << 20 & 0xFFFFFFFL);
        n13 += n23 >> 8;
        n10 -= (n23 << 12 & 0xFFFFFFFL);
        n11 -= n23 >> 16;
        n9 += (n23 << 8 & 0xFFFFFFFL);
        n10 += n23 >> 20;
        n12 += (n22 << 24 & 0xFFFFFFFL);
        n13 += n22 >> 4;
        n11 += (n22 << 20 & 0xFFFFFFFL);
        n12 += n22 >> 8;
        n9 -= (n22 << 12 & 0xFFFFFFFL);
        n10 -= n22 >> 16;
        n8 += (n22 << 8 & 0xFFFFFFFL);
        n9 += n22 >> 20;
        n11 += (n21 << 24 & 0xFFFFFFFL);
        n12 += n21 >> 4;
        n10 += (n21 << 20 & 0xFFFFFFFL);
        n11 += n21 >> 8;
        n8 -= (n21 << 12 & 0xFFFFFFFL);
        n9 -= n21 >> 16;
        n7 += (n21 << 8 & 0xFFFFFFFL);
        n8 += n21 >> 20;
        n10 += (n20 << 24 & 0xFFFFFFFL);
        n11 += n20 >> 4;
        n9 += (n20 << 20 & 0xFFFFFFFL);
        n10 += n20 >> 8;
        n7 -= (n20 << 12 & 0xFFFFFFFL);
        n8 -= n20 >> 16;
        n6 += (n20 << 8 & 0xFFFFFFFL);
        n7 += n20 >> 20;
        n9 += (n19 << 24 & 0xFFFFFFFL);
        n10 += n19 >> 4;
        n8 += (n19 << 20 & 0xFFFFFFFL);
        n9 += n19 >> 8;
        n6 -= (n19 << 12 & 0xFFFFFFFL);
        n7 -= n19 >> 16;
        n5 += (n19 << 8 & 0xFFFFFFFL);
        n6 += n19 >> 20;
        n8 += (n18 << 24 & 0xFFFFFFFL);
        n9 += n18 >> 4;
        n7 += (n18 << 20 & 0xFFFFFFFL);
        n8 += n18 >> 8;
        n5 -= (n18 << 12 & 0xFFFFFFFL);
        n6 -= n18 >> 16;
        n4 += (n18 << 8 & 0xFFFFFFFL);
        n5 += n18 >> 20;
        n7 += (n17 << 24 & 0xFFFFFFFL);
        n8 += n17 >> 4;
        n6 += (n17 << 20 & 0xFFFFFFFL);
        n7 += n17 >> 8;
        n4 -= (n17 << 12 & 0xFFFFFFFL);
        n5 -= n17 >> 16;
        n3 += (n17 << 8 & 0xFFFFFFFL);
        n4 += n17 >> 20;
        n6 += (n16 << 24 & 0xFFFFFFFL);
        n7 += n16 >> 4;
        n5 += (n16 << 20 & 0xFFFFFFFL);
        n6 += n16 >> 8;
        n3 -= (n16 << 12 & 0xFFFFFFFL);
        n4 -= n16 >> 16;
        n2 += (n16 << 8 & 0xFFFFFFFL);
        n3 += n16 >> 20;
        n5 += (n15 << 24 & 0xFFFFFFFL);
        n6 += n15 >> 4;
        n4 += (n15 << 20 & 0xFFFFFFFL);
        n5 += n15 >> 8;
        n2 -= (n15 << 12 & 0xFFFFFFFL);
        n3 -= n15 >> 16;
        n += (n15 << 8 & 0xFFFFFFFL);
        n2 += n15 >> 20;
        n15 = 0L;
        this.carryReduce0(array, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27, n28);
    }
    
    void carryReduce0(final long[] array, long n, long n2, long n3, long n4, long n5, long n6, long n7, long n8, long n9, long n10, long n11, long n12, long n13, long n14, long n15, final long n16, final long n17, final long n18, final long n19, final long n20, final long n21, final long n22, final long n23, final long n24, final long n25, final long n26, final long n27, final long n28) {
        final long n29 = n13 + 134217728L >> 28;
        n13 -= n29 << 28;
        n14 += n29;
        final long n30 = n14 + 134217728L >> 28;
        n14 -= n30 << 28;
        n15 += n30;
        n5 += (n15 << 24 & 0xFFFFFFFL);
        n6 += n15 >> 4;
        n4 += (n15 << 20 & 0xFFFFFFFL);
        n5 += n15 >> 8;
        n2 -= (n15 << 12 & 0xFFFFFFFL);
        n3 -= n15 >> 16;
        n += (n15 << 8 & 0xFFFFFFFL);
        n2 += n15 >> 20;
        final long n31 = n + 134217728L >> 28;
        n -= n31 << 28;
        n2 += n31;
        final long n32 = n2 + 134217728L >> 28;
        n2 -= n32 << 28;
        n3 += n32;
        final long n33 = n3 + 134217728L >> 28;
        n3 -= n33 << 28;
        n4 += n33;
        final long n34 = n4 + 134217728L >> 28;
        n4 -= n34 << 28;
        n5 += n34;
        final long n35 = n5 + 134217728L >> 28;
        n5 -= n35 << 28;
        n6 += n35;
        final long n36 = n6 + 134217728L >> 28;
        n6 -= n36 << 28;
        n7 += n36;
        final long n37 = n7 + 134217728L >> 28;
        n7 -= n37 << 28;
        n8 += n37;
        final long n38 = n8 + 134217728L >> 28;
        n8 -= n38 << 28;
        n9 += n38;
        final long n39 = n9 + 134217728L >> 28;
        n9 -= n39 << 28;
        n10 += n39;
        final long n40 = n10 + 134217728L >> 28;
        n10 -= n40 << 28;
        n11 += n40;
        final long n41 = n11 + 134217728L >> 28;
        n11 -= n41 << 28;
        n12 += n41;
        final long n42 = n12 + 134217728L >> 28;
        n12 -= n42 << 28;
        n13 += n42;
        final long n43 = n13 + 134217728L >> 28;
        n13 -= n43 << 28;
        n14 += n43;
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
        array[10] = n11;
        array[11] = n12;
        array[12] = n13;
        array[13] = n14;
    }
    
    private void carryReduce(final long[] array, long n, long n2, long n3, long n4, long n5, long n6, long n7, long n8, long n9, long n10, long n11, long n12, long n13, long n14) {
        final long n15 = 0L;
        final long n16 = n13 + 134217728L >> 28;
        n13 -= n16 << 28;
        n14 += n16;
        final long n17 = n14 + 134217728L >> 28;
        n14 -= n17 << 28;
        final int n18 = (int)(n15 + n17);
        n5 += (n18 << 24 & 0xFFFFFFFL);
        n6 += n18 >> 4;
        n4 += (n18 << 20 & 0xFFFFFFFL);
        n5 += n18 >> 8;
        n2 -= (n18 << 12 & 0xFFFFFFFL);
        n3 -= n18 >> 16;
        n += (n18 << 8 & 0xFFFFFFFL);
        n2 += n18 >> 20;
        final long n19 = n + 134217728L >> 28;
        n -= n19 << 28;
        n2 += n19;
        final long n20 = n2 + 134217728L >> 28;
        n2 -= n20 << 28;
        n3 += n20;
        final long n21 = n3 + 134217728L >> 28;
        n3 -= n21 << 28;
        n4 += n21;
        final long n22 = n4 + 134217728L >> 28;
        n4 -= n22 << 28;
        n5 += n22;
        final long n23 = n5 + 134217728L >> 28;
        n5 -= n23 << 28;
        n6 += n23;
        final long n24 = n6 + 134217728L >> 28;
        n6 -= n24 << 28;
        n7 += n24;
        final long n25 = n7 + 134217728L >> 28;
        n7 -= n25 << 28;
        n8 += n25;
        final long n26 = n8 + 134217728L >> 28;
        n8 -= n26 << 28;
        n9 += n26;
        final long n27 = n9 + 134217728L >> 28;
        n9 -= n27 << 28;
        n10 += n27;
        final long n28 = n10 + 134217728L >> 28;
        n10 -= n28 << 28;
        n11 += n28;
        final long n29 = n11 + 134217728L >> 28;
        n11 -= n29 << 28;
        n12 += n29;
        final long n30 = n12 + 134217728L >> 28;
        n12 -= n30 << 28;
        n13 += n30;
        final long n31 = n13 + 134217728L >> 28;
        n13 -= n31 << 28;
        n14 += n31;
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
        array[10] = n11;
        array[11] = n12;
        array[12] = n13;
        array[13] = n14;
    }
    
    @Override
    protected void mult(final long[] array, final long[] array2, final long[] array3) {
        this.carryReduce(array3, array[0] * array2[0], array[0] * array2[1] + array[1] * array2[0], array[0] * array2[2] + array[1] * array2[1] + array[2] * array2[0], array[0] * array2[3] + array[1] * array2[2] + array[2] * array2[1] + array[3] * array2[0], array[0] * array2[4] + array[1] * array2[3] + array[2] * array2[2] + array[3] * array2[1] + array[4] * array2[0], array[0] * array2[5] + array[1] * array2[4] + array[2] * array2[3] + array[3] * array2[2] + array[4] * array2[1] + array[5] * array2[0], array[0] * array2[6] + array[1] * array2[5] + array[2] * array2[4] + array[3] * array2[3] + array[4] * array2[2] + array[5] * array2[1] + array[6] * array2[0], array[0] * array2[7] + array[1] * array2[6] + array[2] * array2[5] + array[3] * array2[4] + array[4] * array2[3] + array[5] * array2[2] + array[6] * array2[1] + array[7] * array2[0], array[0] * array2[8] + array[1] * array2[7] + array[2] * array2[6] + array[3] * array2[5] + array[4] * array2[4] + array[5] * array2[3] + array[6] * array2[2] + array[7] * array2[1] + array[8] * array2[0], array[0] * array2[9] + array[1] * array2[8] + array[2] * array2[7] + array[3] * array2[6] + array[4] * array2[5] + array[5] * array2[4] + array[6] * array2[3] + array[7] * array2[2] + array[8] * array2[1] + array[9] * array2[0], array[0] * array2[10] + array[1] * array2[9] + array[2] * array2[8] + array[3] * array2[7] + array[4] * array2[6] + array[5] * array2[5] + array[6] * array2[4] + array[7] * array2[3] + array[8] * array2[2] + array[9] * array2[1] + array[10] * array2[0], array[0] * array2[11] + array[1] * array2[10] + array[2] * array2[9] + array[3] * array2[8] + array[4] * array2[7] + array[5] * array2[6] + array[6] * array2[5] + array[7] * array2[4] + array[8] * array2[3] + array[9] * array2[2] + array[10] * array2[1] + array[11] * array2[0], array[0] * array2[12] + array[1] * array2[11] + array[2] * array2[10] + array[3] * array2[9] + array[4] * array2[8] + array[5] * array2[7] + array[6] * array2[6] + array[7] * array2[5] + array[8] * array2[4] + array[9] * array2[3] + array[10] * array2[2] + array[11] * array2[1] + array[12] * array2[0], array[0] * array2[13] + array[1] * array2[12] + array[2] * array2[11] + array[3] * array2[10] + array[4] * array2[9] + array[5] * array2[8] + array[6] * array2[7] + array[7] * array2[6] + array[8] * array2[5] + array[9] * array2[4] + array[10] * array2[3] + array[11] * array2[2] + array[12] * array2[1] + array[13] * array2[0], array[1] * array2[13] + array[2] * array2[12] + array[3] * array2[11] + array[4] * array2[10] + array[5] * array2[9] + array[6] * array2[8] + array[7] * array2[7] + array[8] * array2[6] + array[9] * array2[5] + array[10] * array2[4] + array[11] * array2[3] + array[12] * array2[2] + array[13] * array2[1], array[2] * array2[13] + array[3] * array2[12] + array[4] * array2[11] + array[5] * array2[10] + array[6] * array2[9] + array[7] * array2[8] + array[8] * array2[7] + array[9] * array2[6] + array[10] * array2[5] + array[11] * array2[4] + array[12] * array2[3] + array[13] * array2[2], array[3] * array2[13] + array[4] * array2[12] + array[5] * array2[11] + array[6] * array2[10] + array[7] * array2[9] + array[8] * array2[8] + array[9] * array2[7] + array[10] * array2[6] + array[11] * array2[5] + array[12] * array2[4] + array[13] * array2[3], array[4] * array2[13] + array[5] * array2[12] + array[6] * array2[11] + array[7] * array2[10] + array[8] * array2[9] + array[9] * array2[8] + array[10] * array2[7] + array[11] * array2[6] + array[12] * array2[5] + array[13] * array2[4], array[5] * array2[13] + array[6] * array2[12] + array[7] * array2[11] + array[8] * array2[10] + array[9] * array2[9] + array[10] * array2[8] + array[11] * array2[7] + array[12] * array2[6] + array[13] * array2[5], array[6] * array2[13] + array[7] * array2[12] + array[8] * array2[11] + array[9] * array2[10] + array[10] * array2[9] + array[11] * array2[8] + array[12] * array2[7] + array[13] * array2[6], array[7] * array2[13] + array[8] * array2[12] + array[9] * array2[11] + array[10] * array2[10] + array[11] * array2[9] + array[12] * array2[8] + array[13] * array2[7], array[8] * array2[13] + array[9] * array2[12] + array[10] * array2[11] + array[11] * array2[10] + array[12] * array2[9] + array[13] * array2[8], array[9] * array2[13] + array[10] * array2[12] + array[11] * array2[11] + array[12] * array2[10] + array[13] * array2[9], array[10] * array2[13] + array[11] * array2[12] + array[12] * array2[11] + array[13] * array2[10], array[11] * array2[13] + array[12] * array2[12] + array[13] * array2[11], array[12] * array2[13] + array[13] * array2[12], array[13] * array2[13]);
    }
    
    @Override
    protected void reduce(final long[] array) {
        this.carryReduce(array, array[0], array[1], array[2], array[3], array[4], array[5], array[6], array[7], array[8], array[9], array[10], array[11], array[12], array[13]);
    }
    
    @Override
    protected void square(final long[] array, final long[] array2) {
        this.carryReduce(array2, array[0] * array[0], 2L * (array[0] * array[1]), 2L * (array[0] * array[2]) + array[1] * array[1], 2L * (array[0] * array[3] + array[1] * array[2]), 2L * (array[0] * array[4] + array[1] * array[3]) + array[2] * array[2], 2L * (array[0] * array[5] + array[1] * array[4] + array[2] * array[3]), 2L * (array[0] * array[6] + array[1] * array[5] + array[2] * array[4]) + array[3] * array[3], 2L * (array[0] * array[7] + array[1] * array[6] + array[2] * array[5] + array[3] * array[4]), 2L * (array[0] * array[8] + array[1] * array[7] + array[2] * array[6] + array[3] * array[5]) + array[4] * array[4], 2L * (array[0] * array[9] + array[1] * array[8] + array[2] * array[7] + array[3] * array[6] + array[4] * array[5]), 2L * (array[0] * array[10] + array[1] * array[9] + array[2] * array[8] + array[3] * array[7] + array[4] * array[6]) + array[5] * array[5], 2L * (array[0] * array[11] + array[1] * array[10] + array[2] * array[9] + array[3] * array[8] + array[4] * array[7] + array[5] * array[6]), 2L * (array[0] * array[12] + array[1] * array[11] + array[2] * array[10] + array[3] * array[9] + array[4] * array[8] + array[5] * array[7]) + array[6] * array[6], 2L * (array[0] * array[13] + array[1] * array[12] + array[2] * array[11] + array[3] * array[10] + array[4] * array[9] + array[5] * array[8] + array[6] * array[7]), 2L * (array[1] * array[13] + array[2] * array[12] + array[3] * array[11] + array[4] * array[10] + array[5] * array[9] + array[6] * array[8]) + array[7] * array[7], 2L * (array[2] * array[13] + array[3] * array[12] + array[4] * array[11] + array[5] * array[10] + array[6] * array[9] + array[7] * array[8]), 2L * (array[3] * array[13] + array[4] * array[12] + array[5] * array[11] + array[6] * array[10] + array[7] * array[9]) + array[8] * array[8], 2L * (array[4] * array[13] + array[5] * array[12] + array[6] * array[11] + array[7] * array[10] + array[8] * array[9]), 2L * (array[5] * array[13] + array[6] * array[12] + array[7] * array[11] + array[8] * array[10]) + array[9] * array[9], 2L * (array[6] * array[13] + array[7] * array[12] + array[8] * array[11] + array[9] * array[10]), 2L * (array[7] * array[13] + array[8] * array[12] + array[9] * array[11]) + array[10] * array[10], 2L * (array[8] * array[13] + array[9] * array[12] + array[10] * array[11]), 2L * (array[9] * array[13] + array[10] * array[12]) + array[11] * array[11], 2L * (array[10] * array[13] + array[11] * array[12]), 2L * (array[11] * array[13]) + array[12] * array[12], 2L * (array[12] * array[13]), array[13] * array[13]);
    }
    
    static {
        MODULUS = evaluateModulus();
    }
}
