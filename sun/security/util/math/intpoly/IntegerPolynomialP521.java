package sun.security.util.math.intpoly;

import java.math.BigInteger;

public class IntegerPolynomialP521 extends IntegerPolynomial
{
    private static final int BITS_PER_LIMB = 28;
    private static final int NUM_LIMBS = 19;
    private static final int MAX_ADDS = 2;
    public static final BigInteger MODULUS;
    private static final long CARRY_ADD = 134217728L;
    private static final int LIMB_MASK = 268435455;
    
    public IntegerPolynomialP521() {
        super(28, 19, 2, IntegerPolynomialP521.MODULUS);
    }
    
    private static BigInteger evaluateModulus() {
        return BigInteger.valueOf(2L).pow(521).subtract(BigInteger.valueOf(1L));
    }
    
    @Override
    protected void finalCarryReduceLast(final long[] array) {
        final long n = array[18] >> 17;
        final int n2 = 18;
        array[n2] -= n << 17;
        final int n3 = 0;
        array[n3] += n;
    }
    
    private void carryReduce(final long[] array, long n, long n2, long n3, long n4, long n5, long n6, long n7, long n8, long n9, long n10, long n11, long n12, long n13, long n14, long n15, long n16, long n17, long n18, long n19, long n20, final long n21, final long n22, final long n23, final long n24, final long n25, final long n26, final long n27, final long n28, final long n29, final long n30, final long n31, final long n32, final long n33, final long n34, final long n35, final long n36, final long n37) {
        final long n38 = 0L;
        n18 += (n37 << 11 & 0xFFFFFFFL);
        n19 += n37 >> 17;
        n17 += (n36 << 11 & 0xFFFFFFFL);
        n18 += n36 >> 17;
        n16 += (n35 << 11 & 0xFFFFFFFL);
        n17 += n35 >> 17;
        n15 += (n34 << 11 & 0xFFFFFFFL);
        n16 += n34 >> 17;
        n14 += (n33 << 11 & 0xFFFFFFFL);
        n15 += n33 >> 17;
        n13 += (n32 << 11 & 0xFFFFFFFL);
        n14 += n32 >> 17;
        n12 += (n31 << 11 & 0xFFFFFFFL);
        n13 += n31 >> 17;
        n11 += (n30 << 11 & 0xFFFFFFFL);
        n12 += n30 >> 17;
        n10 += (n29 << 11 & 0xFFFFFFFL);
        n11 += n29 >> 17;
        n9 += (n28 << 11 & 0xFFFFFFFL);
        n10 += n28 >> 17;
        n8 += (n27 << 11 & 0xFFFFFFFL);
        n9 += n27 >> 17;
        n7 += (n26 << 11 & 0xFFFFFFFL);
        n8 += n26 >> 17;
        n6 += (n25 << 11 & 0xFFFFFFFL);
        n7 += n25 >> 17;
        n5 += (n24 << 11 & 0xFFFFFFFL);
        n6 += n24 >> 17;
        n4 += (n23 << 11 & 0xFFFFFFFL);
        n5 += n23 >> 17;
        n3 += (n22 << 11 & 0xFFFFFFFL);
        n4 += n22 >> 17;
        n2 += (n21 << 11 & 0xFFFFFFFL);
        n3 += n21 >> 17;
        n += (n20 << 11 & 0xFFFFFFFL);
        n2 += n20 >> 17;
        n20 = 0L;
        this.carryReduce0(array, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27, n28, n29, n30, n31, n32, n33, n34, n35, n36, n37, n38);
    }
    
