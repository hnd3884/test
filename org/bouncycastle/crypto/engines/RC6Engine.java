package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class RC6Engine implements BlockCipher
{
    private static final int wordSize = 32;
    private static final int bytesPerWord = 4;
    private static final int _noRounds = 20;
    private int[] _S;
    private static final int P32 = -1209970333;
    private static final int Q32 = -1640531527;
    private static final int LGW = 5;
    private boolean forEncryption;
    
    public RC6Engine() {
        this._S = null;
    }
    
    public String getAlgorithmName() {
        return "RC6";
    }
    
    public int getBlockSize() {
        return 16;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to RC6 init - " + cipherParameters.getClass().getName());
        }
        final KeyParameter keyParameter = (KeyParameter)cipherParameters;
        this.forEncryption = forEncryption;
        this.setKey(keyParameter.getKey());
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        final int blockSize = this.getBlockSize();
        if (this._S == null) {
            throw new IllegalStateException("RC6 engine not initialised");
        }
        if (n + blockSize > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + blockSize > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        return this.forEncryption ? this.encryptBlock(array, n, array2, n2) : this.decryptBlock(array, n, array2, n2);
    }
    
    public void reset() {
    }
    
    private void setKey(final byte[] array) {
        if ((array.length + 3) / 4 == 0) {}
        final int[] array2 = new int[(array.length + 4 - 1) / 4];
        for (int i = array.length - 1; i >= 0; --i) {
            array2[i / 4] = (array2[i / 4] << 8) + (array[i] & 0xFF);
        }
        (this._S = new int[44])[0] = -1209970333;
        for (int j = 1; j < this._S.length; ++j) {
            this._S[j] = this._S[j - 1] - 1640531527;
        }
        int n;
        if (array2.length > this._S.length) {
            n = 3 * array2.length;
        }
        else {
            n = 3 * this._S.length;
        }
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        for (int k = 0; k < n; ++k) {
            final int[] s = this._S;
            final int n6 = n4;
            final int rotateLeft = this.rotateLeft(this._S[n4] + n2 + n3, 3);
            s[n6] = rotateLeft;
            n2 = rotateLeft;
            final int[] array3 = array2;
            final int n7 = n5;
            final int rotateLeft2 = this.rotateLeft(array2[n5] + n2 + n3, n2 + n3);
            array3[n7] = rotateLeft2;
            n3 = rotateLeft2;
            n4 = (n4 + 1) % this._S.length;
            n5 = (n5 + 1) % array2.length;
        }
    }
    
    private int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int bytesToWord = this.bytesToWord(array, n);
        final int bytesToWord2 = this.bytesToWord(array, n + 4);
        int bytesToWord3 = this.bytesToWord(array, n + 8);
        final int bytesToWord4 = this.bytesToWord(array, n + 12);
        int n3 = bytesToWord2 + this._S[0];
        int n4 = bytesToWord4 + this._S[1];
        for (int i = 1; i <= 20; ++i) {
            final int rotateLeft = this.rotateLeft(n3 * (2 * n3 + 1), 5);
            final int rotateLeft2 = this.rotateLeft(n4 * (2 * n4 + 1), 5);
            final int n5 = this.rotateLeft(bytesToWord ^ rotateLeft, rotateLeft2) + this._S[2 * i];
            final int n6 = this.rotateLeft(bytesToWord3 ^ rotateLeft2, rotateLeft) + this._S[2 * i + 1];
            final int n7 = n5;
            bytesToWord = n3;
            n3 = n6;
            bytesToWord3 = n4;
            n4 = n7;
        }
        final int n8 = bytesToWord + this._S[42];
        final int n9 = bytesToWord3 + this._S[43];
        this.wordToBytes(n8, array2, n2);
        this.wordToBytes(n3, array2, n2 + 4);
        this.wordToBytes(n9, array2, n2 + 8);
        this.wordToBytes(n4, array2, n2 + 12);
        return 16;
    }
    
    private int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        final int bytesToWord = this.bytesToWord(array, n);
        int bytesToWord2 = this.bytesToWord(array, n + 4);
        final int bytesToWord3 = this.bytesToWord(array, n + 8);
        int bytesToWord4 = this.bytesToWord(array, n + 12);
        int n3 = bytesToWord3 - this._S[43];
        int n4 = bytesToWord - this._S[42];
        for (int i = 20; i >= 1; --i) {
            final int n5 = bytesToWord4;
            bytesToWord4 = n3;
            final int n6 = bytesToWord2;
            bytesToWord2 = n4;
            final int n7 = n5;
            final int rotateLeft = this.rotateLeft(bytesToWord2 * (2 * bytesToWord2 + 1), 5);
            final int rotateLeft2 = this.rotateLeft(bytesToWord4 * (2 * bytesToWord4 + 1), 5);
            n3 = (this.rotateRight(n6 - this._S[2 * i + 1], rotateLeft) ^ rotateLeft2);
            n4 = (this.rotateRight(n7 - this._S[2 * i], rotateLeft2) ^ rotateLeft);
        }
        final int n8 = bytesToWord4 - this._S[1];
        final int n9 = bytesToWord2 - this._S[0];
        this.wordToBytes(n4, array2, n2);
        this.wordToBytes(n9, array2, n2 + 4);
        this.wordToBytes(n3, array2, n2 + 8);
        this.wordToBytes(n8, array2, n2 + 12);
        return 16;
    }
    
    private int rotateLeft(final int n, final int n2) {
        return n << n2 | n >>> -n2;
    }
    
    private int rotateRight(final int n, final int n2) {
        return n >>> n2 | n << -n2;
    }
    
    private int bytesToWord(final byte[] array, final int n) {
        int n2 = 0;
        for (int i = 3; i >= 0; --i) {
            n2 = (n2 << 8) + (array[i + n] & 0xFF);
        }
        return n2;
    }
    
    private void wordToBytes(int n, final byte[] array, final int n2) {
        for (int i = 0; i < 4; ++i) {
            array[i + n2] = (byte)n;
            n >>>= 8;
        }
    }
}
