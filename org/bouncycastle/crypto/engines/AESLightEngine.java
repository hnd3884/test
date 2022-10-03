package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.BlockCipher;

public class AESLightEngine implements BlockCipher
{
    private static final byte[] S;
    private static final byte[] Si;
    private static final int[] rcon;
    private static final int m1 = -2139062144;
    private static final int m2 = 2139062143;
    private static final int m3 = 27;
    private static final int m4 = -1061109568;
    private static final int m5 = 1061109567;
    private int ROUNDS;
    private int[][] WorkingKey;
    private int C0;
    private int C1;
    private int C2;
    private int C3;
    private boolean forEncryption;
    private static final int BLOCK_SIZE = 16;
    
    private static int shift(final int n, final int n2) {
        return n >>> n2 | n << -n2;
    }
    
    private static int FFmulX(final int n) {
        return (n & 0x7F7F7F7F) << 1 ^ ((n & 0x80808080) >>> 7) * 27;
    }
    
    private static int FFmulX2(final int n) {
        final int n2 = (n & 0x3F3F3F3F) << 2;
        final int n3 = n & 0xC0C0C0C0;
        final int n4 = n3 ^ n3 >>> 1;
        return n2 ^ n4 >>> 2 ^ n4 >>> 5;
    }
    
    private static int mcol(final int n) {
        final int shift = shift(n, 8);
        final int n2 = n ^ shift;
        return shift(n2, 16) ^ shift ^ FFmulX(n2);
    }
    
    private static int inv_mcol(final int n) {
        final int n2 = n ^ shift(n, 8);
        final int n3 = n ^ FFmulX(n2);
        final int n4 = n2 ^ FFmulX2(n3);
        return n3 ^ (n4 ^ shift(n4, 16));
    }
    
    private static int subWord(final int n) {
        return (AESLightEngine.S[n & 0xFF] & 0xFF) | (AESLightEngine.S[n >> 8 & 0xFF] & 0xFF) << 8 | (AESLightEngine.S[n >> 16 & 0xFF] & 0xFF) << 16 | AESLightEngine.S[n >> 24 & 0xFF] << 24;
    }
    
