package org.bouncycastle.math.ec.rfc7748;

public abstract class X448Field
{
    public static final int SIZE = 16;
    private static final int M28 = 268435455;
    
    private X448Field() {
    }
    
    public static void add(final int[] array, final int[] array2, final int[] array3) {
        for (int i = 0; i < 16; ++i) {
            array3[i] = array[i] + array2[i];
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
        final int n11 = array[10];
        final int n12 = array[11];
        final int n13 = array[12];
        final int n14 = array[13];
        final int n15 = array[14];
        final int n16 = array[15];
        final int n17 = n3 + (n2 >>> 28);
        final int n18 = n2 & 0xFFFFFFF;
        final int n19 = n7 + (n6 >>> 28);
        final int n20 = n6 & 0xFFFFFFF;
        final int n21 = n11 + (n10 >>> 28);
        final int n22 = n10 & 0xFFFFFFF;
        final int n23 = n15 + (n14 >>> 28);
        final int n24 = n14 & 0xFFFFFFF;
        final int n25 = n4 + (n17 >>> 28);
        final int n26 = n17 & 0xFFFFFFF;
        final int n27 = n8 + (n19 >>> 28);
        final int n28 = n19 & 0xFFFFFFF;
        final int n29 = n12 + (n21 >>> 28);
        final int n30 = n21 & 0xFFFFFFF;
        final int n31 = n16 + (n23 >>> 28);
        final int n32 = n23 & 0xFFFFFFF;
        final int n33 = n31 >>> 28;
        final int n34 = n31 & 0xFFFFFFF;
        final int n35 = n + n33;
        final int n36 = n9 + n33;
        final int n37 = n5 + (n25 >>> 28);
        final int n38 = n25 & 0xFFFFFFF;
        final int n39 = n36 + (n27 >>> 28);
        final int n40 = n27 & 0xFFFFFFF;
        final int n41 = n13 + (n29 >>> 28);
        final int n42 = n29 & 0xFFFFFFF;
        final int n43 = n18 + (n35 >>> 28);
        final int n44 = n35 & 0xFFFFFFF;
        final int n45 = n20 + (n37 >>> 28);
        final int n46 = n37 & 0xFFFFFFF;
        final int n47 = n22 + (n39 >>> 28);
        final int n48 = n39 & 0xFFFFFFF;
        final int n49 = n24 + (n41 >>> 28);
        final int n50 = n41 & 0xFFFFFFF;
        array[0] = n44;
        array[1] = n43;
        array[2] = n26;
        array[3] = n38;
        array[4] = n46;
        array[5] = n45;
        array[6] = n28;
        array[7] = n40;
        array[8] = n48;
        array[9] = n47;
        array[10] = n30;
        array[11] = n42;
        array[12] = n50;
        array[13] = n49;
        array[14] = n32;
        array[15] = n34;
    }
    
    public static void copy(final int[] array, final int n, final int[] array2, final int n2) {
        for (int i = 0; i < 16; ++i) {
            array2[n2 + i] = array[n + i];
        }
    }
    
    public static int[] create() {
        return new int[16];
    }
    
    public static void cswap(final int n, final int[] array, final int[] array2) {
        final int n2 = 0 - n;
        for (int i = 0; i < 16; ++i) {
            final int n3 = array[i];
            final int n4 = array2[i];
            final int n5 = n2 & (n3 ^ n4);
            array[i] = (n3 ^ n5);
            array2[i] = (n4 ^ n5);
        }
    }
    
    public static void decode(final byte[] array, final int n, final int[] array2) {
        decode56(array, n, array2, 0);
        decode56(array, n + 7, array2, 2);
        decode56(array, n + 14, array2, 4);
        decode56(array, n + 21, array2, 6);
        decode56(array, n + 28, array2, 8);
        decode56(array, n + 35, array2, 10);
        decode56(array, n + 42, array2, 12);
        decode56(array, n + 49, array2, 14);
    }
    
    private static int decode24(final byte[] array, int n) {
        return (array[n] & 0xFF) | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF) << 16;
    }
    
