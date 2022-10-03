package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;

public class VMPCEngine implements StreamCipher
{
    protected byte n;
    protected byte[] P;
    protected byte s;
    protected byte[] workingIV;
    protected byte[] workingKey;
    
    public VMPCEngine() {
        this.n = 0;
        this.P = null;
        this.s = 0;
    }
    
    public String getAlgorithmName() {
        return "VMPC";
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("VMPC init parameters must include an IV");
        }
        final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
        if (!(parametersWithIV.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("VMPC init parameters must include a key");
        }
        final KeyParameter keyParameter = (KeyParameter)parametersWithIV.getParameters();
        this.workingIV = parametersWithIV.getIV();
        if (this.workingIV == null || this.workingIV.length < 1 || this.workingIV.length > 768) {
            throw new IllegalArgumentException("VMPC requires 1 to 768 bytes of IV");
        }
        this.initKey(this.workingKey = keyParameter.getKey(), this.workingIV);
    }
    
    protected void initKey(final byte[] array, final byte[] array2) {
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
    
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (n + n2 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n3 + n2 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < n2; ++i) {
            this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
            final byte b = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
            final byte b2 = this.P[this.n & 0xFF];
            this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = b2;
            this.n = (byte)(this.n + 1 & 0xFF);
            array2[i + n3] = (byte)(array[i + n] ^ b);
        }
        return n2;
    }
    
    public void reset() {
        this.initKey(this.workingKey, this.workingIV);
    }
    
    public byte returnByte(final byte b) {
        this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
        final byte b2 = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
        final byte b3 = this.P[this.n & 0xFF];
        this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
        this.P[this.s & 0xFF] = b3;
        this.n = (byte)(this.n + 1 & 0xFF);
        return (byte)(b ^ b2);
    }
}
