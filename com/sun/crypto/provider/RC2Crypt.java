package com.sun.crypto.provider;

import java.security.InvalidKeyException;

final class RC2Crypt extends SymmetricCipher
{
    private static final int[] PI_TABLE;
    private final int[] expandedKey;
    private int effectiveKeyBits;
    
    RC2Crypt() {
        this.expandedKey = new int[64];
    }
    
    @Override
    int getBlockSize() {
        return 8;
    }
    
    int getEffectiveKeyBits() {
        return this.effectiveKeyBits;
    }
    
    void initEffectiveKeyBits(final int effectiveKeyBits) {
        this.effectiveKeyBits = effectiveKeyBits;
    }
    
    static void checkKey(final String s, final int n) throws InvalidKeyException {
        if (!s.equals("RC2")) {
            throw new InvalidKeyException("Key algorithm must be RC2");
        }
        if (n < 5 || n > 128) {
            throw new InvalidKeyException("RC2 key length must be between 40 and 1024 bit");
        }
    }
    
    @Override
    void init(final boolean b, final String s, final byte[] array) throws InvalidKeyException {
        final int length = array.length;
        if (this.effectiveKeyBits == 0) {
            this.effectiveKeyBits = length << 3;
        }
        checkKey(s, length);
        final byte[] array2 = new byte[128];
        System.arraycopy(array, 0, array2, 0, length);
        int n = array2[length - 1];
        for (int i = length; i < 128; ++i) {
            n = RC2Crypt.PI_TABLE[n + array2[i - length] & 0xFF];
            array2[i] = (byte)n;
        }
        final int n2 = this.effectiveKeyBits + 7 >> 3;
        int n3 = RC2Crypt.PI_TABLE[array2[128 - n2] & 255 >> (-this.effectiveKeyBits & 0x7)];
        array2[128 - n2] = (byte)n3;
        for (int j = 127 - n2; j >= 0; --j) {
            n3 = RC2Crypt.PI_TABLE[n3 ^ (array2[j + n2] & 0xFF)];
            array2[j] = (byte)n3;
        }
        for (int k = 0, n4 = 0; k < 64; ++k, n4 += 2) {
            this.expandedKey[k] = (array2[n4] & 0xFF) + ((array2[n4 + 1] & 0xFF) << 8);
        }
    }
    