    private int[][] generateWorkingKey(final byte[] array, final boolean b) {
        final int length = array.length;
        if (length < 16 || length > 32 || (length & 0x7) != 0x0) {
            throw new IllegalArgumentException("Key length not 128/192/256 bits.");
        }
        final int n = length >> 2;
        this.ROUNDS = n + 6;
        final int[][] array2 = new int[this.ROUNDS + 1][4];
        switch (n) {
            case 4: {
                int littleEndianToInt = Pack.littleEndianToInt(array, 0);
                array2[0][0] = littleEndianToInt;
                int littleEndianToInt2 = Pack.littleEndianToInt(array, 4);
                array2[0][1] = littleEndianToInt2;
                int littleEndianToInt3 = Pack.littleEndianToInt(array, 8);
                array2[0][2] = littleEndianToInt3;
                int littleEndianToInt4 = Pack.littleEndianToInt(array, 12);
                array2[0][3] = littleEndianToInt4;
                for (int i = 1; i <= 10; ++i) {
                    littleEndianToInt ^= (subWord(shift(littleEndianToInt4, 8)) ^ AESLightEngine.rcon[i - 1]);
                    array2[i][0] = littleEndianToInt;
                    littleEndianToInt2 ^= littleEndianToInt;
                    array2[i][1] = littleEndianToInt2;
                    littleEndianToInt3 ^= littleEndianToInt2;
                    array2[i][2] = littleEndianToInt3;
                    littleEndianToInt4 ^= littleEndianToInt3;
                    array2[i][3] = littleEndianToInt4;
                }
                break;
            }
            case 6: {
                final int littleEndianToInt5 = Pack.littleEndianToInt(array, 0);
                array2[0][0] = littleEndianToInt5;
                final int littleEndianToInt6 = Pack.littleEndianToInt(array, 4);
                array2[0][1] = littleEndianToInt6;
                final int littleEndianToInt7 = Pack.littleEndianToInt(array, 8);
                array2[0][2] = littleEndianToInt7;
                final int littleEndianToInt8 = Pack.littleEndianToInt(array, 12);
                array2[0][3] = littleEndianToInt8;
                final int littleEndianToInt9 = Pack.littleEndianToInt(array, 16);
                array2[1][0] = littleEndianToInt9;
                final int littleEndianToInt10 = Pack.littleEndianToInt(array, 20);
                array2[1][1] = littleEndianToInt10;
                final int n2 = 1;
                final int n3 = subWord(shift(littleEndianToInt10, 8)) ^ n2;
                int n4 = n2 << 1;
                int n5 = littleEndianToInt5 ^ n3;
                array2[1][2] = n5;
                int n6 = littleEndianToInt6 ^ n5;
                array2[1][3] = n6;
                int n7 = littleEndianToInt7 ^ n6;
                array2[2][0] = n7;
                int n8 = littleEndianToInt8 ^ n7;
                array2[2][1] = n8;
                int n9 = littleEndianToInt9 ^ n8;
                array2[2][2] = n9;
                int n10 = littleEndianToInt10 ^ n9;
                array2[2][3] = n10;
                for (int j = 3; j < 12; j += 3) {
                    final int n11 = subWord(shift(n10, 8)) ^ n4;
                    final int n12 = n4 << 1;
                    final int n13 = n5 ^ n11;
                    array2[j][0] = n13;
                    final int n14 = n6 ^ n13;
                    array2[j][1] = n14;
                    final int n15 = n7 ^ n14;
                    array2[j][2] = n15;
                    final int n16 = n8 ^ n15;
                    array2[j][3] = n16;
                    final int n17 = n9 ^ n16;
                    array2[j + 1][0] = n17;
                    final int n18 = n10 ^ n17;
                    array2[j + 1][1] = n18;
                    final int n19 = subWord(shift(n18, 8)) ^ n12;
                    n4 = n12 << 1;
                    n5 = (n13 ^ n19);
                    array2[j + 1][2] = n5;
                    n6 = (n14 ^ n5);
                    array2[j + 1][3] = n6;
                    n7 = (n15 ^ n6);
                    array2[j + 2][0] = n7;
                    n8 = (n16 ^ n7);
                    array2[j + 2][1] = n8;
                    n9 = (n17 ^ n8);
                    array2[j + 2][2] = n9;
                    n10 = (n18 ^ n9);
                    array2[j + 2][3] = n10;
                }
                final int n20 = n5 ^ (subWord(shift(n10, 8)) ^ n4);
                array2[12][0] = n20;
                final int n21 = n6 ^ n20;
                array2[12][1] = n21;
                final int n22 = n7 ^ n21;
                array2[12][2] = n22;
                array2[12][3] = (n8 ^ n22);
                break;
            }
            case 8: {
                int littleEndianToInt11 = Pack.littleEndianToInt(array, 0);
                array2[0][0] = littleEndianToInt11;
                int littleEndianToInt12 = Pack.littleEndianToInt(array, 4);
                array2[0][1] = littleEndianToInt12;
                int littleEndianToInt13 = Pack.littleEndianToInt(array, 8);
                array2[0][2] = littleEndianToInt13;
                int littleEndianToInt14 = Pack.littleEndianToInt(array, 12);
                array2[0][3] = littleEndianToInt14;
                int littleEndianToInt15 = Pack.littleEndianToInt(array, 16);
                array2[1][0] = littleEndianToInt15;
                int littleEndianToInt16 = Pack.littleEndianToInt(array, 20);
                array2[1][1] = littleEndianToInt16;
                int littleEndianToInt17 = Pack.littleEndianToInt(array, 24);
                array2[1][2] = littleEndianToInt17;
                int littleEndianToInt18 = Pack.littleEndianToInt(array, 28);
                array2[1][3] = littleEndianToInt18;
                int n23 = 1;
                for (int k = 2; k < 14; k += 2) {
                    final int n24 = subWord(shift(littleEndianToInt18, 8)) ^ n23;
                    n23 <<= 1;
                    littleEndianToInt11 ^= n24;
                    array2[k][0] = littleEndianToInt11;
                    littleEndianToInt12 ^= littleEndianToInt11;
                    array2[k][1] = littleEndianToInt12;
                    littleEndianToInt13 ^= littleEndianToInt12;
                    array2[k][2] = littleEndianToInt13;
                    littleEndianToInt14 ^= littleEndianToInt13;
                    array2[k][3] = littleEndianToInt14;
                    littleEndianToInt15 ^= subWord(littleEndianToInt14);
                    array2[k + 1][0] = littleEndianToInt15;
                    littleEndianToInt16 ^= littleEndianToInt15;
                    array2[k + 1][1] = littleEndianToInt16;
                    littleEndianToInt17 ^= littleEndianToInt16;
                    array2[k + 1][2] = littleEndianToInt17;
                    littleEndianToInt18 ^= littleEndianToInt17;
                    array2[k + 1][3] = littleEndianToInt18;
                }
                final int n25 = littleEndianToInt11 ^ (subWord(shift(littleEndianToInt18, 8)) ^ n23);
                array2[14][0] = n25;
                final int n26 = littleEndianToInt12 ^ n25;
                array2[14][1] = n26;
                final int n27 = littleEndianToInt13 ^ n26;
                array2[14][2] = n27;
                array2[14][3] = (littleEndianToInt14 ^ n27);
                break;
            }
            default: {
                throw new IllegalStateException("Should never get here");
            }
        }
        if (!b) {
            for (int l = 1; l < this.ROUNDS; ++l) {
                for (int n28 = 0; n28 < 4; ++n28) {
                    array2[l][n28] = inv_mcol(array2[l][n28]);
                }
            }
        }
        return array2;
    }
    
