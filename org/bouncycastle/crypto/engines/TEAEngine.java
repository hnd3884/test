package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class TEAEngine implements BlockCipher
{
    private static final int rounds = 32;
    private static final int block_size = 8;
    private static final int delta = -1640531527;
    private static final int d_sum = -957401312;
    private int _a;
    private int _b;
    private int _c;
    private int _d;
    private boolean _initialised;
    private boolean _forEncryption;
    
    public TEAEngine() {
        this._initialised = false;
    }
    
    public String getAlgorithmName() {
        return "TEA";
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
        this._a = this.bytesToInt(array, 0);
        this._b = this.bytesToInt(array, 4);
        this._c = this.bytesToInt(array, 8);
        this._d = this.bytesToInt(array, 12);
    }
    
    private int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int bytesToInt = this.bytesToInt(array, n);
        int bytesToInt2 = this.bytesToInt(array, n + 4);
        int n3 = 0;
        for (int i = 0; i != 32; ++i) {
            n3 -= 1640531527;
            bytesToInt += ((bytesToInt2 << 4) + this._a ^ bytesToInt2 + n3 ^ (bytesToInt2 >>> 5) + this._b);
            bytesToInt2 += ((bytesToInt << 4) + this._c ^ bytesToInt + n3 ^ (bytesToInt >>> 5) + this._d);
        }
        this.unpackInt(bytesToInt, array2, n2);
        this.unpackInt(bytesToInt2, array2, n2 + 4);
        return 8;
    }
    
    private int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int bytesToInt = this.bytesToInt(array, n);
        int bytesToInt2 = this.bytesToInt(array, n + 4);
        int n3 = -957401312;
        for (int i = 0; i != 32; ++i) {
            bytesToInt2 -= ((bytesToInt << 4) + this._c ^ bytesToInt + n3 ^ (bytesToInt >>> 5) + this._d);
            bytesToInt -= ((bytesToInt2 << 4) + this._a ^ bytesToInt2 + n3 ^ (bytesToInt2 >>> 5) + this._b);
            n3 += 1640531527;
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
