package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class XTEAEngine implements BlockCipher
{
    private static final int rounds = 32;
    private static final int block_size = 8;
    private static final int delta = -1640531527;
    private int[] _S;
    private int[] _sum0;
    private int[] _sum1;
    private boolean _initialised;
    private boolean _forEncryption;
    
    public XTEAEngine() {
        this._S = new int[4];
        this._sum0 = new int[32];
        this._sum1 = new int[32];
        this._initialised = false;
    }
    
    public String getAlgorithmName() {
        return "XTEA";
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to TEA init - " + cipherParameters.getClass().getName());
        }
        this._forEncryption = forEncryption;
        this._initialised = true;
        this.setKey(((KeyParameter)cipherParameters).getKey());
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (!this._initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (n + 8 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 8 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        return this._forEncryption ? this.encryptBlock(array, n, array2, n2) : this.decryptBlock(array, n, array2, n2);
    }
    
    public void reset() {
    }
    
    private void setKey(final byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Key size must be 128 bits.");
        }
        int i;
        for (int n = i = 0; i < 4; ++i, n += 4) {
            this._S[i] = this.bytesToInt(array, n);
        }
        int j;
        for (int n2 = j = 0; j < 32; ++j) {
            this._sum0[j] = n2 + this._S[n2 & 0x3];
            n2 -= 1640531527;
            this._sum1[j] = n2 + this._S[n2 >>> 11 & 0x3];
        }
    }
    
    private int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int bytesToInt = this.bytesToInt(array, n);
        int bytesToInt2 = this.bytesToInt(array, n + 4);
        for (int i = 0; i < 32; ++i) {
            bytesToInt += ((bytesToInt2 << 4 ^ bytesToInt2 >>> 5) + bytesToInt2 ^ this._sum0[i]);
            bytesToInt2 += ((bytesToInt << 4 ^ bytesToInt >>> 5) + bytesToInt ^ this._sum1[i]);
        }
        this.unpackInt(bytesToInt, array2, n2);
        this.unpackInt(bytesToInt2, array2, n2 + 4);
        return 8;
    }
    
    private int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int bytesToInt = this.bytesToInt(array, n);
        int bytesToInt2 = this.bytesToInt(array, n + 4);
        for (int i = 31; i >= 0; --i) {
            bytesToInt2 -= ((bytesToInt << 4 ^ bytesToInt >>> 5) + bytesToInt ^ this._sum1[i]);
            bytesToInt -= ((bytesToInt2 << 4 ^ bytesToInt2 >>> 5) + bytesToInt2 ^ this._sum0[i]);
        }
        this.unpackInt(bytesToInt, array2, n2);
        this.unpackInt(bytesToInt2, array2, n2 + 4);
        return 8;
    }
    
    private int bytesToInt(final byte[] array, int n) {
        return array[n++] << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n] & 0xFF);
    }
    
    private void unpackInt(final int n, final byte[] array, int n2) {
        array[n2++] = (byte)(n >>> 24);
        array[n2++] = (byte)(n >>> 16);
        array[n2++] = (byte)(n >>> 8);
        array[n2] = (byte)n;
    }
}
