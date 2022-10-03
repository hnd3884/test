package org.bouncycastle.math.ec.rfc7748;

public abstract class X25519Field
{
    public static final int SIZE = 10;
    private static final int M24 = 16777215;
    private static final int M25 = 33554431;
    private static final int M26 = 67108863;
    
    private X25519Field() {
    }
    
    public static void add(final int[] array, final int[] array2, final int[] array3) {
        for (int i = 0; i < 10; ++i) {
            array3[i] = array[i] + array2[i];
        }
    }
    
    public static void apm(final int[] array, final int[] array2, final int[] array3, final int[] array4) {
        for (int i = 0; i < 10; ++i) {
            final int n = array[i];
            final int n2 = array2[i];
            array3[i] = n + n2;
            array4[i] = n - n2;
        }
    }
    
    public static void carry(final int[] array) {
        final int n = array[0];
        final int n2 = array[1];
        final int n3 = array[2];
        final int n4 = array[3];
        final int n5 = array[4];
        final int n6 = array[5];
        final int n7 = array[6];
        final int n8 = array[7];
        final int n9 = array[8];
        final int n10 = array[9];
        final int n11 = n4 + (n3 >> 25);
        final int n12 = n3 & 0x1FFFFFF;
        final int n13 = n6 + (n5 >> 25);
        final int n14 = n5 & 0x1FFFFFF;
        final int n15 = n9 + (n8 >> 25);
        final int n16 = n8 & 0x1FFFFFF;
        final int n17 = n + (n10 >> 25) * 38;
        final int n18 = n10 & 0x1FFFFFF;
        final int n19 = n2 + (n17 >> 26);
        final int n20 = n17 & 0x3FFFFFF;
        final int n21 = n7 + (n13 >> 26);
        final int n22 = n13 & 0x3FFFFFF;
        final int n23 = n12 + (n19 >> 26);
        final int n24 = n19 & 0x3FFFFFF;
        final int n25 = n14 + (n11 >> 26);
        final int n26 = n11 & 0x3FFFFFF;
        final int n27 = n16 + (n21 >> 26);
        final int n28 = n21 & 0x3FFFFFF;
        final int n29 = n18 + (n15 >> 26);
        final int n30 = n15 & 0x3FFFFFF;
        array[0] = n20;
        array[1] = n24;
        array[2] = n23;
        array[3] = n26;
        array[4] = n25;
        array[5] = n22;
        array[6] = n28;
        array[7] = n27;
        array[8] = n30;
        array[9] = n29;
    }
    
    public static void copy(final int[] array, final int n, final int[] array2, final int n2) {
        for (int i = 0; i < 10; ++i) {
            array2[n2 + i] = array[n + i];
        }
    }
    
    public static int[] create() {
        return new int[10];
    }
    
    public static void cswap(final int n, final int[] array, final int[] array2) {
        final int n2 = 0 - n;
        for (int i = 0; i < 10; ++i) {
            final int n3 = array[i];
            final int n4 = array2[i];
            final int n5 = n2 & (n3 ^ n4);
            array[i] = (n3 ^ n5);
            array2[i] = (n4 ^ n5);
        }
    }
    
    public static void decode(final byte[] array, final int n, final int[] array2) {
        decode128(array, n, array2, 0);
        decode128(array, n + 16, array2, 5);
        final int n2 = 9;
        array2[n2] &= 0xFFFFFF;
    }
    
    private static void decode128(final byte[] array, final int n, final int[] array2, final int n2) {
        final int decode32 = decode32(array, n + 0);
        final int decode33 = decode32(array, n + 4);
        final int decode34 = decode32(array, n + 8);
        final int decode35 = decode32(array, n + 12);
        array2[n2 + 0] = (decode32 & 0x3FFFFFF);
        array2[n2 + 1] = ((decode33 << 6 | decode32 >>> 26) & 0x3FFFFFF);
        array2[n2 + 2] = ((decode34 << 12 | decode33 >>> 20) & 0x1FFFFFF);
        array2[n2 + 3] = ((decode35 << 19 | decode34 >>> 13) & 0x3FFFFFF);
        array2[n2 + 4] = decode35 >>> 7;
    }
    