    public AESLightEngine() {
        this.WorkingKey = null;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        if (cipherParameters instanceof KeyParameter) {
            this.WorkingKey = this.generateWorkingKey(((KeyParameter)cipherParameters).getKey(), forEncryption);
            this.forEncryption = forEncryption;
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to AES init - " + cipherParameters.getClass().getName());
    }
    
    public String getAlgorithmName() {
        return "AES";
    }
    
    public int getBlockSize() {
        return 16;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (this.WorkingKey == null) {
            throw new IllegalStateException("AES engine not initialised");
        }
        if (n + 16 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 16 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            this.unpackBlock(array, n);
            this.encryptBlock(this.WorkingKey);
            this.packBlock(array2, n2);
        }
        else {
            this.unpackBlock(array, n);
            this.decryptBlock(this.WorkingKey);
            this.packBlock(array2, n2);
        }
        return 16;
    }
    
    public void reset() {
    }
    
    private void unpackBlock(final byte[] array, final int n) {
        int n2 = n;
        this.C0 = (array[n2++] & 0xFF);
        this.C0 |= (array[n2++] & 0xFF) << 8;
        this.C0 |= (array[n2++] & 0xFF) << 16;
        this.C0 |= array[n2++] << 24;
        this.C1 = (array[n2++] & 0xFF);
        this.C1 |= (array[n2++] & 0xFF) << 8;
        this.C1 |= (array[n2++] & 0xFF) << 16;
        this.C1 |= array[n2++] << 24;
        this.C2 = (array[n2++] & 0xFF);
        this.C2 |= (array[n2++] & 0xFF) << 8;
        this.C2 |= (array[n2++] & 0xFF) << 16;
        this.C2 |= array[n2++] << 24;
        this.C3 = (array[n2++] & 0xFF);
        this.C3 |= (array[n2++] & 0xFF) << 8;
        this.C3 |= (array[n2++] & 0xFF) << 16;
        this.C3 |= array[n2++] << 24;
    }
    
    private void packBlock(final byte[] array, final int n) {
        int n2 = n;
        array[n2++] = (byte)this.C0;
        array[n2++] = (byte)(this.C0 >> 8);
        array[n2++] = (byte)(this.C0 >> 16);
        array[n2++] = (byte)(this.C0 >> 24);
        array[n2++] = (byte)this.C1;
        array[n2++] = (byte)(this.C1 >> 8);
        array[n2++] = (byte)(this.C1 >> 16);
        array[n2++] = (byte)(this.C1 >> 24);
        array[n2++] = (byte)this.C2;
        array[n2++] = (byte)(this.C2 >> 8);
        array[n2++] = (byte)(this.C2 >> 16);
        array[n2++] = (byte)(this.C2 >> 24);
        array[n2++] = (byte)this.C3;
        array[n2++] = (byte)(this.C3 >> 8);
        array[n2++] = (byte)(this.C3 >> 16);
        array[n2++] = (byte)(this.C3 >> 24);
    }
    
    private void encryptBlock(final int[][] array) {
        int n;
        int n2;
        int n3;
        int i;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        for (n = (this.C0 ^ array[0][0]), n2 = (this.C1 ^ array[0][1]), n3 = (this.C2 ^ array[0][2]), i = 1, n4 = (this.C3 ^ array[0][3]); i < this.ROUNDS - 1; n8 = (mcol((AESLightEngine.S[n4 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n2 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n3 >> 24 & 0xFF] << 24) ^ array[i++][3]), n = (mcol((AESLightEngine.S[n5 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n6 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n7 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n8 >> 24 & 0xFF] << 24) ^ array[i][0]), n2 = (mcol((AESLightEngine.S[n6 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n7 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n8 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n5 >> 24 & 0xFF] << 24) ^ array[i][1]), n3 = (mcol((AESLightEngine.S[n7 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n8 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n5 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n6 >> 24 & 0xFF] << 24) ^ array[i][2]), n4 = (mcol((AESLightEngine.S[n8 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n5 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n6 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n7 >> 24 & 0xFF] << 24) ^ array[i++][3])) {
            n5 = (mcol((AESLightEngine.S[n & 0xFF] & 0xFF) ^ (AESLightEngine.S[n2 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n3 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n4 >> 24 & 0xFF] << 24) ^ array[i][0]);
            n6 = (mcol((AESLightEngine.S[n2 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n3 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n4 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n >> 24 & 0xFF] << 24) ^ array[i][1]);
            n7 = (mcol((AESLightEngine.S[n3 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n4 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n2 >> 24 & 0xFF] << 24) ^ array[i][2]);
        }
        final int n9 = mcol((AESLightEngine.S[n & 0xFF] & 0xFF) ^ (AESLightEngine.S[n2 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n3 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n4 >> 24 & 0xFF] << 24) ^ array[i][0];
        final int n10 = mcol((AESLightEngine.S[n2 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n3 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n4 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n >> 24 & 0xFF] << 24) ^ array[i][1];
        final int n11 = mcol((AESLightEngine.S[n3 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n4 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n2 >> 24 & 0xFF] << 24) ^ array[i][2];
        final int n12 = mcol((AESLightEngine.S[n4 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n2 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n3 >> 24 & 0xFF] << 24) ^ array[i++][3];
        this.C0 = ((AESLightEngine.S[n9 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n10 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n11 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n12 >> 24 & 0xFF] << 24 ^ array[i][0]);
        this.C1 = ((AESLightEngine.S[n10 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n11 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n12 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n9 >> 24 & 0xFF] << 24 ^ array[i][1]);
        this.C2 = ((AESLightEngine.S[n11 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n12 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n9 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n10 >> 24 & 0xFF] << 24 ^ array[i][2]);
        this.C3 = ((AESLightEngine.S[n12 & 0xFF] & 0xFF) ^ (AESLightEngine.S[n9 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.S[n10 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.S[n11 >> 24 & 0xFF] << 24 ^ array[i][3]);
    }
    
    private void decryptBlock(final int[][] array) {
        int n;
        int n2;
        int n3;
        int i;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        for (n = (this.C0 ^ array[this.ROUNDS][0]), n2 = (this.C1 ^ array[this.ROUNDS][1]), n3 = (this.C2 ^ array[this.ROUNDS][2]), i = this.ROUNDS - 1, n4 = (this.C3 ^ array[this.ROUNDS][3]); i > 1; n8 = (inv_mcol((AESLightEngine.Si[n4 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n3 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n2 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n >> 24 & 0xFF] << 24) ^ array[i--][3]), n = (inv_mcol((AESLightEngine.Si[n5 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n8 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n7 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n6 >> 24 & 0xFF] << 24) ^ array[i][0]), n2 = (inv_mcol((AESLightEngine.Si[n6 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n5 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n8 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n7 >> 24 & 0xFF] << 24) ^ array[i][1]), n3 = (inv_mcol((AESLightEngine.Si[n7 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n6 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n5 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n8 >> 24 & 0xFF] << 24) ^ array[i][2]), n4 = (inv_mcol((AESLightEngine.Si[n8 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n7 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n6 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n5 >> 24 & 0xFF] << 24) ^ array[i--][3])) {
            n5 = (inv_mcol((AESLightEngine.Si[n & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n4 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n3 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n2 >> 24 & 0xFF] << 24) ^ array[i][0]);
            n6 = (inv_mcol((AESLightEngine.Si[n2 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n4 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n3 >> 24 & 0xFF] << 24) ^ array[i][1]);
            n7 = (inv_mcol((AESLightEngine.Si[n3 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n2 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n4 >> 24 & 0xFF] << 24) ^ array[i][2]);
        }
        final int n9 = inv_mcol((AESLightEngine.Si[n & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n4 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n3 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n2 >> 24 & 0xFF] << 24) ^ array[i][0];
        final int n10 = inv_mcol((AESLightEngine.Si[n2 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n4 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n3 >> 24 & 0xFF] << 24) ^ array[i][1];
        final int n11 = inv_mcol((AESLightEngine.Si[n3 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n2 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n4 >> 24 & 0xFF] << 24) ^ array[i][2];
        final int n12 = inv_mcol((AESLightEngine.Si[n4 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n3 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n2 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n >> 24 & 0xFF] << 24) ^ array[i][3];
        this.C0 = ((AESLightEngine.Si[n9 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n12 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n11 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n10 >> 24 & 0xFF] << 24 ^ array[0][0]);
        this.C1 = ((AESLightEngine.Si[n10 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n9 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n12 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n11 >> 24 & 0xFF] << 24 ^ array[0][1]);
        this.C2 = ((AESLightEngine.Si[n11 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n10 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n9 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n12 >> 24 & 0xFF] << 24 ^ array[0][2]);
        this.C3 = ((AESLightEngine.Si[n12 & 0xFF] & 0xFF) ^ (AESLightEngine.Si[n11 >> 8 & 0xFF] & 0xFF) << 8 ^ (AESLightEngine.Si[n10 >> 16 & 0xFF] & 0xFF) << 16 ^ AESLightEngine.Si[n9 >> 24 & 0xFF] << 24 ^ array[0][3]);
    }
    
    static {
        S = new byte[] { 99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118, -54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, -64, -73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, -15, 113, -40, 49, 21, 4, -57, 35, -61, 24, -106, 5, -102, 7, 18, -128, -30, -21, 39, -78, 117, 9, -125, 44, 26, 27, 110, 90, -96, 82, 59, -42, -77, 41, -29, 47, -124, 83, -47, 0, -19, 32, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49, -48, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, 127, 80, 60, -97, -88, 81, -93, 64, -113, -110, -99, 56, -11, -68, -74, -38, 33, 16, -1, -13, -46, -51, 12, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115, 96, -127, 79, -36, 34, 42, -112, -120, 70, -18, -72, 20, -34, 94, 11, -37, -32, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121, -25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8, -70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118, 112, 62, -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, -122, -63, 29, -98, -31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33, -116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, 15, -80, 84, -69, 22 };
        Si = new byte[] { 82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, -13, -41, -5, 124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53, 84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78, 8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37, 114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110, 108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124, -112, -40, -85, 0, -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6, -48, 44, 30, -113, -54, 63, 15, 2, -63, -81, -67, 3, 1, 19, -118, 107, 58, -111, 17, 65, 79, 103, -36, -22, -105, -14, -49, -50, -16, -76, -26, 115, -106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110, 71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 98, 14, -86, 24, -66, 27, -4, 86, 62, 75, -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 90, -12, 31, -35, -88, 51, -120, 7, -57, 49, -79, 18, 16, 89, 39, -128, -20, 95, 96, 81, 127, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17, -96, -32, 59, 77, -82, 42, -11, -80, -56, -21, -69, 60, -125, 83, -103, 97, 23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 20, 99, 85, 33, 12, 125 };
        rcon = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 27, 54, 108, 216, 171, 77, 154, 47, 94, 188, 99, 198, 151, 53, 106, 212, 179, 125, 250, 239, 197, 145 };
    }
}
