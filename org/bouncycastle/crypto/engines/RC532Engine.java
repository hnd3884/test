package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RC5Parameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class RC532Engine implements BlockCipher
{
    private int _noRounds;
    private int[] _S;
    private static final int P32 = -1209970333;
    private static final int Q32 = -1640531527;
    private boolean forEncryption;
    
    public RC532Engine() {
        this._noRounds = 12;
        this._S = null;
    }
    
    public String getAlgorithmName() {
        return "RC5-32";
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        if (cipherParameters instanceof RC5Parameters) {
            final RC5Parameters rc5Parameters = (RC5Parameters)cipherParameters;
            this._noRounds = rc5Parameters.getRounds();
            this.setKey(rc5Parameters.getKey());
        }
        else {
            if (!(cipherParameters instanceof KeyParameter)) {
                throw new IllegalArgumentException("invalid parameter passed to RC532 init - " + cipherParameters.getClass().getName());
            }
            this.setKey(((KeyParameter)cipherParameters).getKey());
        }
        this.forEncryption = forEncryption;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        return this.forEncryption ? this.encryptBlock(array, n, array2, n2) : this.decryptBlock(array, n, array2, n2);
    }
    
    public void reset() {
    }
    
    private void setKey(final byte[] array) {
        final int[] array2 = new int[(array.length + 3) / 4];
        for (int i = 0; i != array.length; ++i) {
            final int[] array3 = array2;
            final int n = i / 4;
            array3[n] += (array[i] & 0xFF) << 8 * (i % 4);
        }
        (this._S = new int[2 * (this._noRounds + 1)])[0] = -1209970333;
        for (int j = 1; j < this._S.length; ++j) {
            this._S[j] = this._S[j - 1] - 1640531527;
        }
        int n2;
        if (array2.length > this._S.length) {
            n2 = 3 * array2.length;
        }
        else {
            n2 = 3 * this._S.length;
        }
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        for (int k = 0; k < n2; ++k) {
            final int[] s = this._S;
            final int n7 = n5;
            final int rotateLeft = this.rotateLeft(this._S[n5] + n3 + n4, 3);
            s[n7] = rotateLeft;
            n3 = rotateLeft;
            final int[] array4 = array2;
            final int n8 = n6;
            final int rotateLeft2 = this.rotateLeft(array2[n6] + n3 + n4, n3 + n4);
            array4[n8] = rotateLeft2;
            n4 = rotateLeft2;
            n5 = (n5 + 1) % this._S.length;
            n6 = (n6 + 1) % array2.length;
        }
    }
    
    private int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int n3 = this.bytesToWord(array, n) + this._S[0];
        int n4 = this.bytesToWord(array, n + 4) + this._S[1];
        for (int i = 1; i <= this._noRounds; ++i) {
            n3 = this.rotateLeft(n3 ^ n4, n4) + this._S[2 * i];
            n4 = this.rotateLeft(n4 ^ n3, n3) + this._S[2 * i + 1];
        }
        this.wordToBytes(n3, array2, n2);
        this.wordToBytes(n4, array2, n2 + 4);
        return 8;
    }
    
    private int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int bytesToWord = this.bytesToWord(array, n);
        int bytesToWord2 = this.bytesToWord(array, n + 4);
        for (int i = this._noRounds; i >= 1; --i) {
            bytesToWord2 = (this.rotateRight(bytesToWord2 - this._S[2 * i + 1], bytesToWord) ^ bytesToWord);
            bytesToWord = (this.rotateRight(bytesToWord - this._S[2 * i], bytesToWord2) ^ bytesToWord2);
        }
        this.wordToBytes(bytesToWord - this._S[0], array2, n2);
        this.wordToBytes(bytesToWord2 - this._S[1], array2, n2 + 4);
        return 8;
    }
    
    private int rotateLeft(final int n, final int n2) {
        return n << (n2 & 0x1F) | n >>> 32 - (n2 & 0x1F);
    }
    
    private int rotateRight(final int n, final int n2) {
        return n >>> (n2 & 0x1F) | n << 32 - (n2 & 0x1F);
    }
    
    private int bytesToWord(final byte[] array, final int n) {
        return (array[n] & 0xFF) | (array[n + 1] & 0xFF) << 8 | (array[n + 2] & 0xFF) << 16 | (array[n + 3] & 0xFF) << 24;
    }
    
    private void wordToBytes(final int n, final byte[] array, final int n2) {
        array[n2] = (byte)n;
        array[n2 + 1] = (byte)(n >> 8);
        array[n2 + 2] = (byte)(n >> 16);
        array[n2 + 3] = (byte)(n >> 24);
    }
}