    private static int decode32(final byte[] array, int n) {
        return (array[n] & 0xFF) | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF) << 16 | array[++n] << 24;
    }
    
    public static void encode(final int[] array, final byte[] array2, final int n) {
        encode128(array, 0, array2, n);
        encode128(array, 5, array2, n + 16);
    }
    
    private static void encode128(final int[] array, final int n, final byte[] array2, final int n2) {
        final int n3 = array[n + 0];
        final int n4 = array[n + 1];
        final int n5 = array[n + 2];
        final int n6 = array[n + 3];
        final int n7 = array[n + 4];
        encode32(n3 | n4 << 26, array2, n2 + 0);
        encode32(n4 >>> 6 | n5 << 20, array2, n2 + 4);
        encode32(n5 >>> 12 | n6 << 13, array2, n2 + 8);
        encode32(n6 >>> 19 | n7 << 7, array2, n2 + 12);
    }
    
    private static void encode32(final int n, final byte[] array, int n2) {
        array[n2] = (byte)n;
        array[++n2] = (byte)(n >>> 8);
        array[++n2] = (byte)(n >>> 16);
        array[++n2] = (byte)(n >>> 24);
    }
    
    public static void inv(final int[] array, final int[] array2) {
        final int[] create = create();
        sqr(array, create);
        mul(array, create, create);
        final int[] create2 = create();
        sqr(create, create2);
        mul(array, create2, create2);
        final int[] array3 = create2;
        sqr(create2, 2, array3);
        mul(create, array3, array3);
        final int[] create3 = create();
        sqr(array3, 5, create3);
        mul(array3, create3, create3);
        final int[] create4 = create();
        sqr(create3, 5, create4);
        mul(array3, create4, create4);
        final int[] array4 = array3;
        sqr(create4, 10, array4);
        mul(create3, array4, array4);
        final int[] array5 = create3;
        sqr(array4, 25, array5);
        mul(array4, array5, array5);
        final int[] array6 = create4;
        sqr(array5, 25, array6);
        mul(array4, array6, array6);
        final int[] array7 = array4;
        sqr(array6, 50, array7);
        mul(array5, array7, array7);
        final int[] array8 = array5;
        sqr(array7, 125, array8);
        mul(array7, array8, array8);
        final int[] array9 = array7;
        sqr(array8, 2, array9);
        mul(array9, array, array9);
        sqr(array9, 3, array9);
        mul(array9, create, array2);
    }
    
    public static void mul(final int[] array, final int n, final int[] array2) {
        final int n2 = array[0];
        final int n3 = array[1];
        final int n4 = array[2];
        final int n5 = array[3];
        final int n6 = array[4];
        final int n7 = array[5];
        final int n8 = array[6];
        final int n9 = array[7];
        final int n10 = array[8];
        final int n11 = array[9];
        final long n12 = n4 * (long)n;
        final int n13 = (int)n12 & 0x1FFFFFF;
        final long n14 = n12 >> 25;
        final long n15 = n6 * (long)n;
        final int n16 = (int)n15 & 0x1FFFFFF;
        final long n17 = n15 >> 25;
        final long n18 = n9 * (long)n;
        final int n19 = (int)n18 & 0x1FFFFFF;
        final long n20 = n18 >> 25;
        final long n21 = n11 * (long)n;
        final int n22 = (int)n21 & 0x1FFFFFF;
        final long n23 = (n21 >> 25) * 38L + n2 * (long)n;
        array2[0] = ((int)n23 & 0x3FFFFFF);
        final long n24 = n23 >> 26;
        final long n25 = n17 + n7 * (long)n;
        array2[5] = ((int)n25 & 0x3FFFFFF);
        final long n26 = n25 >> 26;
        final long n27 = n24 + n3 * (long)n;
        array2[1] = ((int)n27 & 0x3FFFFFF);
        final long n28 = n27 >> 26;
        final long n29 = n14 + n5 * (long)n;
        array2[3] = ((int)n29 & 0x3FFFFFF);
        final long n30 = n29 >> 26;
        final long n31 = n26 + n8 * (long)n;
        array2[6] = ((int)n31 & 0x3FFFFFF);
        final long n32 = n31 >> 26;
        final long n33 = n20 + n10 * (long)n;
        array2[8] = ((int)n33 & 0x3FFFFFF);
        final long n34 = n33 >> 26;
        array2[2] = n13 + (int)n28;
        array2[4] = n16 + (int)n30;
        array2[7] = n19 + (int)n32;
        array2[9] = n22 + (int)n34;
    }
    
    public static void mul(final int[] array, final int[] array2, final int[] array3) {
        final int n = array[0];
        final int n2 = array2[0];
        final int n3 = array[1];
        final int n4 = array2[1];
        final int n5 = array[2];
        final int n6 = array2[2];
        final int n7 = array[3];
        final int n8 = array2[3];
        final int n9 = array[4];
        final int n10 = array2[4];
        final int n11 = array[5];
        final int n12 = array2[5];
        final int n13 = array[6];
        final int n14 = array2[6];
        final int n15 = array[7];
        final int n16 = array2[7];
        final int n17 = array[8];
        final int n18 = array2[8];
        final int n19 = array[9];
        final int n20 = array2[9];
        final long n21 = n * (long)n2;
        final long n22 = n * (long)n4 + n3 * (long)n2;
        final long n23 = n * (long)n6 + n3 * (long)n4 + n5 * (long)n2;
        final long n24 = (n3 * (long)n6 + n5 * (long)n4 << 1) + (n * (long)n8 + n7 * (long)n2);
        final long n25 = (n5 * (long)n6 << 1) + (n * (long)n10 + n3 * (long)n8 + n7 * (long)n4 + n9 * (long)n2);
        final long n26 = n3 * (long)n10 + n5 * (long)n8 + n7 * (long)n6 + n9 * (long)n4 << 1;
        final long n27 = (n5 * (long)n10 + n9 * (long)n6 << 1) + n7 * (long)n8;
        final long n28 = n7 * (long)n10 + n9 * (long)n8;
        final long n29 = n9 * (long)n10 << 1;
        final long n30 = n11 * (long)n12;
        final long n31 = n11 * (long)n14 + n13 * (long)n12;
        final long n32 = n11 * (long)n16 + n13 * (long)n14 + n15 * (long)n12;
        final long n33 = (n13 * (long)n16 + n15 * (long)n14 << 1) + (n11 * (long)n18 + n17 * (long)n12);
        final long n34 = (n15 * (long)n16 << 1) + (n11 * (long)n20 + n13 * (long)n18 + n17 * (long)n14 + n19 * (long)n12);
        final long n35 = n13 * (long)n20 + n15 * (long)n18 + n17 * (long)n16 + n19 * (long)n14;
        final long n36 = (n15 * (long)n20 + n19 * (long)n16 << 1) + n17 * (long)n18;
        final long n37 = n17 * (long)n20 + n19 * (long)n18;
        final long n38 = n19 * (long)n20;
        final long n39 = n21 - n35 * 76L;
        final long n40 = n22 - n36 * 38L;
        final long n41 = n23 - n37 * 38L;
        final long n42 = n24 - n38 * 76L;
        final long n43 = n26 - n30;
        final long n44 = n27 - n31;
        final long n45 = n28 - n32;
        final long n46 = n29 - n33;
        final int n47 = n + n11;
        final int n48 = n2 + n12;
        final int n49 = n3 + n13;
        final int n50 = n4 + n14;
        final int n51 = n5 + n15;
        final int n52 = n6 + n16;
        final int n53 = n7 + n17;
        final int n54 = n8 + n18;
        final int n55 = n9 + n19;
        final int n56 = n10 + n20;
        final long n57 = n47 * (long)n48;
        final long n58 = n47 * (long)n50 + n49 * (long)n48;
        final long n59 = n47 * (long)n52 + n49 * (long)n50 + n51 * (long)n48;
        final long n60 = (n49 * (long)n52 + n51 * (long)n50 << 1) + (n47 * (long)n54 + n53 * (long)n48);
        final long n61 = (n51 * (long)n52 << 1) + (n47 * (long)n56 + n49 * (long)n54 + n53 * (long)n50 + n55 * (long)n48);
        final long n62 = n49 * (long)n56 + n51 * (long)n54 + n53 * (long)n52 + n55 * (long)n50 << 1;
        final long n63 = (n51 * (long)n56 + n55 * (long)n52 << 1) + n53 * (long)n54;
        final long n64 = n53 * (long)n56 + n55 * (long)n54;
        final long n65 = n55 * (long)n56 << 1;
        final long n66 = n46 + (n60 - n42);
        final int n67 = (int)n66 & 0x3FFFFFF;
        final long n68 = (n66 >> 26) + (n61 - n25 - n34);
        final int n69 = (int)n68 & 0x1FFFFFF;
        final long n70 = n39 + ((n68 >> 25) + n62 - n43) * 38L;
        array3[0] = ((int)n70 & 0x3FFFFFF);
        final long n71 = (n70 >> 26) + (n40 + (n63 - n44) * 38L);
        array3[1] = ((int)n71 & 0x3FFFFFF);
        final long n72 = (n71 >> 26) + (n41 + (n64 - n45) * 38L);
        array3[2] = ((int)n72 & 0x1FFFFFF);
        final long n73 = (n72 >> 25) + (n42 + (n65 - n46) * 38L);
        array3[3] = ((int)n73 & 0x3FFFFFF);
        final long n74 = (n73 >> 26) + (n25 + n34 * 38L);
        array3[4] = ((int)n74 & 0x1FFFFFF);
        final long n75 = (n74 >> 25) + (n43 + (n57 - n39));
        array3[5] = ((int)n75 & 0x3FFFFFF);
        final long n76 = (n75 >> 26) + (n44 + (n58 - n40));
        array3[6] = ((int)n76 & 0x3FFFFFF);
        final long n77 = (n76 >> 26) + (n45 + (n59 - n41));
        array3[7] = ((int)n77 & 0x1FFFFFF);
        final long n78 = (n77 >> 25) + n67;
        array3[8] = ((int)n78 & 0x3FFFFFF);
        array3[9] = n69 + (int)(n78 >> 26);
    }
    
    public static void normalize(final int[] array) {
        final int n = array[9] >>> 23 & 0x1;
        reduce(array, n);
        reduce(array, -n);
    }
    
    private static void reduce(final int[] array, final int n) {
        final int n2 = array[9];
        final int n3 = n2 & 0xFFFFFF;
        final int n4 = ((n2 >> 24) + n) * 19 + array[0];
        array[0] = (n4 & 0x3FFFFFF);
        final int n5 = (n4 >> 26) + array[1];
        array[1] = (n5 & 0x3FFFFFF);
        final int n6 = (n5 >> 26) + array[2];
        array[2] = (n6 & 0x1FFFFFF);
        final int n7 = (n6 >> 25) + array[3];
        array[3] = (n7 & 0x3FFFFFF);
        final int n8 = (n7 >> 26) + array[4];
        array[4] = (n8 & 0x1FFFFFF);
        final int n9 = (n8 >> 25) + array[5];
        array[5] = (n9 & 0x3FFFFFF);
        final int n10 = (n9 >> 26) + array[6];
        array[6] = (n10 & 0x3FFFFFF);
        final int n11 = (n10 >> 26) + array[7];
        array[7] = (n11 & 0x1FFFFFF);
        final int n12 = (n11 >> 25) + array[8];
        array[8] = (n12 & 0x3FFFFFF);
        array[9] = (n12 >> 26) + n3;
    }
    
    public static void sqr(final int[] array, final int[] array2) {
        final int n = array[0];
        final int n2 = array[1];
        final int n3 = array[2];
        final int n4 = array[3];
        final int n5 = array[4];
        final int n6 = array[5];
        final int n7 = array[6];
        final int n8 = array[7];
        final int n9 = array[8];
        final int n10 = array[9];
        final int n11 = n2 * 2;
        final int n12 = n3 * 2;
        final int n13 = n4 * 2;
        final int n14 = n5 * 2;
        final long n15 = n * (long)n;
        final long n16 = n * (long)n11;
        final long n17 = n * (long)n12 + n2 * (long)n2;
        final long n18 = n11 * (long)n12 + n * (long)n13;
        final long n19 = n3 * (long)n12 + n * (long)n14 + n2 * (long)n13;
        final long n20 = n11 * (long)n14 + n12 * (long)n13;
        final long n21 = n12 * (long)n14 + n4 * (long)n4;
        final long n22 = n4 * (long)n14;
        final long n23 = n5 * (long)n14;
        final int n24 = n7 * 2;
        final int n25 = n8 * 2;
        final int n26 = n9 * 2;
        final int n27 = n10 * 2;
        final long n28 = n6 * (long)n6;
        final long n29 = n6 * (long)n24;
        final long n30 = n6 * (long)n25 + n7 * (long)n7;
        final long n31 = n24 * (long)n25 + n6 * (long)n26;
        final long n32 = n8 * (long)n25 + n6 * (long)n27 + n7 * (long)n26;
        final long n33 = n24 * (long)n27 + n25 * (long)n26;
        final long n34 = n25 * (long)n27 + n9 * (long)n9;
        final long n35 = n9 * (long)n27;
        final long n36 = n10 * (long)n27;
        final long n37 = n15 - n33 * 38L;
        final long n38 = n16 - n34 * 38L;
        final long n39 = n17 - n35 * 38L;
        final long n40 = n18 - n36 * 38L;
        final long n41 = n20 - n28;
        final long n42 = n21 - n29;
        final long n43 = n22 - n30;
        final long n44 = n23 - n31;
        final int n45 = n + n6;
        final int n46 = n2 + n7;
        final int n47 = n3 + n8;
        final int n48 = n4 + n9;
        final int n49 = n5 + n10;
        final int n50 = n46 * 2;
        final int n51 = n47 * 2;
        final int n52 = n48 * 2;
        final int n53 = n49 * 2;
        final long n54 = n45 * (long)n45;
        final long n55 = n45 * (long)n50;
        final long n56 = n45 * (long)n51 + n46 * (long)n46;
        final long n57 = n50 * (long)n51 + n45 * (long)n52;
        final long n58 = n47 * (long)n51 + n45 * (long)n53 + n46 * (long)n52;
        final long n59 = n50 * (long)n53 + n51 * (long)n52;
        final long n60 = n51 * (long)n53 + n48 * (long)n48;
        final long n61 = n48 * (long)n53;
        final long n62 = n49 * (long)n53;
        final long n63 = n44 + (n57 - n40);
        final int n64 = (int)n63 & 0x3FFFFFF;
        final long n65 = (n63 >> 26) + (n58 - n19 - n32);
        final int n66 = (int)n65 & 0x1FFFFFF;
        final long n67 = n37 + ((n65 >> 25) + n59 - n41) * 38L;
        array2[0] = ((int)n67 & 0x3FFFFFF);
        final long n68 = (n67 >> 26) + (n38 + (n60 - n42) * 38L);
        array2[1] = ((int)n68 & 0x3FFFFFF);
        final long n69 = (n68 >> 26) + (n39 + (n61 - n43) * 38L);
        array2[2] = ((int)n69 & 0x1FFFFFF);
        final long n70 = (n69 >> 25) + (n40 + (n62 - n44) * 38L);
        array2[3] = ((int)n70 & 0x3FFFFFF);
        final long n71 = (n70 >> 26) + (n19 + n32 * 38L);
        array2[4] = ((int)n71 & 0x1FFFFFF);
        final long n72 = (n71 >> 25) + (n41 + (n54 - n37));
        array2[5] = ((int)n72 & 0x3FFFFFF);
        final long n73 = (n72 >> 26) + (n42 + (n55 - n38));
        array2[6] = ((int)n73 & 0x3FFFFFF);
        final long n74 = (n73 >> 26) + (n43 + (n56 - n39));
        array2[7] = ((int)n74 & 0x1FFFFFF);
        final long n75 = (n74 >> 25) + n64;
        array2[8] = ((int)n75 & 0x3FFFFFF);
        array2[9] = n66 + (int)(n75 >> 26);
    }
    
    public static void sqr(final int[] array, int n, final int[] array2) {
        sqr(array, array2);
        while (--n > 0) {
            sqr(array2, array2);
        }
    }
    
    public static void sub(final int[] array, final int[] array2, final int[] array3) {
        for (int i = 0; i < 10; ++i) {
            array3[i] = array[i] - array2[i];
        }
    }
}
