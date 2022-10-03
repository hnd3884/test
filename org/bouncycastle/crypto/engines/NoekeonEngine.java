package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class NoekeonEngine implements BlockCipher
{
    private static final int genericSize = 16;
    private static final int[] nullVector;
    private static final int[] roundConstants;
    private int[] state;
    private int[] subKeys;
    private int[] decryptKeys;
    private boolean _initialised;
    private boolean _forEncryption;
    
    public NoekeonEngine() {
        this.state = new int[4];
        this.subKeys = new int[4];
        this.decryptKeys = new int[4];
        this._initialised = false;
    }
    
    public String getAlgorithmName() {
        return "Noekeon";
    }
    
    public int getBlockSize() {
        return 16;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to Noekeon init - " + cipherParameters.getClass().getName());
        }
        this._forEncryption = forEncryption;
        this._initialised = true;
        this.setKey(((KeyParameter)cipherParameters).getKey());
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (!this._initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (n + 16 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 16 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        return this._forEncryption ? this.encryptBlock(array, n, array2, n2) : this.decryptBlock(array, n, array2, n2);
    }
    
    public void reset() {
    }
    
    private void setKey(final byte[] array) {
        this.subKeys[0] = this.bytesToIntBig(array, 0);
        this.subKeys[1] = this.bytesToIntBig(array, 4);
        this.subKeys[2] = this.bytesToIntBig(array, 8);
        this.subKeys[3] = this.bytesToIntBig(array, 12);
    }
    
    private int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        this.state[0] = this.bytesToIntBig(array, n);
        this.state[1] = this.bytesToIntBig(array, n + 4);
        this.state[2] = this.bytesToIntBig(array, n + 8);
        this.state[3] = this.bytesToIntBig(array, n + 12);
        int i;
        for (i = 0; i < 16; ++i) {
            final int[] state = this.state;
            final int n3 = 0;
            state[n3] ^= NoekeonEngine.roundConstants[i];
            this.theta(this.state, this.subKeys);
            this.pi1(this.state);
            this.gamma(this.state);
            this.pi2(this.state);
        }
        final int[] state2 = this.state;
        final int n4 = 0;
        state2[n4] ^= NoekeonEngine.roundConstants[i];
        this.theta(this.state, this.subKeys);
        this.intToBytesBig(this.state[0], array2, n2);
        this.intToBytesBig(this.state[1], array2, n2 + 4);
        this.intToBytesBig(this.state[2], array2, n2 + 8);
        this.intToBytesBig(this.state[3], array2, n2 + 12);
        return 16;
    }
    
    private int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        this.state[0] = this.bytesToIntBig(array, n);
        this.state[1] = this.bytesToIntBig(array, n + 4);
        this.state[2] = this.bytesToIntBig(array, n + 8);
        this.state[3] = this.bytesToIntBig(array, n + 12);
        System.arraycopy(this.subKeys, 0, this.decryptKeys, 0, this.subKeys.length);
        this.theta(this.decryptKeys, NoekeonEngine.nullVector);
        int i;
        for (i = 16; i > 0; --i) {
            this.theta(this.state, this.decryptKeys);
            final int[] state = this.state;
            final int n3 = 0;
            state[n3] ^= NoekeonEngine.roundConstants[i];
            this.pi1(this.state);
            this.gamma(this.state);
            this.pi2(this.state);
        }
        this.theta(this.state, this.decryptKeys);
        final int[] state2 = this.state;
        final int n4 = 0;
        state2[n4] ^= NoekeonEngine.roundConstants[i];
        this.intToBytesBig(this.state[0], array2, n2);
        this.intToBytesBig(this.state[1], array2, n2 + 4);
        this.intToBytesBig(this.state[2], array2, n2 + 8);
        this.intToBytesBig(this.state[3], array2, n2 + 12);
        return 16;
    }
    
    private void gamma(final int[] array) {
        final int n = 1;
        array[n] ^= (~array[3] & ~array[2]);
        final int n2 = 0;
        array[n2] ^= (array[2] & array[1]);
        final int n3 = array[3];
        array[3] = array[0];
        array[0] = n3;
        final int n4 = 2;
        array[n4] ^= (array[0] ^ array[1] ^ array[3]);
        final int n5 = 1;
        array[n5] ^= (~array[3] & ~array[2]);
        final int n6 = 0;
        array[n6] ^= (array[2] & array[1]);
    }
    
    private void theta(final int[] array, final int[] array2) {
        final int n = array[0] ^ array[2];
        final int n2 = n ^ (this.rotl(n, 8) ^ this.rotl(n, 24));
        final int n3 = 1;
        array[n3] ^= n2;
        final int n4 = 3;
        array[n4] ^= n2;
        for (int i = 0; i < 4; ++i) {
            final int n5 = i;
            array[n5] ^= array2[i];
        }
        final int n6 = array[1] ^ array[3];
        final int n7 = n6 ^ (this.rotl(n6, 8) ^ this.rotl(n6, 24));
        final int n8 = 0;
        array[n8] ^= n7;
        final int n9 = 2;
        array[n9] ^= n7;
    }
    
    private void pi1(final int[] array) {
        array[1] = this.rotl(array[1], 1);
        array[2] = this.rotl(array[2], 5);
        array[3] = this.rotl(array[3], 2);
    }
    
    private void pi2(final int[] array) {
        array[1] = this.rotl(array[1], 31);
        array[2] = this.rotl(array[2], 27);
        array[3] = this.rotl(array[3], 30);
    }
    
    private int bytesToIntBig(final byte[] array, int n) {
        return array[n++] << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n] & 0xFF);
    }
    
    private void intToBytesBig(final int n, final byte[] array, int n2) {
        array[n2++] = (byte)(n >>> 24);
        array[n2++] = (byte)(n >>> 16);
        array[n2++] = (byte)(n >>> 8);
        array[n2] = (byte)n;
    }
    
    private int rotl(final int n, final int n2) {
        return n << n2 | n >>> 32 - n2;
    }
    
    static {
        nullVector = new int[] { 0, 0, 0, 0 };
        roundConstants = new int[] { 128, 27, 54, 108, 216, 171, 77, 154, 47, 94, 188, 99, 198, 151, 53, 106, 212 };
    }
}
