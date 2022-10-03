package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class RC2Engine implements BlockCipher
{
    private static byte[] piTable;
    private static final int BLOCK_SIZE = 8;
    private int[] workingKey;
    private boolean encrypting;
    
    private int[] generateWorkingKey(final byte[] array, final int n) {
        final int[] array2 = new int[128];
        for (int i = 0; i != array.length; ++i) {
            array2[i] = (array[i] & 0xFF);
        }
        int j = array.length;
        if (j < 128) {
            int n2 = 0;
            int n3 = array2[j - 1];
            do {
                n3 = (RC2Engine.piTable[n3 + array2[n2++] & 0xFF] & 0xFF);
                array2[j++] = n3;
            } while (j < 128);
        }
        final int n4 = n + 7 >> 3;
        int n5 = RC2Engine.piTable[array2[128 - n4] & 255 >> (0x7 & -n)] & 0xFF;
        array2[128 - n4] = n5;
        for (int k = 128 - n4 - 1; k >= 0; --k) {
            n5 = (RC2Engine.piTable[n5 ^ array2[k + n4]] & 0xFF);
            array2[k] = n5;
        }
        final int[] array3 = new int[64];
        for (int l = 0; l != array3.length; ++l) {
            array3[l] = array2[2 * l] + (array2[2 * l + 1] << 8);
        }
        return array3;
    }
    
    public void init(final boolean encrypting, final CipherParameters cipherParameters) {
        this.encrypting = encrypting;
        if (cipherParameters instanceof RC2Parameters) {
            final RC2Parameters rc2Parameters = (RC2Parameters)cipherParameters;
            this.workingKey = this.generateWorkingKey(rc2Parameters.getKey(), rc2Parameters.getEffectiveKeyBits());
        }
        else {
            if (!(cipherParameters instanceof KeyParameter)) {
                throw new IllegalArgumentException("invalid parameter passed to RC2 init - " + cipherParameters.getClass().getName());
            }
            final byte[] key = ((KeyParameter)cipherParameters).getKey();
            this.workingKey = this.generateWorkingKey(key, key.length * 8);
        }
    }
    
    public void reset() {
    }
    
    public String getAlgorithmName() {
        return "RC2";
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public final int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (this.workingKey == null) {
            throw new IllegalStateException("RC2 engine not initialised");
        }
        if (n + 8 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 8 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.encrypting) {
            this.encryptBlock(array, n, array2, n2);
        }
        else {
            this.decryptBlock(array, n, array2, n2);
        }
        return 8;
    }
    
    private int rotateWordLeft(int n, final int n2) {
        n &= 0xFFFF;
        return n << n2 | n >> 16 - n2;
    }
    
    private void encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int rotateWordLeft = ((array[n + 7] & 0xFF) << 8) + (array[n + 6] & 0xFF);
        int rotateWordLeft2 = ((array[n + 5] & 0xFF) << 8) + (array[n + 4] & 0xFF);
        int rotateWordLeft3 = ((array[n + 3] & 0xFF) << 8) + (array[n + 2] & 0xFF);
        int rotateWordLeft4 = ((array[n + 1] & 0xFF) << 8) + (array[n + 0] & 0xFF);
        for (int i = 0; i <= 16; i += 4) {
            rotateWordLeft4 = this.rotateWordLeft(rotateWordLeft4 + (rotateWordLeft3 & ~rotateWordLeft) + (rotateWordLeft2 & rotateWordLeft) + this.workingKey[i], 1);
            rotateWordLeft3 = this.rotateWordLeft(rotateWordLeft3 + (rotateWordLeft2 & ~rotateWordLeft4) + (rotateWordLeft & rotateWordLeft4) + this.workingKey[i + 1], 2);
            rotateWordLeft2 = this.rotateWordLeft(rotateWordLeft2 + (rotateWordLeft & ~rotateWordLeft3) + (rotateWordLeft4 & rotateWordLeft3) + this.workingKey[i + 2], 3);
            rotateWordLeft = this.rotateWordLeft(rotateWordLeft + (rotateWordLeft4 & ~rotateWordLeft2) + (rotateWordLeft3 & rotateWordLeft2) + this.workingKey[i + 3], 5);
        }
        int rotateWordLeft5 = rotateWordLeft4 + this.workingKey[rotateWordLeft & 0x3F];
        int rotateWordLeft6 = rotateWordLeft3 + this.workingKey[rotateWordLeft5 & 0x3F];
        int rotateWordLeft7 = rotateWordLeft2 + this.workingKey[rotateWordLeft6 & 0x3F];
        int rotateWordLeft8 = rotateWordLeft + this.workingKey[rotateWordLeft7 & 0x3F];
        for (int j = 20; j <= 40; j += 4) {
            rotateWordLeft5 = this.rotateWordLeft(rotateWordLeft5 + (rotateWordLeft6 & ~rotateWordLeft8) + (rotateWordLeft7 & rotateWordLeft8) + this.workingKey[j], 1);
            rotateWordLeft6 = this.rotateWordLeft(rotateWordLeft6 + (rotateWordLeft7 & ~rotateWordLeft5) + (rotateWordLeft8 & rotateWordLeft5) + this.workingKey[j + 1], 2);
            rotateWordLeft7 = this.rotateWordLeft(rotateWordLeft7 + (rotateWordLeft8 & ~rotateWordLeft6) + (rotateWordLeft5 & rotateWordLeft6) + this.workingKey[j + 2], 3);
            rotateWordLeft8 = this.rotateWordLeft(rotateWordLeft8 + (rotateWordLeft5 & ~rotateWordLeft7) + (rotateWordLeft6 & rotateWordLeft7) + this.workingKey[j + 3], 5);
        }
        int rotateWordLeft9 = rotateWordLeft5 + this.workingKey[rotateWordLeft8 & 0x3F];
        int rotateWordLeft10 = rotateWordLeft6 + this.workingKey[rotateWordLeft9 & 0x3F];
        int rotateWordLeft11 = rotateWordLeft7 + this.workingKey[rotateWordLeft10 & 0x3F];
        int rotateWordLeft12 = rotateWordLeft8 + this.workingKey[rotateWordLeft11 & 0x3F];
        for (int k = 44; k < 64; k += 4) {
            rotateWordLeft9 = this.rotateWordLeft(rotateWordLeft9 + (rotateWordLeft10 & ~rotateWordLeft12) + (rotateWordLeft11 & rotateWordLeft12) + this.workingKey[k], 1);
            rotateWordLeft10 = this.rotateWordLeft(rotateWordLeft10 + (rotateWordLeft11 & ~rotateWordLeft9) + (rotateWordLeft12 & rotateWordLeft9) + this.workingKey[k + 1], 2);
            rotateWordLeft11 = this.rotateWordLeft(rotateWordLeft11 + (rotateWordLeft12 & ~rotateWordLeft10) + (rotateWordLeft9 & rotateWordLeft10) + this.workingKey[k + 2], 3);
            rotateWordLeft12 = this.rotateWordLeft(rotateWordLeft12 + (rotateWordLeft9 & ~rotateWordLeft11) + (rotateWordLeft10 & rotateWordLeft11) + this.workingKey[k + 3], 5);
        }
        array2[n2 + 0] = (byte)rotateWordLeft9;
        array2[n2 + 1] = (byte)(rotateWordLeft9 >> 8);
        array2[n2 + 2] = (byte)rotateWordLeft10;
        array2[n2 + 3] = (byte)(rotateWordLeft10 >> 8);
        array2[n2 + 4] = (byte)rotateWordLeft11;
        array2[n2 + 5] = (byte)(rotateWordLeft11 >> 8);
        array2[n2 + 6] = (byte)rotateWordLeft12;
        array2[n2 + 7] = (byte)(rotateWordLeft12 >> 8);
    }
    
    private void decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int n3 = ((array[n + 7] & 0xFF) << 8) + (array[n + 6] & 0xFF);
        int n4 = ((array[n + 5] & 0xFF) << 8) + (array[n + 4] & 0xFF);
        int n5 = ((array[n + 3] & 0xFF) << 8) + (array[n + 2] & 0xFF);
        int n6 = ((array[n + 1] & 0xFF) << 8) + (array[n + 0] & 0xFF);
        for (int i = 60; i >= 44; i -= 4) {
            n3 = this.rotateWordLeft(n3, 11) - ((n6 & ~n4) + (n5 & n4) + this.workingKey[i + 3]);
            n4 = this.rotateWordLeft(n4, 13) - ((n3 & ~n5) + (n6 & n5) + this.workingKey[i + 2]);
            n5 = this.rotateWordLeft(n5, 14) - ((n4 & ~n6) + (n3 & n6) + this.workingKey[i + 1]);
            n6 = this.rotateWordLeft(n6, 15) - ((n5 & ~n3) + (n4 & n3) + this.workingKey[i]);
        }
        int n7 = n3 - this.workingKey[n4 & 0x3F];
        int n8 = n4 - this.workingKey[n5 & 0x3F];
        int n9 = n5 - this.workingKey[n6 & 0x3F];
        int n10 = n6 - this.workingKey[n7 & 0x3F];
        for (int j = 40; j >= 20; j -= 4) {
            n7 = this.rotateWordLeft(n7, 11) - ((n10 & ~n8) + (n9 & n8) + this.workingKey[j + 3]);
            n8 = this.rotateWordLeft(n8, 13) - ((n7 & ~n9) + (n10 & n9) + this.workingKey[j + 2]);
            n9 = this.rotateWordLeft(n9, 14) - ((n8 & ~n10) + (n7 & n10) + this.workingKey[j + 1]);
            n10 = this.rotateWordLeft(n10, 15) - ((n9 & ~n7) + (n8 & n7) + this.workingKey[j]);
        }
        int n11 = n7 - this.workingKey[n8 & 0x3F];
        int n12 = n8 - this.workingKey[n9 & 0x3F];
        int n13 = n9 - this.workingKey[n10 & 0x3F];
        int n14 = n10 - this.workingKey[n11 & 0x3F];
        for (int k = 16; k >= 0; k -= 4) {
            n11 = this.rotateWordLeft(n11, 11) - ((n14 & ~n12) + (n13 & n12) + this.workingKey[k + 3]);
            n12 = this.rotateWordLeft(n12, 13) - ((n11 & ~n13) + (n14 & n13) + this.workingKey[k + 2]);
            n13 = this.rotateWordLeft(n13, 14) - ((n12 & ~n14) + (n11 & n14) + this.workingKey[k + 1]);
            n14 = this.rotateWordLeft(n14, 15) - ((n13 & ~n11) + (n12 & n11) + this.workingKey[k]);
        }
        array2[n2 + 0] = (byte)n14;
        array2[n2 + 1] = (byte)(n14 >> 8);
        array2[n2 + 2] = (byte)n13;
        array2[n2 + 3] = (byte)(n13 >> 8);
        array2[n2 + 4] = (byte)n12;
        array2[n2 + 5] = (byte)(n12 >> 8);
        array2[n2 + 6] = (byte)n11;
        array2[n2 + 7] = (byte)(n11 >> 8);
    }
    
    static {
        RC2Engine.piTable = new byte[] { -39, 120, -7, -60, 25, -35, -75, -19, 40, -23, -3, 121, 74, -96, -40, -99, -58, 126, 55, -125, 43, 118, 83, -114, 98, 76, 100, -120, 68, -117, -5, -94, 23, -102, 89, -11, -121, -77, 79, 19, 97, 69, 109, -115, 9, -127, 125, 50, -67, -113, 64, -21, -122, -73, 123, 11, -16, -107, 33, 34, 92, 107, 78, -126, 84, -42, 101, -109, -50, 96, -78, 28, 115, 86, -64, 20, -89, -116, -15, -36, 18, 117, -54, 31, 59, -66, -28, -47, 66, 61, -44, 48, -93, 60, -74, 38, 111, -65, 14, -38, 70, 105, 7, 87, 39, -14, 29, -101, -68, -108, 67, 3, -8, 17, -57, -10, -112, -17, 62, -25, 6, -61, -43, 47, -56, 102, 30, -41, 8, -24, -22, -34, -128, 82, -18, -9, -124, -86, 114, -84, 53, 77, 106, 42, -106, 26, -46, 113, 90, 21, 73, 116, 75, -97, -48, 94, 4, 24, -92, -20, -62, -32, 65, 110, 15, 81, -53, -52, 36, -111, -81, 80, -95, -12, 112, 57, -103, 124, 58, -123, 35, -72, -76, 122, -4, 2, 54, 91, 37, 85, -105, 49, 45, 93, -6, -104, -29, -118, -110, -82, 5, -33, 41, 16, 103, 108, -70, -55, -45, 0, -26, -49, -31, -98, -88, 44, 99, 22, 1, 63, 88, -30, -119, -87, 13, 56, 52, 27, -85, 51, -1, -80, -69, 72, 12, 95, -71, -79, -51, 46, -59, -13, -37, 71, -27, -91, -100, 119, 10, -90, 32, 104, -2, 127, -63, -83 };
    }
}
