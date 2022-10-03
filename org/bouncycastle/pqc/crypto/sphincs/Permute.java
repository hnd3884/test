package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.util.Pack;

class Permute
{
    private static final int CHACHA_ROUNDS = 12;
    
    protected static int rotl(final int n, final int n2) {
        return n << n2 | n >>> -n2;
    }
    
    public static void permute(final int n, final int[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException();
        }
        if (n % 2 != 0) {
            throw new IllegalArgumentException("Number of rounds must be even");
        }
        int n2 = array[0];
        int n3 = array[1];
        int n4 = array[2];
        int n5 = array[3];
        int rotl = array[4];
        int rotl2 = array[5];
        int rotl3 = array[6];
        int rotl4 = array[7];
        int n6 = array[8];
        int n7 = array[9];
        int n8 = array[10];
        int n9 = array[11];
        int rotl5 = array[12];
        int rotl6 = array[13];
        int rotl7 = array[14];
        int rotl8 = array[15];
        for (int i = n; i > 0; i -= 2) {
            final int n10 = n2 + rotl;
            final int rotl9 = rotl(rotl5 ^ n10, 16);
            final int n11 = n6 + rotl9;
            final int rotl10 = rotl(rotl ^ n11, 12);
            final int n12 = n10 + rotl10;
            final int rotl11 = rotl(rotl9 ^ n12, 8);
            final int n13 = n11 + rotl11;
            final int rotl12 = rotl(rotl10 ^ n13, 7);
            final int n14 = n3 + rotl2;
            final int rotl13 = rotl(rotl6 ^ n14, 16);
            final int n15 = n7 + rotl13;
            final int rotl14 = rotl(rotl2 ^ n15, 12);
            final int n16 = n14 + rotl14;
            final int rotl15 = rotl(rotl13 ^ n16, 8);
            final int n17 = n15 + rotl15;
            final int rotl16 = rotl(rotl14 ^ n17, 7);
            final int n18 = n4 + rotl3;
            final int rotl17 = rotl(rotl7 ^ n18, 16);
            final int n19 = n8 + rotl17;
            final int rotl18 = rotl(rotl3 ^ n19, 12);
            final int n20 = n18 + rotl18;
            final int rotl19 = rotl(rotl17 ^ n20, 8);
            final int n21 = n19 + rotl19;
            final int rotl20 = rotl(rotl18 ^ n21, 7);
            final int n22 = n5 + rotl4;
            final int rotl21 = rotl(rotl8 ^ n22, 16);
            final int n23 = n9 + rotl21;
            final int rotl22 = rotl(rotl4 ^ n23, 12);
            final int n24 = n22 + rotl22;
            final int rotl23 = rotl(rotl21 ^ n24, 8);
            final int n25 = n23 + rotl23;
            final int rotl24 = rotl(rotl22 ^ n25, 7);
            final int n26 = n12 + rotl16;
            final int rotl25 = rotl(rotl23 ^ n26, 16);
            final int n27 = n21 + rotl25;
            final int rotl26 = rotl(rotl16 ^ n27, 12);
            n2 = n26 + rotl26;
            rotl8 = rotl(rotl25 ^ n2, 8);
            n8 = n27 + rotl8;
            rotl2 = rotl(rotl26 ^ n8, 7);
            final int n28 = n16 + rotl20;
            final int rotl27 = rotl(rotl11 ^ n28, 16);
            final int n29 = n25 + rotl27;
            final int rotl28 = rotl(rotl20 ^ n29, 12);
            n3 = n28 + rotl28;
            rotl5 = rotl(rotl27 ^ n3, 8);
            n9 = n29 + rotl5;
            rotl3 = rotl(rotl28 ^ n9, 7);
            final int n30 = n20 + rotl24;
            final int rotl29 = rotl(rotl15 ^ n30, 16);
            final int n31 = n13 + rotl29;
            final int rotl30 = rotl(rotl24 ^ n31, 12);
            n4 = n30 + rotl30;
            rotl6 = rotl(rotl29 ^ n4, 8);
            n6 = n31 + rotl6;
            rotl4 = rotl(rotl30 ^ n6, 7);
            final int n32 = n24 + rotl12;
            final int rotl31 = rotl(rotl19 ^ n32, 16);
            final int n33 = n17 + rotl31;
            final int rotl32 = rotl(rotl12 ^ n33, 12);
            n5 = n32 + rotl32;
            rotl7 = rotl(rotl31 ^ n5, 8);
            n7 = n33 + rotl7;
            rotl = rotl(rotl32 ^ n7, 7);
        }
        array[0] = n2;
        array[1] = n3;
        array[2] = n4;
        array[3] = n5;
        array[4] = rotl;
        array[5] = rotl2;
        array[6] = rotl3;
        array[7] = rotl4;
        array[8] = n6;
        array[9] = n7;
        array[10] = n8;
        array[11] = n9;
        array[12] = rotl5;
        array[13] = rotl6;
        array[14] = rotl7;
        array[15] = rotl8;
    }
    
    void chacha_permute(final byte[] array, final byte[] array2) {
        final int[] array3 = new int[16];
        for (int i = 0; i < 16; ++i) {
            array3[i] = Pack.littleEndianToInt(array2, 4 * i);
        }
        permute(12, array3);
        for (int j = 0; j < 16; ++j) {
            Pack.intToLittleEndian(array3[j], array, 4 * j);
        }
    }
}