    @Override
    void encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int n3 = (array[n] & 0xFF) + ((array[n + 1] & 0xFF) << 8);
        int n4 = (array[n + 2] & 0xFF) + ((array[n + 3] & 0xFF) << 8);
        int n5 = (array[n + 4] & 0xFF) + ((array[n + 5] & 0xFF) << 8);
        int n6 = (array[n + 6] & 0xFF) + ((array[n + 7] & 0xFF) << 8);
        for (int i = 0; i < 20; i += 4) {
            final int n7 = n3 + this.expandedKey[i] + (n6 & n5) + (~n6 & n4) & 0xFFFF;
            n3 = (n7 << 1 | n7 >>> 15);
            final int n8 = n4 + this.expandedKey[i + 1] + (n3 & n6) + (~n3 & n5) & 0xFFFF;
            n4 = (n8 << 2 | n8 >>> 14);
            final int n9 = n5 + this.expandedKey[i + 2] + (n4 & n3) + (~n4 & n6) & 0xFFFF;
            n5 = (n9 << 3 | n9 >>> 13);
            final int n10 = n6 + this.expandedKey[i + 3] + (n5 & n4) + (~n5 & n3) & 0xFFFF;
            n6 = (n10 << 5 | n10 >>> 11);
        }
        int n11 = n3 + this.expandedKey[n6 & 0x3F];
        int n12 = n4 + this.expandedKey[n11 & 0x3F];
        int n13 = n5 + this.expandedKey[n12 & 0x3F];
        int n14 = n6 + this.expandedKey[n13 & 0x3F];
        for (int j = 20; j < 44; j += 4) {
            final int n15 = n11 + this.expandedKey[j] + (n14 & n13) + (~n14 & n12) & 0xFFFF;
            n11 = (n15 << 1 | n15 >>> 15);
            final int n16 = n12 + this.expandedKey[j + 1] + (n11 & n14) + (~n11 & n13) & 0xFFFF;
            n12 = (n16 << 2 | n16 >>> 14);
            final int n17 = n13 + this.expandedKey[j + 2] + (n12 & n11) + (~n12 & n14) & 0xFFFF;
            n13 = (n17 << 3 | n17 >>> 13);
            final int n18 = n14 + this.expandedKey[j + 3] + (n13 & n12) + (~n13 & n11) & 0xFFFF;
            n14 = (n18 << 5 | n18 >>> 11);
        }
        int n19 = n11 + this.expandedKey[n14 & 0x3F];
        int n20 = n12 + this.expandedKey[n19 & 0x3F];
        int n21 = n13 + this.expandedKey[n20 & 0x3F];
        int n22 = n14 + this.expandedKey[n21 & 0x3F];
        for (int k = 44; k < 64; k += 4) {
            final int n23 = n19 + this.expandedKey[k] + (n22 & n21) + (~n22 & n20) & 0xFFFF;
            n19 = (n23 << 1 | n23 >>> 15);
            final int n24 = n20 + this.expandedKey[k + 1] + (n19 & n22) + (~n19 & n21) & 0xFFFF;
            n20 = (n24 << 2 | n24 >>> 14);
            final int n25 = n21 + this.expandedKey[k + 2] + (n20 & n19) + (~n20 & n22) & 0xFFFF;
            n21 = (n25 << 3 | n25 >>> 13);
            final int n26 = n22 + this.expandedKey[k + 3] + (n21 & n20) + (~n21 & n19) & 0xFFFF;
            n22 = (n26 << 5 | n26 >>> 11);
        }
        array2[n2] = (byte)n19;
        array2[n2 + 1] = (byte)(n19 >> 8);
        array2[n2 + 2] = (byte)n20;
        array2[n2 + 3] = (byte)(n20 >> 8);
        array2[n2 + 4] = (byte)n21;
        array2[n2 + 5] = (byte)(n21 >> 8);
        array2[n2 + 6] = (byte)n22;
        array2[n2 + 7] = (byte)(n22 >> 8);
    }
    
    @Override
    void decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int n3 = (array[n] & 0xFF) + ((array[n + 1] & 0xFF) << 8);
        int n4 = (array[n + 2] & 0xFF) + ((array[n + 3] & 0xFF) << 8);
        int n5 = (array[n + 4] & 0xFF) + ((array[n + 5] & 0xFF) << 8);
        int n6 = (array[n + 6] & 0xFF) + ((array[n + 7] & 0xFF) << 8);
        for (int i = 64; i > 44; i -= 4) {
            n6 = (((n6 << 11 | n6 >>> 5) & 0xFFFF) - this.expandedKey[i - 1] - (n5 & n4) - (~n5 & n3) & 0xFFFF);
            n5 = (((n5 << 13 | n5 >>> 3) & 0xFFFF) - this.expandedKey[i - 2] - (n4 & n3) - (~n4 & n6) & 0xFFFF);
            n4 = (((n4 << 14 | n4 >>> 2) & 0xFFFF) - this.expandedKey[i - 3] - (n3 & n6) - (~n3 & n5) & 0xFFFF);
            n3 = (((n3 << 15 | n3 >>> 1) & 0xFFFF) - this.expandedKey[i - 4] - (n6 & n5) - (~n6 & n4) & 0xFFFF);
        }
        int n7 = n6 - this.expandedKey[n5 & 0x3F] & 0xFFFF;
        int n8 = n5 - this.expandedKey[n4 & 0x3F] & 0xFFFF;
        int n9 = n4 - this.expandedKey[n3 & 0x3F] & 0xFFFF;
        int n10 = n3 - this.expandedKey[n7 & 0x3F] & 0xFFFF;
        for (int j = 44; j > 20; j -= 4) {
            n7 = (((n7 << 11 | n7 >>> 5) & 0xFFFF) - this.expandedKey[j - 1] - (n8 & n9) - (~n8 & n10) & 0xFFFF);
            n8 = (((n8 << 13 | n8 >>> 3) & 0xFFFF) - this.expandedKey[j - 2] - (n9 & n10) - (~n9 & n7) & 0xFFFF);
            n9 = (((n9 << 14 | n9 >>> 2) & 0xFFFF) - this.expandedKey[j - 3] - (n10 & n7) - (~n10 & n8) & 0xFFFF);
            n10 = (((n10 << 15 | n10 >>> 1) & 0xFFFF) - this.expandedKey[j - 4] - (n7 & n8) - (~n7 & n9) & 0xFFFF);
        }
        int n11 = n7 - this.expandedKey[n8 & 0x3F] & 0xFFFF;
        int n12 = n8 - this.expandedKey[n9 & 0x3F] & 0xFFFF;
        int n13 = n9 - this.expandedKey[n10 & 0x3F] & 0xFFFF;
        int n14 = n10 - this.expandedKey[n11 & 0x3F] & 0xFFFF;
        for (int k = 20; k > 0; k -= 4) {
            n11 = (((n11 << 11 | n11 >>> 5) & 0xFFFF) - this.expandedKey[k - 1] - (n12 & n13) - (~n12 & n14) & 0xFFFF);
            n12 = (((n12 << 13 | n12 >>> 3) & 0xFFFF) - this.expandedKey[k - 2] - (n13 & n14) - (~n13 & n11) & 0xFFFF);
            n13 = (((n13 << 14 | n13 >>> 2) & 0xFFFF) - this.expandedKey[k - 3] - (n14 & n11) - (~n14 & n12) & 0xFFFF);
            n14 = (((n14 << 15 | n14 >>> 1) & 0xFFFF) - this.expandedKey[k - 4] - (n11 & n12) - (~n11 & n13) & 0xFFFF);
        }
        array2[n2] = (byte)n14;
        array2[n2 + 1] = (byte)(n14 >> 8);
        array2[n2 + 2] = (byte)n13;
        array2[n2 + 3] = (byte)(n13 >> 8);
        array2[n2 + 4] = (byte)n12;
        array2[n2 + 5] = (byte)(n12 >> 8);
        array2[n2 + 6] = (byte)n11;
        array2[n2 + 7] = (byte)(n11 >> 8);
    }
    
    static {
        PI_TABLE = new int[] { 217, 120, 249, 196, 25, 221, 181, 237, 40, 233, 253, 121, 74, 160, 216, 157, 198, 126, 55, 131, 43, 118, 83, 142, 98, 76, 100, 136, 68, 139, 251, 162, 23, 154, 89, 245, 135, 179, 79, 19, 97, 69, 109, 141, 9, 129, 125, 50, 189, 143, 64, 235, 134, 183, 123, 11, 240, 149, 33, 34, 92, 107, 78, 130, 84, 214, 101, 147, 206, 96, 178, 28, 115, 86, 192, 20, 167, 140, 241, 220, 18, 117, 202, 31, 59, 190, 228, 209, 66, 61, 212, 48, 163, 60, 182, 38, 111, 191, 14, 218, 70, 105, 7, 87, 39, 242, 29, 155, 188, 148, 67, 3, 248, 17, 199, 246, 144, 239, 62, 231, 6, 195, 213, 47, 200, 102, 30, 215, 8, 232, 234, 222, 128, 82, 238, 247, 132, 170, 114, 172, 53, 77, 106, 42, 150, 26, 210, 113, 90, 21, 73, 116, 75, 159, 208, 94, 4, 24, 164, 236, 194, 224, 65, 110, 15, 81, 203, 204, 36, 145, 175, 80, 161, 244, 112, 57, 153, 124, 58, 133, 35, 184, 180, 122, 252, 2, 54, 91, 37, 85, 151, 49, 45, 93, 250, 152, 227, 138, 146, 174, 5, 223, 41, 16, 103, 108, 186, 201, 211, 0, 230, 207, 225, 158, 168, 44, 99, 22, 1, 63, 88, 226, 137, 169, 13, 56, 52, 27, 171, 51, 255, 176, 187, 72, 12, 95, 185, 177, 205, 46, 197, 243, 219, 71, 229, 165, 156, 119, 10, 166, 32, 104, 254, 127, 193, 173 };
    }
}
