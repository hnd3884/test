package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;

public class VMPCMac implements Mac
{
    private byte g;
    private byte n;
    private byte[] P;
    private byte s;
    private byte[] T;
    private byte[] workingIV;
    private byte[] workingKey;
    private byte x1;
    private byte x2;
    private byte x3;
    private byte x4;
    
    public VMPCMac() {
        this.n = 0;
        this.P = null;
        this.s = 0;
    }
    
    public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        for (int i = 1; i < 25; ++i) {
            this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
            this.x4 = this.P[this.x4 + this.x3 + i & 0xFF];
            this.x3 = this.P[this.x3 + this.x2 + i & 0xFF];
            this.x2 = this.P[this.x2 + this.x1 + i & 0xFF];
            this.x1 = this.P[this.x1 + this.s + i & 0xFF];
            this.T[this.g & 0x1F] ^= this.x1;
            this.T[this.g + 1 & 0x1F] ^= this.x2;
            this.T[this.g + 2 & 0x1F] ^= this.x3;
            this.T[this.g + 3 & 0x1F] ^= this.x4;
            this.g = (byte)(this.g + 4 & 0x1F);
            final byte b = this.P[this.n & 0xFF];
            this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = b;
            this.n = (byte)(this.n + 1 & 0xFF);
        }
        for (int j = 0; j < 768; ++j) {
            this.s = this.P[this.s + this.P[j & 0xFF] + this.T[j & 0x1F] & 0xFF];
            final byte b2 = this.P[j & 0xFF];
            this.P[j & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = b2;
        }
        final byte[] array2 = new byte[20];
        for (int k = 0; k < 20; ++k) {
            this.s = this.P[this.s + this.P[k & 0xFF] & 0xFF];
            array2[k] = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
            final byte b3 = this.P[k & 0xFF];
            this.P[k & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = b3;
        }
        System.arraycopy(array2, 0, array, n, array2.length);
        this.reset();
        return array2.length;
    }
    
    public String getAlgorithmName() {
        return "VMPC-MAC";
    }
    
    public int getMacSize() {
        return 20;
    }
    
    public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("VMPC-MAC Init parameters must include an IV");
        }
        final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
        final KeyParameter keyParameter = (KeyParameter)parametersWithIV.getParameters();
        if (!(parametersWithIV.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("VMPC-MAC Init parameters must include a key");
        }
        this.workingIV = parametersWithIV.getIV();
        if (this.workingIV == null || this.workingIV.length < 1 || this.workingIV.length > 768) {
            throw new IllegalArgumentException("VMPC-MAC requires 1 to 768 bytes of IV");
        }
        this.workingKey = keyParameter.getKey();
        this.reset();
    }
    
    private void initKey(final byte[] array, final byte[] array2) {
        this.s = 0;
        this.P = new byte[256];
        for (int i = 0; i < 256; ++i) {
            this.P[i] = (byte)i;
        }
        for (int j = 0; j < 768; ++j) {
            this.s = this.P[this.s + this.P[j & 0xFF] + array[j % array.length] & 0xFF];
            final byte b = this.P[j & 0xFF];
            this.P[j & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = b;
        }
        for (int k = 0; k < 768; ++k) {
            this.s = this.P[this.s + this.P[k & 0xFF] + array2[k % array2.length] & 0xFF];
            final byte b2 = this.P[k & 0xFF];
            this.P[k & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = b2;
        }
        this.n = 0;
    }
    
    public void reset() {
        this.initKey(this.workingKey, this.workingIV);
        final byte b = 0;
        this.n = b;
        this.x4 = b;
        this.x3 = b;
        this.x2 = b;
        this.x1 = b;
        this.g = b;
        this.T = new byte[32];
        for (int i = 0; i < 32; ++i) {
            this.T[i] = 0;
        }
    }
    
    public void update(final byte b) throws IllegalStateException {
        this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
        final byte b2 = (byte)(b ^ this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF]);
        this.x4 = this.P[this.x4 + this.x3 & 0xFF];
        this.x3 = this.P[this.x3 + this.x2 & 0xFF];
        this.x2 = this.P[this.x2 + this.x1 & 0xFF];
        this.x1 = this.P[this.x1 + this.s + b2 & 0xFF];
        this.T[this.g & 0x1F] ^= this.x1;
        this.T[this.g + 1 & 0x1F] ^= this.x2;
        this.T[this.g + 2 & 0x1F] ^= this.x3;
        this.T[this.g + 3 & 0x1F] ^= this.x4;
        this.g = (byte)(this.g + 4 & 0x1F);
        final byte b3 = this.P[this.n & 0xFF];
        this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
        this.P[this.s & 0xFF] = b3;
        this.n = (byte)(this.n + 1 & 0xFF);
    }
    
    public void update(final byte[] array, final int n, final int n2) throws DataLengthException, IllegalStateException {
        if (n + n2 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        for (int i = 0; i < n2; ++i) {
            this.update(array[n + i]);
        }
    }
}