    void carryReduce0(final long[] array, long n, long n2, long n3, long n4, long n5, long n6, long n7, long n8, long n9, long n10, long n11, long n12, long n13, long n14, long n15, long n16, long n17, long n18, long n19, long n20, final long n21, final long n22, final long n23, final long n24, final long n25, final long n26, final long n27, final long n28, final long n29, final long n30, final long n31, final long n32, final long n33, final long n34, final long n35, final long n36, final long n37, final long n38) {
        final long n39 = n18 + 134217728L >> 28;
        n18 -= n39 << 28;
        n19 += n39;
        final long n40 = n19 + 134217728L >> 28;
        n19 -= n40 << 28;
        n20 += n40;
        n += (n20 << 11 & 0xFFFFFFFL);
        n2 += n20 >> 17;
        final long n41 = n + 134217728L >> 28;
        n -= n41 << 28;
        n2 += n41;
        final long n42 = n2 + 134217728L >> 28;
        n2 -= n42 << 28;
        n3 += n42;
        final long n43 = n3 + 134217728L >> 28;
        n3 -= n43 << 28;
        n4 += n43;
        final long n44 = n4 + 134217728L >> 28;
        n4 -= n44 << 28;
        n5 += n44;
        final long n45 = n5 + 134217728L >> 28;
        n5 -= n45 << 28;
        n6 += n45;
        final long n46 = n6 + 134217728L >> 28;
        n6 -= n46 << 28;
        n7 += n46;
        final long n47 = n7 + 134217728L >> 28;
        n7 -= n47 << 28;
        n8 += n47;
        final long n48 = n8 + 134217728L >> 28;
        n8 -= n48 << 28;
        n9 += n48;
        final long n49 = n9 + 134217728L >> 28;
        n9 -= n49 << 28;
        n10 += n49;
        final long n50 = n10 + 134217728L >> 28;
        n10 -= n50 << 28;
        n11 += n50;
        final long n51 = n11 + 134217728L >> 28;
        n11 -= n51 << 28;
        n12 += n51;
        final long n52 = n12 + 134217728L >> 28;
        n12 -= n52 << 28;
        n13 += n52;
        final long n53 = n13 + 134217728L >> 28;
        n13 -= n53 << 28;
        n14 += n53;
        final long n54 = n14 + 134217728L >> 28;
        n14 -= n54 << 28;
        n15 += n54;
        final long n55 = n15 + 134217728L >> 28;
        n15 -= n55 << 28;
        n16 += n55;
        final long n56 = n16 + 134217728L >> 28;
        n16 -= n56 << 28;
        n17 += n56;
        final long n57 = n17 + 134217728L >> 28;
        n17 -= n57 << 28;
        n18 += n57;
        final long n58 = n18 + 134217728L >> 28;
        n18 -= n58 << 28;
        n19 += n58;
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
        array[14] = n15;
        array[15] = n16;
        array[16] = n17;
        array[17] = n18;
        array[18] = n19;
    }
    
    private void carryReduce(final long[] array, long n, long n2, long n3, long n4, long n5, long n6, long n7, long n8, long n9, long n10, long n11, long n12, long n13, long n14, long n15, long n16, long n17, long n18, long n19) {
        final long n20 = 0L;
        final long n21 = n18 + 134217728L >> 28;
        n18 -= n21 << 28;
        n19 += n21;
        final long n22 = n19 + 134217728L >> 28;
        n19 -= n22 << 28;
        final int n23 = (int)(n20 + n22);
        n += (n23 << 11 & 0xFFFFFFFL);
        n2 += n23 >> 17;
        final long n24 = n + 134217728L >> 28;
        n -= n24 << 28;
        n2 += n24;
        final long n25 = n2 + 134217728L >> 28;
        n2 -= n25 << 28;
        n3 += n25;
        final long n26 = n3 + 134217728L >> 28;
        n3 -= n26 << 28;
        n4 += n26;
        final long n27 = n4 + 134217728L >> 28;
        n4 -= n27 << 28;
        n5 += n27;
        final long n28 = n5 + 134217728L >> 28;
        n5 -= n28 << 28;
        n6 += n28;
        final long n29 = n6 + 134217728L >> 28;
        n6 -= n29 << 28;
        n7 += n29;
        final long n30 = n7 + 134217728L >> 28;
        n7 -= n30 << 28;
        n8 += n30;
        final long n31 = n8 + 134217728L >> 28;
        n8 -= n31 << 28;
        n9 += n31;
        final long n32 = n9 + 134217728L >> 28;
        n9 -= n32 << 28;
        n10 += n32;
        final long n33 = n10 + 134217728L >> 28;
        n10 -= n33 << 28;
        n11 += n33;
        final long n34 = n11 + 134217728L >> 28;
        n11 -= n34 << 28;
        n12 += n34;
        final long n35 = n12 + 134217728L >> 28;
        n12 -= n35 << 28;
        n13 += n35;
        final long n36 = n13 + 134217728L >> 28;
        n13 -= n36 << 28;
        n14 += n36;
        final long n37 = n14 + 134217728L >> 28;
        n14 -= n37 << 28;
        n15 += n37;
        final long n38 = n15 + 134217728L >> 28;
        n15 -= n38 << 28;
        n16 += n38;
        final long n39 = n16 + 134217728L >> 28;
        n16 -= n39 << 28;
        n17 += n39;
        final long n40 = n17 + 134217728L >> 28;
        n17 -= n40 << 28;
        n18 += n40;
        final long n41 = n18 + 134217728L >> 28;
        n18 -= n41 << 28;
        n19 += n41;
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
        array[14] = n15;
        array[15] = n16;
        array[16] = n17;
        array[17] = n18;
        array[18] = n19;
    }
    
