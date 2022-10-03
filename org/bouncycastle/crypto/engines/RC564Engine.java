package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.params.RC5Parameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class RC564Engine implements BlockCipher
{
    private static final int wordSize = 64;
    private static final int bytesPerWord = 8;
    private int _noRounds;
    private long[] _S;
    private static final long P64 = -5196783011329398165L;
    private static final long Q64 = -7046029254386353131L;
    private boolean forEncryption;
    
    public RC564Engine() {
        this._noRounds = 12;
        this._S = null;
    }
    
    public String getAlgorithmName() {
        return "RC5-64";
    }
    
    public int getBlockSize() {
        return 16;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof RC5Parameters)) {
            throw new IllegalArgumentException("invalid parameter passed to RC564 init - " + cipherParameters.getClass().getName());
        }
        final RC5Parameters rc5Parameters = (RC5Parameters)cipherParameters;
        this.forEncryption = forEncryption;
        this._noRounds = rc5Parameters.getRounds();
        this.setKey(rc5Parameters.getKey());
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        return this.forEncryption ? this.encryptBlock(array, n, array2, n2) : this.decryptBlock(array, n, array2, n2);
    }
    
    public void reset() {
    }
    
    private void setKey(final byte[] array) {
        final long[] array2 = new long[(array.length + 7) / 8];
        for (int i = 0; i != array.length; ++i) {
            final long[] array3 = array2;
            final int n = i / 8;
            array3[n] += (long)(array[i] & 0xFF) << 8 * (i % 8);
        }
        (this._S = new long[2 * (this._noRounds + 1)])[0] = -5196783011329398165L;
        for (int j = 1; j < this._S.length; ++j) {
            this._S[j] = this._S[j - 1] - 7046029254386353131L;
        }
        int n2;
        if (array2.length > this._S.length) {
            n2 = 3 * array2.length;
        }
        else {
            n2 = 3 * this._S.length;
        }
        long n3 = 0L;
        long n4 = 0L;
        int n5 = 0;
        int n6 = 0;
        for (int k = 0; k < n2; ++k) {
            final long[] s = this._S;
            final int n7 = n5;
            final long rotateLeft = this.rotateLeft(this._S[n5] + n3 + n4, 3L);
            s[n7] = rotateLeft;
            n3 = rotateLeft;
            final long[] array4 = array2;
            final int n8 = n6;
            final long rotateLeft2 = this.rotateLeft(array2[n6] + n3 + n4, n3 + n4);
            array4[n8] = rotateLeft2;
            n4 = rotateLeft2;
            n5 = (n5 + 1) % this._S.length;
            n6 = (n6 + 1) % array2.length;
        }
    }
    
    private int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        long n3 = this.bytesToWord(array, n) + this._S[0];
        long n4 = this.bytesToWord(array, n + 8) + this._S[1];
        for (int i = 1; i <= this._noRounds; ++i) {
            n3 = this.rotateLeft(n3 ^ n4, n4) + this._S[2 * i];
            n4 = this.rotateLeft(n4 ^ n3, n3) + this._S[2 * i + 1];
        }
        this.wordToBytes(n3, array2, n2);
        this.wordToBytes(n4, array2, n2 + 8);
        return 16;
    }
    
    private int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        long bytesToWord = this.bytesToWord(array, n);
        long bytesToWord2 = this.bytesToWord(array, n + 8);
        for (int i = this._noRounds; i >= 1; --i) {
            bytesToWord2 = (this.rotateRight(bytesToWord2 - this._S[2 * i + 1], bytesToWord) ^ bytesToWord);
            bytesToWord = (this.rotateRight(bytesToWord - this._S[2 * i], bytesToWord2) ^ bytesToWord2);
        }
        this.wordToBytes(bytesToWord - this._S[0], array2, n2);
        this.wordToBytes(bytesToWord2 - this._S[1], array2, n2 + 8);
        return 16;
    }
    
    private long rotateLeft(final long n, final long n2) {
        return n << (int)(n2 & 0x3FL) | n >>> (int)(64L - (n2 & 0x3FL));
    }
    
    private long rotateRight(final long n, final long n2) {
        return n >>> (int)(n2 & 0x3FL) | n << (int)(64L - (n2 & 0x3FL));
    }
    
    private long bytesToWord(final byte[] array, final int n) {
        long n2 = 0L;
        for (int i = 7; i >= 0; --i) {
            n2 = (n2 << 8) + (array[i + n] & 0xFF);
        }
        return n2;
    }
    
    private void wordToBytes(long n, final byte[] array, final int n2) {
        for (int i = 0; i < 8; ++i) {
            array[i + n2] = (byte)n;
            n >>>= 8;
        }
    }
}
