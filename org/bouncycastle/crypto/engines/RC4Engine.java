package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;

public class RC4Engine implements StreamCipher
{
    private static final int STATE_LENGTH = 256;
    private byte[] engineState;
    private int x;
    private int y;
    private byte[] workingKey;
    
    public RC4Engine() {
        this.engineState = null;
        this.x = 0;
        this.y = 0;
        this.workingKey = null;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (cipherParameters instanceof KeyParameter) {
            this.setKey(this.workingKey = ((KeyParameter)cipherParameters).getKey());
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to RC4 init - " + cipherParameters.getClass().getName());
    }
    
    public String getAlgorithmName() {
        return "RC4";
    }
    
    public byte returnByte(final byte b) {
        this.x = (this.x + 1 & 0xFF);
        this.y = (this.engineState[this.x] + this.y & 0xFF);
        final byte b2 = this.engineState[this.x];
        this.engineState[this.x] = this.engineState[this.y];
        this.engineState[this.y] = b2;
        return (byte)(b ^ this.engineState[this.engineState[this.x] + this.engineState[this.y] & 0xFF]);
    }
    
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (n + n2 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n3 + n2 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < n2; ++i) {
            this.x = (this.x + 1 & 0xFF);
            this.y = (this.engineState[this.x] + this.y & 0xFF);
            final byte b = this.engineState[this.x];
            this.engineState[this.x] = this.engineState[this.y];
            this.engineState[this.y] = b;
            array2[i + n3] = (byte)(array[i + n] ^ this.engineState[this.engineState[this.x] + this.engineState[this.y] & 0xFF]);
        }
        return n2;
    }
    
    public void reset() {
        this.setKey(this.workingKey);
    }
    
    private void setKey(final byte[] workingKey) {
        this.workingKey = workingKey;
        this.x = 0;
        this.y = 0;
        if (this.engineState == null) {
            this.engineState = new byte[256];
        }
        for (int i = 0; i < 256; ++i) {
            this.engineState[i] = (byte)i;
        }
        int n = 0;
        int n2 = 0;
        for (int j = 0; j < 256; ++j) {
            n2 = ((workingKey[n] & 0xFF) + this.engineState[j] + n2 & 0xFF);
            final byte b = this.engineState[j];
            this.engineState[j] = this.engineState[n2];
            this.engineState[n2] = b;
            n = (n + 1) % workingKey.length;
        }
    }
}
