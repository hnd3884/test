package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class IDEAEngine implements BlockCipher
{
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey;
    private static final int MASK = 65535;
    private static final int BASE = 65537;
    
    public IDEAEngine() {
        this.workingKey = null;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (cipherParameters instanceof KeyParameter) {
            this.workingKey = this.generateWorkingKey(b, ((KeyParameter)cipherParameters).getKey());
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to IDEA init - " + cipherParameters.getClass().getName());
    }
    
    public String getAlgorithmName() {
        return "IDEA";
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (this.workingKey == null) {
            throw new IllegalStateException("IDEA engine not initialised");
        }
        if (n + 8 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 8 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.ideaFunc(this.workingKey, array, n, array2, n2);
        return 8;
    }
    
    public void reset() {
    }
    
    private int bytesToWord(final byte[] array, final int n) {
        return (array[n] << 8 & 0xFF00) + (array[n + 1] & 0xFF);
    }
    
    private void wordToBytes(final int n, final byte[] array, final int n2) {
        array[n2] = (byte)(n >>> 8);
        array[n2 + 1] = (byte)n;
    }
    
    private int mul(int n, int n2) {
        if (n == 0) {
            n = 65537 - n2;
        }
        else if (n2 == 0) {
            n = 65537 - n;
        }
        else {
            final int n3 = n * n2;
            n2 = (n3 & 0xFFFF);
            n = n3 >>> 16;
            n = n2 - n + ((n2 < n) ? 1 : 0);
        }
        return n & 0xFFFF;
    }
    
    private void ideaFunc(final int[] array, final byte[] array2, final int n, final byte[] array3, final int n2) {
        int n3 = 0;
        int bytesToWord = this.bytesToWord(array2, n);
        int bytesToWord2 = this.bytesToWord(array2, n + 2);
        int bytesToWord3 = this.bytesToWord(array2, n + 4);
        int bytesToWord4 = this.bytesToWord(array2, n + 6);
        for (int i = 0; i < 8; ++i) {
            final int mul = this.mul(bytesToWord, array[n3++]);
            final int n4 = bytesToWord2 + array[n3++] & 0xFFFF;
            final int n5 = bytesToWord3 + array[n3++] & 0xFFFF;
            final int mul2 = this.mul(bytesToWord4, array[n3++]);
            final int n6 = n4;
            final int n7 = n5;
            final int n8 = n5 ^ mul;
            final int n9 = n4 ^ mul2;
            final int mul3 = this.mul(n8, array[n3++]);
            final int mul4 = this.mul(n9 + mul3 & 0xFFFF, array[n3++]);
            final int n10 = mul3 + mul4 & 0xFFFF;
            bytesToWord = (mul ^ mul4);
            bytesToWord4 = (mul2 ^ n10);
            bytesToWord2 = (mul4 ^ n7);
            bytesToWord3 = (n10 ^ n6);
        }
        this.wordToBytes(this.mul(bytesToWord, array[n3++]), array3, n2);
        this.wordToBytes(bytesToWord3 + array[n3++], array3, n2 + 2);
        this.wordToBytes(bytesToWord2 + array[n3++], array3, n2 + 4);
        this.wordToBytes(this.mul(bytesToWord4, array[n3]), array3, n2 + 6);
    }
    
    private int[] expandKey(byte[] array) {
        final int[] array2 = new int[52];
        if (array.length < 16) {
            final byte[] array3 = new byte[16];
            System.arraycopy(array, 0, array3, array3.length - array.length, array.length);
            array = array3;
        }
        for (int i = 0; i < 8; ++i) {
            array2[i] = this.bytesToWord(array, i * 2);
        }
        for (int j = 8; j < 52; ++j) {
            if ((j & 0x7) < 6) {
                array2[j] = (((array2[j - 7] & 0x7F) << 9 | array2[j - 6] >> 7) & 0xFFFF);
            }
            else if ((j & 0x7) == 0x6) {
                array2[j] = (((array2[j - 7] & 0x7F) << 9 | array2[j - 14] >> 7) & 0xFFFF);
            }
            else {
                array2[j] = (((array2[j - 15] & 0x7F) << 9 | array2[j - 14] >> 7) & 0xFFFF);
            }
        }
        return array2;
    }
    
    private int mulInv(int n) {
        if (n < 2) {
            return n;
        }
        int n2 = 1;
        int n3 = 65537 / n;
        int n5;
        for (int i = 65537 % n; i != 1; i %= n, n3 = (n3 + n2 * n5 & 0xFFFF)) {
            final int n4 = n / i;
            n %= i;
            n2 = (n2 + n3 * n4 & 0xFFFF);
            if (n == 1) {
                return n2;
            }
            n5 = i / n;
        }
        return 1 - n3 & 0xFFFF;
    }
    
    int addInv(final int n) {
        return 0 - n & 0xFFFF;
    }
    
    private int[] invertKey(final int[] array) {
        int n = 52;
        final int[] array2 = new int[52];
        int n2 = 0;
        final int mulInv = this.mulInv(array[n2++]);
        final int addInv = this.addInv(array[n2++]);
        final int addInv2 = this.addInv(array[n2++]);
        array2[--n] = this.mulInv(array[n2++]);
        array2[--n] = addInv2;
        array2[--n] = addInv;
        array2[--n] = mulInv;
        for (int i = 1; i < 8; ++i) {
            final int n3 = array[n2++];
            array2[--n] = array[n2++];
            array2[--n] = n3;
            final int mulInv2 = this.mulInv(array[n2++]);
            final int addInv3 = this.addInv(array[n2++]);
            final int addInv4 = this.addInv(array[n2++]);
            array2[--n] = this.mulInv(array[n2++]);
            array2[--n] = addInv3;
            array2[--n] = addInv4;
            array2[--n] = mulInv2;
        }
        final int n4 = array[n2++];
        array2[--n] = array[n2++];
        array2[--n] = n4;
        final int mulInv3 = this.mulInv(array[n2++]);
        final int addInv5 = this.addInv(array[n2++]);
        final int addInv6 = this.addInv(array[n2++]);
        array2[--n] = this.mulInv(array[n2]);
        array2[--n] = addInv6;
        array2[--n] = addInv5;
        array2[--n] = mulInv3;
        return array2;
    }
    
    private int[] generateWorkingKey(final boolean b, final byte[] array) {
        if (b) {
            return this.expandKey(array);
        }
        return this.invertKey(this.expandKey(array));
    }
}
