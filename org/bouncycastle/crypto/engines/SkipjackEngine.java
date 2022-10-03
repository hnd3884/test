package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class SkipjackEngine implements BlockCipher
{
    static final int BLOCK_SIZE = 8;
    static short[] ftable;
    private int[] key0;
    private int[] key1;
    private int[] key2;
    private int[] key3;
    private boolean encrypting;
    
    public void init(final boolean encrypting, final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to SKIPJACK init - " + cipherParameters.getClass().getName());
        }
        final byte[] key = ((KeyParameter)cipherParameters).getKey();
        this.encrypting = encrypting;
        this.key0 = new int[32];
        this.key1 = new int[32];
        this.key2 = new int[32];
        this.key3 = new int[32];
        for (int i = 0; i < 32; ++i) {
            this.key0[i] = (key[i * 4 % 10] & 0xFF);
            this.key1[i] = (key[(i * 4 + 1) % 10] & 0xFF);
            this.key2[i] = (key[(i * 4 + 2) % 10] & 0xFF);
            this.key3[i] = (key[(i * 4 + 3) % 10] & 0xFF);
        }
    }
    
    public String getAlgorithmName() {
        return "SKIPJACK";
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (this.key1 == null) {
            throw new IllegalStateException("SKIPJACK engine not initialised");
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
    
    public void reset() {
    }
    
    private int g(final int n, final int n2) {
        final int n3 = n2 >> 8 & 0xFF;
        final int n4 = n2 & 0xFF;
        final int n5 = SkipjackEngine.ftable[n4 ^ this.key0[n]] ^ n3;
        final int n6 = SkipjackEngine.ftable[n5 ^ this.key1[n]] ^ n4;
        final int n7 = SkipjackEngine.ftable[n6 ^ this.key2[n]] ^ n5;
        return (n7 << 8) + (SkipjackEngine.ftable[n7 ^ this.key3[n]] ^ n6);
    }
    
    public int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int n3 = (array[n + 0] << 8) + (array[n + 1] & 0xFF);
        int n4 = (array[n + 2] << 8) + (array[n + 3] & 0xFF);
        int n5 = (array[n + 4] << 8) + (array[n + 5] & 0xFF);
        int n6 = (array[n + 6] << 8) + (array[n + 7] & 0xFF);
        int n7 = 0;
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 8; ++j) {
                final int n8 = n6;
                n6 = n5;
                n5 = n4;
                n4 = this.g(n7, n3);
                n3 = (n4 ^ n8 ^ n7 + 1);
                ++n7;
            }
            for (int k = 0; k < 8; ++k) {
                final int n9 = n6;
                n6 = n5;
                n5 = (n3 ^ n4 ^ n7 + 1);
                n4 = this.g(n7, n3);
                n3 = n9;
                ++n7;
            }
        }
        array2[n2 + 0] = (byte)(n3 >> 8);
        array2[n2 + 1] = (byte)n3;
        array2[n2 + 2] = (byte)(n4 >> 8);
        array2[n2 + 3] = (byte)n4;
        array2[n2 + 4] = (byte)(n5 >> 8);
        array2[n2 + 5] = (byte)n5;
        array2[n2 + 6] = (byte)(n6 >> 8);
        array2[n2 + 7] = (byte)n6;
        return 8;
    }
    
    private int h(final int n, final int n2) {
        final int n3 = n2 & 0xFF;
        final int n4 = n2 >> 8 & 0xFF;
        final int n5 = SkipjackEngine.ftable[n4 ^ this.key3[n]] ^ n3;
        final int n6 = SkipjackEngine.ftable[n5 ^ this.key2[n]] ^ n4;
        final int n7 = SkipjackEngine.ftable[n6 ^ this.key1[n]] ^ n5;
        return ((SkipjackEngine.ftable[n7 ^ this.key0[n]] ^ n6) << 8) + n7;
    }
    
    public int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int n3 = (array[n + 0] << 8) + (array[n + 1] & 0xFF);
        int n4 = (array[n + 2] << 8) + (array[n + 3] & 0xFF);
        int n5 = (array[n + 4] << 8) + (array[n + 5] & 0xFF);
        int n6 = (array[n + 6] << 8) + (array[n + 7] & 0xFF);
        int n7 = 31;
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 8; ++j) {
                final int n8 = n5;
                n5 = n6;
                n6 = n3;
                n3 = this.h(n7, n4);
                n4 = (n3 ^ n8 ^ n7 + 1);
                --n7;
            }
            for (int k = 0; k < 8; ++k) {
                final int n9 = n5;
                n5 = n6;
                n6 = (n4 ^ n3 ^ n7 + 1);
                n3 = this.h(n7, n4);
                n4 = n9;
                --n7;
            }
        }
        array2[n2 + 0] = (byte)(n3 >> 8);
        array2[n2 + 1] = (byte)n3;
        array2[n2 + 2] = (byte)(n4 >> 8);
        array2[n2 + 3] = (byte)n4;
        array2[n2 + 4] = (byte)(n5 >> 8);
        array2[n2 + 5] = (byte)n5;
        array2[n2 + 6] = (byte)(n6 >> 8);
        array2[n2 + 7] = (byte)n6;
        return 8;
    }
    
    static {
        SkipjackEngine.ftable = new short[] { 163, 215, 9, 131, 248, 72, 246, 244, 179, 33, 21, 120, 153, 177, 175, 249, 231, 45, 77, 138, 206, 76, 202, 46, 82, 149, 217, 30, 78, 56, 68, 40, 10, 223, 2, 160, 23, 241, 96, 104, 18, 183, 122, 195, 233, 250, 61, 83, 150, 132, 107, 186, 242, 99, 154, 25, 124, 174, 229, 245, 247, 22, 106, 162, 57, 182, 123, 15, 193, 147, 129, 27, 238, 180, 26, 234, 208, 145, 47, 184, 85, 185, 218, 133, 63, 65, 191, 224, 90, 88, 128, 95, 102, 11, 216, 144, 53, 213, 192, 167, 51, 6, 101, 105, 69, 0, 148, 86, 109, 152, 155, 118, 151, 252, 178, 194, 176, 254, 219, 32, 225, 235, 214, 228, 221, 71, 74, 29, 66, 237, 158, 110, 73, 60, 205, 67, 39, 210, 7, 212, 222, 199, 103, 24, 137, 203, 48, 31, 141, 198, 143, 170, 200, 116, 220, 201, 93, 92, 49, 164, 112, 136, 97, 44, 159, 13, 43, 135, 80, 130, 84, 100, 38, 125, 3, 64, 52, 75, 28, 115, 209, 196, 253, 59, 204, 251, 127, 171, 230, 62, 91, 165, 173, 4, 35, 156, 20, 81, 34, 240, 41, 121, 113, 126, 255, 140, 14, 226, 12, 239, 188, 114, 117, 111, 55, 161, 236, 211, 142, 98, 139, 134, 16, 232, 8, 119, 17, 190, 146, 79, 36, 197, 50, 54, 157, 207, 243, 166, 187, 172, 94, 108, 169, 19, 87, 37, 181, 227, 189, 168, 58, 1, 5, 89, 42, 70 };
    }
}