    private static int decode32(final byte[] array, int n) {
        return (array[n] & 0xFF) | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF) << 16 | array[++n] << 24;
    }
    
    private static void decode56(final byte[] array, final int n, final int[] array2, final int n2) {
        final int decode32 = decode32(array, n);
        final int decode33 = decode24(array, n + 4);
        array2[n2] = (decode32 & 0xFFFFFFF);
        array2[n2 + 1] = (decode32 >>> 28 | decode33 << 4);
    }
    
    public static void encode(final int[] array, final byte[] array2, final int n) {
        encode56(array, 0, array2, n);
        encode56(array, 2, array2, n + 7);
        encode56(array, 4, array2, n + 14);
        encode56(array, 6, array2, n + 21);
        encode56(array, 8, array2, n + 28);
        encode56(array, 10, array2, n + 35);
        encode56(array, 12, array2, n + 42);
        encode56(array, 14, array2, n + 49);
    }
    
    private static void encode24(final int n, final byte[] array, int n2) {
        array[n2] = (byte)n;
        array[++n2] = (byte)(n >>> 8);
        array[++n2] = (byte)(n >>> 16);
    }
    
    private static void encode32(final int n, final byte[] array, int n2) {
        array[n2] = (byte)n;
        array[++n2] = (byte)(n >>> 8);
        array[++n2] = (byte)(n >>> 16);
        array[++n2] = (byte)(n >>> 24);
    }
    
    private static void encode56(final int[] array, final int n, final byte[] array2, final int n2) {
        final int n3 = array[n];
        final int n4 = array[n + 1];
        encode32(n3 | n4 << 28, array2, n2);
        encode24(n4 >>> 4, array2, n2 + 4);
    }
    
    public static void inv(final int[] array, final int[] array2) {
        final int[] create = create();
        sqr(array, create);
        mul(array, create, create);
        final int[] create2 = create();
        sqr(create, create2);
        mul(array, create2, create2);
        final int[] create3 = create();
        sqr(create2, 3, create3);
        mul(create2, create3, create3);
        final int[] create4 = create();
        sqr(create3, 3, create4);
        mul(create2, create4, create4);
        final int[] create5 = create();
        sqr(create4, 9, create5);
        mul(create4, create5, create5);
        final int[] create6 = create();
        sqr(create5, create6);
        mul(array, create6, create6);
        final int[] create7 = create();
        sqr(create6, 18, create7);
        mul(create5, create7, create7);
        final int[] create8 = create();
        sqr(create7, 37, create8);
        mul(create7, create8, create8);
        final int[] create9 = create();
        sqr(create8, 37, create9);
        mul(create7, create9, create9);
        final int[] create10 = create();
        sqr(create9, 111, create10);
        mul(create9, create10, create10);
        final int[] create11 = create();
        sqr(create10, create11);
        mul(array, create11, create11);
        final int[] create12 = create();
        sqr(create11, 223, create12);
        mul(create12, create10, create12);
        sqr(create12, 2, create12);
        mul(create12, array, array2);
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
        final int n12 = array[10];
        final int n13 = array[11];
        final int n14 = array[12];
        final int n15 = array[13];
        final int n16 = array[14];
        final int n17 = array[15];
        final long n18 = n3 * (long)n;
        final int n19 = (int)n18 & 0xFFFFFFF;
        final long n20 = n18 >>> 28;
        final long n21 = n7 * (long)n;
        final int n22 = (int)n21 & 0xFFFFFFF;
        final long n23 = n21 >>> 28;
        final long n24 = n11 * (long)n;
        final int n25 = (int)n24 & 0xFFFFFFF;
        final long n26 = n24 >>> 28;
        final long n27 = n15 * (long)n;
        final int n28 = (int)n27 & 0xFFFFFFF;
        final long n29 = n27 >>> 28;
        final long n30 = n20 + n4 * (long)n;
        array2[2] = ((int)n30 & 0xFFFFFFF);
        final long n31 = n30 >>> 28;
        final long n32 = n23 + n8 * (long)n;
        array2[6] = ((int)n32 & 0xFFFFFFF);
        final long n33 = n32 >>> 28;
        final long n34 = n26 + n12 * (long)n;
        array2[10] = ((int)n34 & 0xFFFFFFF);
        final long n35 = n34 >>> 28;
        final long n36 = n29 + n16 * (long)n;
        array2[14] = ((int)n36 & 0xFFFFFFF);
        final long n37 = n36 >>> 28;
        final long n38 = n31 + n5 * (long)n;
        array2[3] = ((int)n38 & 0xFFFFFFF);
        final long n39 = n38 >>> 28;
        final long n40 = n33 + n9 * (long)n;
        array2[7] = ((int)n40 & 0xFFFFFFF);
        final long n41 = n40 >>> 28;
        final long n42 = n35 + n13 * (long)n;
        array2[11] = ((int)n42 & 0xFFFFFFF);
        final long n43 = n42 >>> 28;
        final long n44 = n37 + n17 * (long)n;
        array2[15] = ((int)n44 & 0xFFFFFFF);
        final long n45 = n44 >>> 28;
        final long n46 = n41 + n45;
        final long n47 = n39 + n6 * (long)n;
        array2[4] = ((int)n47 & 0xFFFFFFF);
        final long n48 = n47 >>> 28;
        final long n49 = n46 + n10 * (long)n;
        array2[8] = ((int)n49 & 0xFFFFFFF);
        final long n50 = n49 >>> 28;
        final long n51 = n43 + n14 * (long)n;
        array2[12] = ((int)n51 & 0xFFFFFFF);
        final long n52 = n51 >>> 28;
        final long n53 = n45 + n2 * (long)n;
        array2[0] = ((int)n53 & 0xFFFFFFF);
        array2[1] = n19 + (int)(n53 >>> 28);
        array2[5] = n22 + (int)n48;
        array2[9] = n25 + (int)n50;
        array2[13] = n28 + (int)n52;
    }
    
    public static void mul(final int[] array, final int[] array2, final int[] array3) {
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
        final int n11 = array[10];
        final int n12 = array[11];
        final int n13 = array[12];
        final int n14 = array[13];
        final int n15 = array[14];
        final int n16 = array[15];
        final int n17 = array2[0];
        final int n18 = array2[1];
        final int n19 = array2[2];
        final int n20 = array2[3];
        final int n21 = array2[4];
        final int n22 = array2[5];
        final int n23 = array2[6];
        final int n24 = array2[7];
        final int n25 = array2[8];
        final int n26 = array2[9];
        final int n27 = array2[10];
        final int n28 = array2[11];
        final int n29 = array2[12];
        final int n30 = array2[13];
        final int n31 = array2[14];
        final int n32 = array2[15];
        final int n33 = n + n9;
        final int n34 = n2 + n10;
        final int n35 = n3 + n11;
        final int n36 = n4 + n12;
        final int n37 = n5 + n13;
        final int n38 = n6 + n14;
        final int n39 = n7 + n15;
        final int n40 = n8 + n16;
        final int n41 = n17 + n25;
        final int n42 = n18 + n26;
        final int n43 = n19 + n27;
        final int n44 = n20 + n28;
        final int n45 = n21 + n29;
        final int n46 = n22 + n30;
        final int n47 = n23 + n31;
        final int n48 = n24 + n32;
        final long n49 = n * (long)n17;
        final long n50 = n8 * (long)n18 + n7 * (long)n19 + n6 * (long)n20 + n5 * (long)n21 + n4 * (long)n22 + n3 * (long)n23 + n2 * (long)n24;
        final long n51 = n9 * (long)n25;
        final long n52 = n16 * (long)n26 + n15 * (long)n27 + n14 * (long)n28 + n13 * (long)n29 + n12 * (long)n30 + n11 * (long)n31 + n10 * (long)n32;
        final long n53 = n33 * (long)n41;
        final long n54 = n40 * (long)n42 + n39 * (long)n43 + n38 * (long)n44 + n37 * (long)n45 + n36 * (long)n46 + n35 * (long)n47 + n34 * (long)n48;
        final long n55 = n49 + n51 + n54 - n50;
        final int n56 = (int)n55 & 0xFFFFFFF;
        final long n57 = n55 >>> 28;
        final long n58 = n52 + n53 - n49 + n54;
        final int n59 = (int)n58 & 0xFFFFFFF;
        final long n60 = n58 >>> 28;
        final long n61 = n2 * (long)n17 + n * (long)n18;
        final long n62 = n8 * (long)n19 + n7 * (long)n20 + n6 * (long)n21 + n5 * (long)n22 + n4 * (long)n23 + n3 * (long)n24;
        final long n63 = n10 * (long)n25 + n9 * (long)n26;
        final long n64 = n16 * (long)n27 + n15 * (long)n28 + n14 * (long)n29 + n13 * (long)n30 + n12 * (long)n31 + n11 * (long)n32;
        final long n65 = n34 * (long)n41 + n33 * (long)n42;
        final long n66 = n40 * (long)n43 + n39 * (long)n44 + n38 * (long)n45 + n37 * (long)n46 + n36 * (long)n47 + n35 * (long)n48;
        final long n67 = n57 + (n61 + n63 + n66 - n62);
        final int n68 = (int)n67 & 0xFFFFFFF;
        final long n69 = n67 >>> 28;
        final long n70 = n60 + (n64 + n65 - n61 + n66);
        final int n71 = (int)n70 & 0xFFFFFFF;
        final long n72 = n70 >>> 28;
        final long n73 = n3 * (long)n17 + n2 * (long)n18 + n * (long)n19;
        final long n74 = n8 * (long)n20 + n7 * (long)n21 + n6 * (long)n22 + n5 * (long)n23 + n4 * (long)n24;
        final long n75 = n11 * (long)n25 + n10 * (long)n26 + n9 * (long)n27;
        final long n76 = n16 * (long)n28 + n15 * (long)n29 + n14 * (long)n30 + n13 * (long)n31 + n12 * (long)n32;
        final long n77 = n35 * (long)n41 + n34 * (long)n42 + n33 * (long)n43;
        final long n78 = n40 * (long)n44 + n39 * (long)n45 + n38 * (long)n46 + n37 * (long)n47 + n36 * (long)n48;
        final long n79 = n69 + (n73 + n75 + n78 - n74);
        final int n80 = (int)n79 & 0xFFFFFFF;
        final long n81 = n79 >>> 28;
        final long n82 = n72 + (n76 + n77 - n73 + n78);
        final int n83 = (int)n82 & 0xFFFFFFF;
        final long n84 = n82 >>> 28;
        final long n85 = n4 * (long)n17 + n3 * (long)n18 + n2 * (long)n19 + n * (long)n20;
        final long n86 = n8 * (long)n21 + n7 * (long)n22 + n6 * (long)n23 + n5 * (long)n24;
        final long n87 = n12 * (long)n25 + n11 * (long)n26 + n10 * (long)n27 + n9 * (long)n28;
        final long n88 = n16 * (long)n29 + n15 * (long)n30 + n14 * (long)n31 + n13 * (long)n32;
        final long n89 = n36 * (long)n41 + n35 * (long)n42 + n34 * (long)n43 + n33 * (long)n44;
        final long n90 = n40 * (long)n45 + n39 * (long)n46 + n38 * (long)n47 + n37 * (long)n48;
        final long n91 = n81 + (n85 + n87 + n90 - n86);
        final int n92 = (int)n91 & 0xFFFFFFF;
        final long n93 = n91 >>> 28;
        final long n94 = n84 + (n88 + n89 - n85 + n90);
        final int n95 = (int)n94 & 0xFFFFFFF;
        final long n96 = n94 >>> 28;
        final long n97 = n5 * (long)n17 + n4 * (long)n18 + n3 * (long)n19 + n2 * (long)n20 + n * (long)n21;
        final long n98 = n8 * (long)n22 + n7 * (long)n23 + n6 * (long)n24;
        final long n99 = n13 * (long)n25 + n12 * (long)n26 + n11 * (long)n27 + n10 * (long)n28 + n9 * (long)n29;
        final long n100 = n16 * (long)n30 + n15 * (long)n31 + n14 * (long)n32;
        final long n101 = n37 * (long)n41 + n36 * (long)n42 + n35 * (long)n43 + n34 * (long)n44 + n33 * (long)n45;
        final long n102 = n40 * (long)n46 + n39 * (long)n47 + n38 * (long)n48;
        final long n103 = n93 + (n97 + n99 + n102 - n98);
        final int n104 = (int)n103 & 0xFFFFFFF;
        final long n105 = n103 >>> 28;
        final long n106 = n96 + (n100 + n101 - n97 + n102);
        final int n107 = (int)n106 & 0xFFFFFFF;
        final long n108 = n106 >>> 28;
        final long n109 = n6 * (long)n17 + n5 * (long)n18 + n4 * (long)n19 + n3 * (long)n20 + n2 * (long)n21 + n * (long)n22;
        final long n110 = n8 * (long)n23 + n7 * (long)n24;
        final long n111 = n14 * (long)n25 + n13 * (long)n26 + n12 * (long)n27 + n11 * (long)n28 + n10 * (long)n29 + n9 * (long)n30;
        final long n112 = n16 * (long)n31 + n15 * (long)n32;
        final long n113 = n38 * (long)n41 + n37 * (long)n42 + n36 * (long)n43 + n35 * (long)n44 + n34 * (long)n45 + n33 * (long)n46;
        final long n114 = n40 * (long)n47 + n39 * (long)n48;
        final long n115 = n105 + (n109 + n111 + n114 - n110);
        final int n116 = (int)n115 & 0xFFFFFFF;
        final long n117 = n115 >>> 28;
        final long n118 = n108 + (n112 + n113 - n109 + n114);
        final int n119 = (int)n118 & 0xFFFFFFF;
        final long n120 = n118 >>> 28;
        final long n121 = n7 * (long)n17 + n6 * (long)n18 + n5 * (long)n19 + n4 * (long)n20 + n3 * (long)n21 + n2 * (long)n22 + n * (long)n23;
        final long n122 = n8 * (long)n24;
        final long n123 = n15 * (long)n25 + n14 * (long)n26 + n13 * (long)n27 + n12 * (long)n28 + n11 * (long)n29 + n10 * (long)n30 + n9 * (long)n31;
        final long n124 = n16 * (long)n32;
        final long n125 = n39 * (long)n41 + n38 * (long)n42 + n37 * (long)n43 + n36 * (long)n44 + n35 * (long)n45 + n34 * (long)n46 + n33 * (long)n47;
        final long n126 = n40 * (long)n48;
        final long n127 = n117 + (n121 + n123 + n126 - n122);
        final int n128 = (int)n127 & 0xFFFFFFF;
        final long n129 = n127 >>> 28;
        final long n130 = n120 + (n124 + n125 - n121 + n126);
        final int n131 = (int)n130 & 0xFFFFFFF;
        final long n132 = n130 >>> 28;
        final long n133 = n8 * (long)n17 + n7 * (long)n18 + n6 * (long)n19 + n5 * (long)n20 + n4 * (long)n21 + n3 * (long)n22 + n2 * (long)n23 + n * (long)n24;
        final long n134 = n16 * (long)n25 + n15 * (long)n26 + n14 * (long)n27 + n13 * (long)n28 + n12 * (long)n29 + n11 * (long)n30 + n10 * (long)n31 + n9 * (long)n32;
        final long n135 = n40 * (long)n41 + n39 * (long)n42 + n38 * (long)n43 + n37 * (long)n44 + n36 * (long)n45 + n35 * (long)n46 + n34 * (long)n47 + n33 * (long)n48;
        final long n136 = n129 + (n133 + n134);
        final int n137 = (int)n136 & 0xFFFFFFF;
        final long n138 = n136 >>> 28;
        final long n139 = n132 + (n135 - n133);
        final int n140 = (int)n139 & 0xFFFFFFF;
        final long n141 = n139 >>> 28;
        final long n142 = n138 + n141 + n59;
        final int n143 = (int)n142 & 0xFFFFFFF;
        final long n144 = n142 >>> 28;
        final long n145 = n141 + n56;
        final int n146 = (int)n145 & 0xFFFFFFF;
        final long n147 = n145 >>> 28;
        final int n148 = n71 + (int)n144;
        final int n149 = n68 + (int)n147;
        array3[0] = n146;
        array3[1] = n149;
        array3[2] = n80;
        array3[3] = n92;
        array3[4] = n104;
        array3[5] = n116;
        array3[6] = n128;
        array3[7] = n137;
        array3[8] = n143;
        array3[9] = n148;
        array3[10] = n83;
        array3[11] = n95;
        array3[12] = n107;
        array3[13] = n119;
        array3[14] = n131;
        array3[15] = n140;
    }
    
    public static void normalize(final int[] array) {
        reduce(array, 1);
        reduce(array, -1);
    }
    
    private static void reduce(final int[] array, final int n) {
        final int n2 = array[15];
        final int n3 = n2 & 0xFFFFFFF;
        int n4 = (n2 >> 28) + n;
        final int n5 = 8;
        array[n5] += n4;
        for (int i = 0; i < 15; ++i) {
            final int n6 = n4 + array[i];
            array[i] = (n6 & 0xFFFFFFF);
            n4 = n6 >> 28;
        }
        array[15] = n3 + n4;
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
        final int n11 = array[10];
        final int n12 = array[11];
        final int n13 = array[12];
        final int n14 = array[13];
        final int n15 = array[14];
        final int n16 = array[15];
        final int n17 = n * 2;
        final int n18 = n2 * 2;
        final int n19 = n3 * 2;
        final int n20 = n4 * 2;
        final int n21 = n5 * 2;
        final int n22 = n6 * 2;
        final int n23 = n7 * 2;
        final int n24 = n9 * 2;
        final int n25 = n10 * 2;
        final int n26 = n11 * 2;
        final int n27 = n12 * 2;
        final int n28 = n13 * 2;
        final int n29 = n14 * 2;
        final int n30 = n15 * 2;
        final int n31 = n + n9;
        final int n32 = n2 + n10;
        final int n33 = n3 + n11;
        final int n34 = n4 + n12;
        final int n35 = n5 + n13;
        final int n36 = n6 + n14;
        final int n37 = n7 + n15;
        final int n38 = n8 + n16;
        final int n39 = n31 * 2;
        final int n40 = n32 * 2;
        final int n41 = n33 * 2;
        final int n42 = n34 * 2;
        final int n43 = n35 * 2;
        final int n44 = n36 * 2;
        final int n45 = n37 * 2;
        final long n46 = n * (long)n;
        final long n47 = n8 * (long)n18 + n7 * (long)n19 + n6 * (long)n20 + n5 * (long)n5;
        final long n48 = n9 * (long)n9;
        final long n49 = n16 * (long)n25 + n15 * (long)n26 + n14 * (long)n27 + n13 * (long)n13;
        final long n50 = n31 * (long)n31;
        final long n51 = n38 * (long)n40 + n37 * (long)n41 + n36 * (long)n42 + n35 * (long)n35;
        final long n52 = n46 + n48 + n51 - n47;
        final int n53 = (int)n52 & 0xFFFFFFF;
        final long n54 = n52 >>> 28;
        final long n55 = n49 + n50 - n46 + n51;
        final int n56 = (int)n55 & 0xFFFFFFF;
        final long n57 = n55 >>> 28;
        final long n58 = n2 * (long)n17;
        final long n59 = n8 * (long)n19 + n7 * (long)n20 + n6 * (long)n21;
        final long n60 = n10 * (long)n24;
        final long n61 = n16 * (long)n26 + n15 * (long)n27 + n14 * (long)n28;
        final long n62 = n32 * (long)n39;
        final long n63 = n38 * (long)n41 + n37 * (long)n42 + n36 * (long)n43;
        final long n64 = n54 + (n58 + n60 + n63 - n59);
        final int n65 = (int)n64 & 0xFFFFFFF;
        final long n66 = n64 >>> 28;
        final long n67 = n57 + (n61 + n62 - n58 + n63);
        final int n68 = (int)n67 & 0xFFFFFFF;
        final long n69 = n67 >>> 28;
        final long n70 = n3 * (long)n17 + n2 * (long)n2;
        final long n71 = n8 * (long)n20 + n7 * (long)n21 + n6 * (long)n6;
        final long n72 = n11 * (long)n24 + n10 * (long)n10;
        final long n73 = n16 * (long)n27 + n15 * (long)n28 + n14 * (long)n14;
        final long n74 = n33 * (long)n39 + n32 * (long)n32;
        final long n75 = n38 * (long)n42 + n37 * (long)n43 + n36 * (long)n36;
        final long n76 = n66 + (n70 + n72 + n75 - n71);
        final int n77 = (int)n76 & 0xFFFFFFF;
        final long n78 = n76 >>> 28;
        final long n79 = n69 + (n73 + n74 - n70 + n75);
        final int n80 = (int)n79 & 0xFFFFFFF;
        final long n81 = n79 >>> 28;
        final long n82 = n4 * (long)n17 + n3 * (long)n18;
        final long n83 = n8 * (long)n21 + n7 * (long)n22;
        final long n84 = n12 * (long)n24 + n11 * (long)n25;
        final long n85 = n16 * (long)n28 + n15 * (long)n29;
        final long n86 = n34 * (long)n39 + n33 * (long)n40;
        final long n87 = n38 * (long)n43 + n37 * (long)n44;
        final long n88 = n78 + (n82 + n84 + n87 - n83);
        final int n89 = (int)n88 & 0xFFFFFFF;
        final long n90 = n88 >>> 28;
        final long n91 = n81 + (n85 + n86 - n82 + n87);
        final int n92 = (int)n91 & 0xFFFFFFF;
        final long n93 = n91 >>> 28;
        final long n94 = n5 * (long)n17 + n4 * (long)n18 + n3 * (long)n3;
        final long n95 = n8 * (long)n22 + n7 * (long)n7;
        final long n96 = n13 * (long)n24 + n12 * (long)n25 + n11 * (long)n11;
        final long n97 = n16 * (long)n29 + n15 * (long)n15;
        final long n98 = n35 * (long)n39 + n34 * (long)n40 + n33 * (long)n33;
        final long n99 = n38 * (long)n44 + n37 * (long)n37;
        final long n100 = n90 + (n94 + n96 + n99 - n95);
        final int n101 = (int)n100 & 0xFFFFFFF;
        final long n102 = n100 >>> 28;
        final long n103 = n93 + (n97 + n98 - n94 + n99);
        final int n104 = (int)n103 & 0xFFFFFFF;
        final long n105 = n103 >>> 28;
        final long n106 = n6 * (long)n17 + n5 * (long)n18 + n4 * (long)n19;
        final long n107 = n8 * (long)n23;
        final long n108 = n14 * (long)n24 + n13 * (long)n25 + n12 * (long)n26;
        final long n109 = n16 * (long)n30;
        final long n110 = n36 * (long)n39 + n35 * (long)n40 + n34 * (long)n41;
        final long n111 = n38 * (long)n45;
        final long n112 = n102 + (n106 + n108 + n111 - n107);
        final int n113 = (int)n112 & 0xFFFFFFF;
        final long n114 = n112 >>> 28;
        final long n115 = n105 + (n109 + n110 - n106 + n111);
        final int n116 = (int)n115 & 0xFFFFFFF;
        final long n117 = n115 >>> 28;
        final long n118 = n7 * (long)n17 + n6 * (long)n18 + n5 * (long)n19 + n4 * (long)n4;
        final long n119 = n8 * (long)n8;
        final long n120 = n15 * (long)n24 + n14 * (long)n25 + n13 * (long)n26 + n12 * (long)n12;
        final long n121 = n16 * (long)n16;
        final long n122 = n37 * (long)n39 + n36 * (long)n40 + n35 * (long)n41 + n34 * (long)n34;
        final long n123 = n38 * (long)n38;
        final long n124 = n114 + (n118 + n120 + n123 - n119);
        final int n125 = (int)n124 & 0xFFFFFFF;
        final long n126 = n124 >>> 28;
        final long n127 = n117 + (n121 + n122 - n118 + n123);
        final int n128 = (int)n127 & 0xFFFFFFF;
        final long n129 = n127 >>> 28;
        final long n130 = n8 * (long)n17 + n7 * (long)n18 + n6 * (long)n19 + n5 * (long)n20;
        final long n131 = n16 * (long)n24 + n15 * (long)n25 + n14 * (long)n26 + n13 * (long)n27;
        final long n132 = n38 * (long)n39 + n37 * (long)n40 + n36 * (long)n41 + n35 * (long)n42;
        final long n133 = n126 + (n130 + n131);
        final int n134 = (int)n133 & 0xFFFFFFF;
        final long n135 = n133 >>> 28;
        final long n136 = n129 + (n132 - n130);
        final int n137 = (int)n136 & 0xFFFFFFF;
        final long n138 = n136 >>> 28;
        final long n139 = n135 + n138 + n56;
        final int n140 = (int)n139 & 0xFFFFFFF;
        final long n141 = n139 >>> 28;
        final long n142 = n138 + n53;
        final int n143 = (int)n142 & 0xFFFFFFF;
        final long n144 = n142 >>> 28;
        final int n145 = n68 + (int)n141;
        final int n146 = n65 + (int)n144;
        array2[0] = n143;
        array2[1] = n146;
        array2[2] = n77;
        array2[3] = n89;
        array2[4] = n101;
        array2[5] = n113;
        array2[6] = n125;
        array2[7] = n134;
        array2[8] = n140;
        array2[9] = n145;
        array2[10] = n80;
        array2[11] = n92;
        array2[12] = n104;
        array2[13] = n116;
        array2[14] = n128;
        array2[15] = n137;
    }
    
    public static void sqr(final int[] array, int n, final int[] array2) {
        sqr(array, array2);
        while (--n > 0) {
            sqr(array2, array2);
        }
    }
    
    public static void sub(final int[] array, final int[] array2, final int[] array3) {
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
        final int n11 = array[10];
        final int n12 = array[11];
        final int n13 = array[12];
        final int n14 = array[13];
        final int n15 = array[14];
        final int n16 = array[15];
        final int n17 = array2[0];
        final int n18 = array2[1];
        final int n19 = array2[2];
        final int n20 = array2[3];
        final int n21 = array2[4];
        final int n22 = array2[5];
        final int n23 = array2[6];
        final int n24 = array2[7];
        final int n25 = array2[8];
        final int n26 = array2[9];
        final int n27 = array2[10];
        final int n28 = array2[11];
        final int n29 = array2[12];
        final int n30 = array2[13];
        final int n31 = array2[14];
        final int n32 = array2[15];
        final int n33 = n + 536870910 - n17;
        final int n34 = n2 + 536870910 - n18;
        final int n35 = n3 + 536870910 - n19;
        final int n36 = n4 + 536870910 - n20;
        final int n37 = n5 + 536870910 - n21;
        final int n38 = n6 + 536870910 - n22;
        final int n39 = n7 + 536870910 - n23;
        final int n40 = n8 + 536870910 - n24;
        final int n41 = n9 + 536870908 - n25;
        final int n42 = n10 + 536870910 - n26;
        final int n43 = n11 + 536870910 - n27;
        final int n44 = n12 + 536870910 - n28;
        final int n45 = n13 + 536870910 - n29;
        final int n46 = n14 + 536870910 - n30;
        final int n47 = n15 + 536870910 - n31;
        final int n48 = n16 + 536870910 - n32;
        final int n49 = n35 + (n34 >>> 28);
        final int n50 = n34 & 0xFFFFFFF;
        final int n51 = n39 + (n38 >>> 28);
        final int n52 = n38 & 0xFFFFFFF;
        final int n53 = n43 + (n42 >>> 28);
        final int n54 = n42 & 0xFFFFFFF;
        final int n55 = n47 + (n46 >>> 28);
        final int n56 = n46 & 0xFFFFFFF;
        final int n57 = n36 + (n49 >>> 28);
        final int n58 = n49 & 0xFFFFFFF;
        final int n59 = n40 + (n51 >>> 28);
        final int n60 = n51 & 0xFFFFFFF;
        final int n61 = n44 + (n53 >>> 28);
        final int n62 = n53 & 0xFFFFFFF;
        final int n63 = n48 + (n55 >>> 28);
        final int n64 = n55 & 0xFFFFFFF;
        final int n65 = n63 >>> 28;
        final int n66 = n63 & 0xFFFFFFF;
        final int n67 = n33 + n65;
        final int n68 = n41 + n65;
        final int n69 = n37 + (n57 >>> 28);
        final int n70 = n57 & 0xFFFFFFF;
        final int n71 = n68 + (n59 >>> 28);
        final int n72 = n59 & 0xFFFFFFF;
        final int n73 = n45 + (n61 >>> 28);
        final int n74 = n61 & 0xFFFFFFF;
        final int n75 = n50 + (n67 >>> 28);
        final int n76 = n67 & 0xFFFFFFF;
        final int n77 = n52 + (n69 >>> 28);
        final int n78 = n69 & 0xFFFFFFF;
        final int n79 = n54 + (n71 >>> 28);
        final int n80 = n71 & 0xFFFFFFF;
        final int n81 = n56 + (n73 >>> 28);
        final int n82 = n73 & 0xFFFFFFF;
        array3[0] = n76;
        array3[1] = n75;
        array3[2] = n58;
        array3[3] = n70;
        array3[4] = n78;
        array3[5] = n77;
        array3[6] = n60;
        array3[7] = n72;
        array3[8] = n80;
        array3[9] = n79;
        array3[10] = n62;
        array3[11] = n74;
        array3[12] = n82;
        array3[13] = n81;
        array3[14] = n64;
        array3[15] = n66;
    }
}
