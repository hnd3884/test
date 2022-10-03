package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;

public class OldHMac implements Mac
{
    private static final int BLOCK_LENGTH = 64;
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private Digest digest;
    private int digestSize;
    private byte[] inputPad;
    private byte[] outputPad;
    
    @Deprecated
    public OldHMac(final Digest digest) {
        this.inputPad = new byte[64];
        this.outputPad = new byte[64];
        this.digest = digest;
        this.digestSize = digest.getDigestSize();
    }
    
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "/HMAC";
    }
    
    public Digest getUnderlyingDigest() {
        return this.digest;
    }
    
    public void init(final CipherParameters cipherParameters) {
        this.digest.reset();
        final byte[] key = ((KeyParameter)cipherParameters).getKey();
        if (key.length > 64) {
            this.digest.update(key, 0, key.length);
            this.digest.doFinal(this.inputPad, 0);
            for (int i = this.digestSize; i < this.inputPad.length; ++i) {
                this.inputPad[i] = 0;
            }
        }
        else {
            System.arraycopy(key, 0, this.inputPad, 0, key.length);
            for (int j = key.length; j < this.inputPad.length; ++j) {
                this.inputPad[j] = 0;
            }
        }
        this.outputPad = new byte[this.inputPad.length];
        System.arraycopy(this.inputPad, 0, this.outputPad, 0, this.inputPad.length);
        for (int k = 0; k < this.inputPad.length; ++k) {
            final byte[] inputPad = this.inputPad;
            final int n = k;
            inputPad[n] ^= 0x36;
        }
        for (int l = 0; l < this.outputPad.length; ++l) {
            final byte[] outputPad = this.outputPad;
            final int n2 = l;
            outputPad[n2] ^= 0x5C;
        }
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }
    
    public int getMacSize() {
        return this.digestSize;
    }
    
    public void update(final byte b) {
        this.digest.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.digest.update(array, n, n2);
    }
    
    public int doFinal(final byte[] array, final int n) {
        final byte[] array2 = new byte[this.digestSize];
        this.digest.doFinal(array2, 0);
        this.digest.update(this.outputPad, 0, this.outputPad.length);
        this.digest.update(array2, 0, array2.length);
        final int doFinal = this.digest.doFinal(array, n);
        this.reset();
        return doFinal;
    }
    
    public void reset() {
        this.digest.reset();
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }
}