    @Override
    protected void mult(final long[] array, final long[] array2, final long[] array3) {
        this.carryReduce(array3, array[0] * array2[0], array[0] * array2[1] + array[1] * array2[0], array[0] * array2[2] + array[1] * array2[1] + array[2] * array2[0], array[0] * array2[3] + array[1] * array2[2] + array[2] * array2[1] + array[3] * array2[0], array[0] * array2[4] + array[1] * array2[3] + array[2] * array2[2] + array[3] * array2[1] + array[4] * array2[0], array[0] * array2[5] + array[1] * array2[4] + array[2] * array2[3] + array[3] * array2[2] + array[4] * array2[1] + array[5] * array2[0], array[0] * array2[6] + array[1] * array2[5] + array[2] * array2[4] + array[3] * array2[3] + array[4] * array2[2] + array[5] * array2[1] + array[6] * array2[0], array[0] * array2[7] + array[1] * array2[6] + array[2] * array2[5] + array[3] * array2[4] + array[4] * array2[3] + array[5] * array2[2] + array[6] * array2[1] + array[7] * array2[0], array[0] * array2[8] + array[1] * array2[7] + array[2] * array2[6] + array[3] * array2[5] + array[4] * array2[4] + array[5] * array2[3] + array[6] * array2[2] + array[7] * array2[1] + array[8] * array2[0], array[0] * array2[9] + array[1] * array2[8] + array[2] * array2[7] + array[3] * array2[6] + array[4] * array2[5] + array[5] * array2[4] + array[6] * array2[3] + array[7] * array2[2] + array[8] * array2[1] + array[9] * array2[0], array[0] * array2[10] + array[1] * array2[9] + array[2] * array2[8] + array[3] * array2[7] + array[4] * array2[6] + array[5] * array2[5] + array[6] * array2[4] + array[7] * array2[3] + array[8] * array2[2] + array[9] * array2[1] + array[10] * array2[0], array[0] * array2[11] + array[1] * array2[10] + array[2] * array2[9] + array[3] * array2[8] + array[4] * array2[7] + array[5] * array2[6] + array[6] * array2[5] + array[7] * array2[4] + array[8] * array2[3] + array[9] * array2[2] + array[10] * array2[1] + array[11] * array2[0], array[0] * array2[12] + array[1] * array2[11] + array[2] * array2[10] + array[3] * array2[9] + array[4] * array2[8] + array[5] * array2[7] + array[6] * array2[6] + array[7] * array2[5] + array[8] * array2[4] + array[9] * array2[3] + array[10] * array2[2] + array[11] * array2[1] + array[12] * array2[0], array[0] * array2[13] + array[1] * array2[12] + array[2] * array2[11] + array[3] * array2[10] + array[4] * array2[9] + array[5] * array2[8] + array[6] * array2[7] + array[7] * array2[6] + array[8] * array2[5] + array[9] * array2[4] + array[10] * array2[3] + array[11] * array2[2] + array[12] * array2[1] + array[13] * array2[0], array[0] * array2[14] + array[1] * array2[13] + array[2] * array2[12] + array[3] * array2[11] + array[4] * array2[10] + array[5] * array2[9] + array[6] * array2[8] + array[7] * array2[7] + array[8] * array2[6] + array[9] * array2[5] + array[10] * array2[4] + array[11] * array2[3] + array[12] * array2[2] + array[13] * array2[1] + array[14] * array2[0], array[0] * array2[15] + array[1] * array2[14] + array[2] * array2[13] + array[3] * array2[12] + array[4] * array2[11] + array[5] * array2[10] + array[6] * array2[9] + array[7] * array2[8] + array[8] * array2[7] + array[9] * array2[6] + array[10] * array2[5] + array[11] * array2[4] + array[12] * array2[3] + array[13] * array2[2] + array[14] * array2[1] + array[15] * array2[0], array[0] * array2[16] + array[1] * array2[15] + array[2] * array2[14] + array[3] * array2[13] + array[4] * array2[12] + array[5] * array2[11] + array[6] * array2[10] + array[7] * array2[9] + array[8] * array2[8] + array[9] * array2[7] + array[10] * array2[6] + array[11] * array2[5] + array[12] * array2[4] + array[13] * array2[3] + array[14] * array2[2] + array[15] * array2[1] + array[16] * array2[0], array[0] * array2[17] + array[1] * array2[16] + array[2] * array2[15] + array[3] * array2[14] + array[4] * array2[13] + array[5] * array2[12] + array[6] * array2[11] + array[7] * array2[10] + array[8] * array2[9] + array[9] * array2[8] + array[10] * array2[7] + array[11] * array2[6] + array[12] * array2[5] + array[13] * array2[4] + array[14] * array2[3] + array[15] * array2[2] + array[16] * array2[1] + array[17] * array2[0], array[0] * array2[18] + array[1] * array2[17] + array[2] * array2[16] + array[3] * array2[15] + array[4] * array2[14] + array[5] * array2[13] + array[6] * array2[12] + array[7] * array2[11] + array[8] * array2[10] + array[9] * array2[9] + array[10] * array2[8] + array[11] * array2[7] + array[12] * array2[6] + array[13] * array2[5] + array[14] * array2[4] + array[15] * array2[3] + array[16] * array2[2] + array[17] * array2[1] + array[18] * array2[0], array[1] * array2[18] + array[2] * array2[17] + array[3] * array2[16] + array[4] * array2[15] + array[5] * array2[14] + array[6] * array2[13] + array[7] * array2[12] + array[8] * array2[11] + array[9] * array2[10] + array[10] * array2[9] + array[11] * array2[8] + array[12] * array2[7] + array[13] * array2[6] + array[14] * array2[5] + array[15] * array2[4] + array[16] * array2[3] + array[17] * array2[2] + array[18] * array2[1], array[2] * array2[18] + array[3] * array2[17] + array[4] * array2[16] + array[5] * array2[15] + array[6] * array2[14] + array[7] * array2[13] + array[8] * array2[12] + array[9] * array2[11] + array[10] * array2[10] + array[11] * array2[9] + array[12] * array2[8] + array[13] * array2[7] + array[14] * array2[6] + array[15] * array2[5] + array[16] * array2[4] + array[17] * array2[3] + array[18] * array2[2], array[3] * array2[18] + array[4] * array2[17] + array[5] * array2[16] + array[6] * array2[15] + array[7] * array2[14] + array[8] * array2[13] + array[9] * array2[12] + array[10] * array2[11] + array[11] * array2[10] + array[12] * array2[9] + array[13] * array2[8] + array[14] * array2[7] + array[15] * array2[6] + array[16] * array2[5] + array[17] * array2[4] + array[18] * array2[3], array[4] * array2[18] + array[5] * array2[17] + array[6] * array2[16] + array[7] * array2[15] + array[8] * array2[14] + array[9] * array2[13] + array[10] * array2[12] + array[11] * array2[11] + array[12] * array2[10] + array[13] * array2[9] + array[14] * array2[8] + array[15] * array2[7] + array[16] * array2[6] + array[17] * array2[5] + array[18] * array2[4], array[5] * array2[18] + array[6] * array2[17] + array[7] * array2[16] + array[8] * array2[15] + array[9] * array2[14] + array[10] * array2[13] + array[11] * array2[12] + array[12] * array2[11] + array[13] * array2[10] + array[14] * array2[9] + array[15] * array2[8] + array[16] * array2[7] + array[17] * array2[6] + array[18] * array2[5], array[6] * array2[18] + array[7] * array2[17] + array[8] * array2[16] + array[9] * array2[15] + array[10] * array2[14] + array[11] * array2[13] + array[12] * array2[12] + array[13] * array2[11] + array[14] * array2[10] + array[15] * array2[9] + array[16] * array2[8] + array[17] * array2[7] + array[18] * array2[6], array[7] * array2[18] + array[8] * array2[17] + array[9] * array2[16] + array[10] * array2[15] + array[11] * array2[14] + array[12] * array2[13] + array[13] * array2[12] + array[14] * array2[11] + array[15] * array2[10] + array[16] * array2[9] + array[17] * array2[8] + array[18] * array2[7], array[8] * array2[18] + array[9] * array2[17] + array[10] * array2[16] + array[11] * array2[15] + array[12] * array2[14] + array[13] * array2[13] + array[14] * array2[12] + array[15] * array2[11] + array[16] * array2[10] + array[17] * array2[9] + array[18] * array2[8], array[9] * array2[18] + array[10] * array2[17] + array[11] * array2[16] + array[12] * array2[15] + array[13] * array2[14] + array[14] * array2[13] + array[15] * array2[12] + array[16] * array2[11] + array[17] * array2[10] + array[18] * array2[9], array[10] * array2[18] + array[11] * array2[17] + array[12] * array2[16] + array[13] * array2[15] + array[14] * array2[14] + array[15] * array2[13] + array[16] * array2[12] + array[17] * array2[11] + array[18] * array2[10], array[11] * array2[18] + array[12] * array2[17] + array[13] * array2[16] + array[14] * array2[15] + array[15] * array2[14] + array[16] * array2[13] + array[17] * array2[12] + array[18] * array2[11], array[12] * array2[18] + array[13] * array2[17] + array[14] * array2[16] + array[15] * array2[15] + array[16] * array2[14] + array[17] * array2[13] + array[18] * array2[12], array[13] * array2[18] + array[14] * array2[17] + array[15] * array2[16] + array[16] * array2[15] + array[17] * array2[14] + array[18] * array2[13], array[14] * array2[18] + array[15] * array2[17] + array[16] * array2[16] + array[17] * array2[15] + array[18] * array2[14], array[15] * array2[18] + array[16] * array2[17] + array[17] * array2[16] + array[18] * array2[15], array[16] * array2[18] + array[17] * array2[17] + array[18] * array2[16], array[17] * array2[18] + array[18] * array2[17], array[18] * array2[18]);
    }
    
