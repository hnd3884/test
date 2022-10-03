package org.bouncycastle.crypto.macs;

import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.DSTU7564Digest;
import org.bouncycastle.crypto.Mac;

public class DSTU7564Mac implements Mac
{
    private static final int BITS_IN_BYTE = 8;
    private DSTU7564Digest engine;
    private int macSize;
    private byte[] paddedKey;
    private byte[] invertedKey;
    private long inputLength;
    
    public DSTU7564Mac(final int n) {
        this.engine = new DSTU7564Digest(n);
        this.macSize = n / 8;
        this.paddedKey = null;
        this.invertedKey = null;
    }
    
    public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (cipherParameters instanceof KeyParameter) {
            final byte[] key = ((KeyParameter)cipherParameters).getKey();
            this.invertedKey = new byte[key.length];
            this.paddedKey = this.padKey(key);
            for (int i = 0; i < this.invertedKey.length; ++i) {
                this.invertedKey[i] = (byte)~key[i];
            }
            this.engine.update(this.paddedKey, 0, this.paddedKey.length);
            return;
        }
        throw new IllegalArgumentException("Bad parameter passed");
    }
    
    public String getAlgorithmName() {
        return "DSTU7564Mac";
    }
    
    public int getMacSize() {
        return this.macSize;
    }
    
    public void update(final byte b) throws IllegalStateException {
        this.engine.update(b);
        ++this.inputLength;
    }
    
    public void update(final byte[] array, final int n, final int n2) throws DataLengthException, IllegalStateException {
        if (array.length - n < n2) {
            throw new DataLengthException("Input buffer too short");
        }
        if (this.paddedKey == null) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        this.engine.update(array, n, n2);
        this.inputLength += n2;
    }
    
    public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        if (this.paddedKey == null) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (array.length - n < this.macSize) {
            throw new OutputLengthException("Output buffer too short");
        }
        this.pad();
        this.engine.update(this.invertedKey, 0, this.invertedKey.length);
        this.inputLength = 0L;
        return this.engine.doFinal(array, n);
    }
    
    public void reset() {
        this.inputLength = 0L;
        this.engine.reset();
        if (this.paddedKey != null) {
            this.engine.update(this.paddedKey, 0, this.paddedKey.length);
        }
    }
    
    private void pad() {
        int n = this.engine.getByteLength() - (int)(this.inputLength % this.engine.getByteLength());
        if (n < 13) {
            n += this.engine.getByteLength();
        }
        final byte[] array = new byte[n];
        array[0] = -128;
        Pack.longToLittleEndian(this.inputLength * 8L, array, array.length - 12);
        this.engine.update(array, 0, array.length);
    }
    
    private byte[] padKey(final byte[] array) {
        int n = (array.length + this.engine.getByteLength() - 1) / this.engine.getByteLength() * this.engine.getByteLength();
        if (this.engine.getByteLength() - array.length % this.engine.getByteLength() < 13) {
            n += this.engine.getByteLength();
        }
        final byte[] array2 = new byte[n];
        System.arraycopy(array, 0, array2, 0, array.length);
        array2[array.length] = -128;
        Pack.intToLittleEndian(array.length * 8, array2, array2.length - 12);
        return array2;
    }
}