    @Override
    protected void reduce(final long[] array) {
        this.carryReduce(array, array[0], array[1], array[2], array[3], array[4], array[5], array[6], array[7], array[8], array[9], array[10], array[11], array[12], array[13], array[14], array[15], array[16], array[17], array[18]);
    }
    
    @Override
    protected void square(final long[] array, final long[] array2) {
        this.carryReduce(array2, array[0] * array[0], 2L * (array[0] * array[1]), 2L * (array[0] * array[2]) + array[1] * array[1], 2L * (array[0] * array[3] + array[1] * array[2]), 2L * (array[0] * array[4] + array[1] * array[3]) + array[2] * array[2], 2L * (array[0] * array[5] + array[1] * array[4] + array[2] * array[3]), 2L * (array[0] * array[6] + array[1] * array[5] + array[2] * array[4]) + array[3] * array[3], 2L * (array[0] * array[7] + array[1] * array[6] + array[2] * array[5] + array[3] * array[4]), 2L * (array[0] * array[8] + array[1] * array[7] + array[2] * array[6] + array[3] * array[5]) + array[4] * array[4], 2L * (array[0] * array[9] + array[1] * array[8] + array[2] * array[7] + array[3] * array[6] + array[4] * array[5]), 2L * (array[0] * array[10] + array[1] * array[9] + array[2] * array[8] + array[3] * array[7] + array[4] * array[6]) + array[5] * array[5], 2L * (array[0] * array[11] + array[1] * array[10] + array[2] * array[9] + array[3] * array[8] + array[4] * array[7] + array[5] * array[6]), 2L * (array[0] * array[12] + array[1] * array[11] + array[2] * array[10] + array[3] * array[9] + array[4] * array[8] + array[5] * array[7]) + array[6] * array[6], 2L * (array[0] * array[13] + array[1] * array[12] + array[2] * array[11] + array[3] * array[10] + array[4] * array[9] + array[5] * array[8] + array[6] * array[7]), 2L * (array[0] * array[14] + array[1] * array[13] + array[2] * array[12] + array[3] * array[11] + array[4] * array[10] + array[5] * array[9] + array[6] * array[8]) + array[7] * array[7], 2L * (array[0] * array[15] + array[1] * array[14] + array[2] * array[13] + array[3] * array[12] + array[4] * array[11] + array[5] * array[10] + array[6] * array[9] + array[7] * array[8]), 2L * (array[0] * array[16] + array[1] * array[15] + array[2] * array[14] + array[3] * array[13] + array[4] * array[12] + array[5] * array[11] + array[6] * array[10] + array[7] * array[9]) + array[8] * array[8], 2L * (array[0] * array[17] + array[1] * array[16] + array[2] * array[15] + array[3] * array[14] + array[4] * array[13] + array[5] * array[12] + array[6] * array[11] + array[7] * array[10] + array[8] * array[9]), 2L * (array[0] * array[18] + array[1] * array[17] + array[2] * array[16] + array[3] * array[15] + array[4] * array[14] + array[5] * array[13] + array[6] * array[12] + array[7] * array[11] + array[8] * array[10]) + array[9] * array[9], 2L * (array[1] * array[18] + array[2] * array[17] + array[3] * array[16] + array[4] * array[15] + array[5] * array[14] + array[6] * array[13] + array[7] * array[12] + array[8] * array[11] + array[9] * array[10]), 2L * (array[2] * array[18] + array[3] * array[17] + array[4] * array[16] + array[5] * array[15] + array[6] * array[14] + array[7] * array[13] + array[8] * array[12] + array[9] * array[11]) + array[10] * array[10], 2L * (array[3] * array[18] + array[4] * array[17] + array[5] * array[16] + array[6] * array[15] + array[7] * array[14] + array[8] * array[13] + array[9] * array[12] + array[10] * array[11]), 2L * (array[4] * array[18] + array[5] * array[17] + array[6] * array[16] + array[7] * array[15] + array[8] * array[14] + array[9] * array[13] + array[10] * array[12]) + array[11] * array[11], 2L * (array[5] * array[18] + array[6] * array[17] + array[7] * array[16] + array[8] * array[15] + array[9] * array[14] + array[10] * array[13] + array[11] * array[12]), 2L * (array[6] * array[18] + array[7] * array[17] + array[8] * array[16] + array[9] * array[15] + array[10] * array[14] + array[11] * array[13]) + array[12] * array[12], 2L * (array[7] * array[18] + array[8] * array[17] + array[9] * array[16] + array[10] * array[15] + array[11] * array[14] + array[12] * array[13]), 2L * (array[8] * array[18] + array[9] * array[17] + array[10] * array[16] + array[11] * array[15] + array[12] * array[14]) + array[13] * array[13], 2L * (array[9] * array[18] + array[10] * array[17] + array[11] * array[16] + array[12] * array[15] + array[13] * array[14]), 2L * (array[10] * array[18] + array[11] * array[17] + array[12] * array[16] + array[13] * array[15]) + array[14] * array[14], 2L * (array[11] * array[18] + array[12] * array[17] + array[13] * array[16] + array[14] * array[15]), 2L * (array[12] * array[18] + array[13] * array[17] + array[14] * array[16]) + array[15] * array[15], 2L * (array[13] * array[18] + array[14] * array[17] + array[15] * array[16]), 2L * (array[14] * array[18] + array[15] * array[17]) + array[16] * array[16], 2L * (array[15] * array[18] + array[16] * array[17]), 2L * (array[16] * array[18]) + array[17] * array[17], 2L * (array[17] * array[18]), array[18] * array[18]);
    }
    
    static {
        MODULUS = evaluateModulus();
    }
}
