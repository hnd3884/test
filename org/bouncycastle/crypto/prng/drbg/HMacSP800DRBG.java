package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.prng.EntropySource;

public class HMacSP800DRBG implements SP80090DRBG
{
    private static final long RESEED_MAX = 140737488355328L;
    private static final int MAX_BITS_REQUEST = 262144;
    private byte[] _K;
    private byte[] _V;
    private long _reseedCounter;
    private EntropySource _entropySource;
    private Mac _hMac;
    private int _securityStrength;
    
    public HMacSP800DRBG(final Mac hMac, final int securityStrength, final EntropySource entropySource, final byte[] array, final byte[] array2) {
        if (securityStrength > Utils.getMaxSecurityStrength(hMac)) {
            throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
        }
        if (entropySource.entropySize() < securityStrength) {
            throw new IllegalArgumentException("Not enough entropy for security strength required");
        }
        this._securityStrength = securityStrength;
        this._entropySource = entropySource;
        this._hMac = hMac;
        final byte[] concatenate = Arrays.concatenate(this.getEntropy(), array2, array);
        this._K = new byte[hMac.getMacSize()];
        Arrays.fill(this._V = new byte[this._K.length], (byte)1);
        this.hmac_DRBG_Update(concatenate);
        this._reseedCounter = 1L;
    }
    
    private void hmac_DRBG_Update(final byte[] array) {
        this.hmac_DRBG_Update_Func(array, (byte)0);
        if (array != null) {
            this.hmac_DRBG_Update_Func(array, (byte)1);
        }
    }
    
    private void hmac_DRBG_Update_Func(final byte[] array, final byte b) {
        this._hMac.init(new KeyParameter(this._K));
        this._hMac.update(this._V, 0, this._V.length);
        this._hMac.update(b);
        if (array != null) {
            this._hMac.update(array, 0, array.length);
        }
        this._hMac.doFinal(this._K, 0);
        this._hMac.init(new KeyParameter(this._K));
        this._hMac.update(this._V, 0, this._V.length);
        this._hMac.doFinal(this._V, 0);
    }
    
    public int getBlockSize() {
        return this._V.length * 8;
    }
    
    public int generate(final byte[] array, byte[] array2, final boolean b) {
        final int n = array.length * 8;
        if (n > 262144) {
            throw new IllegalArgumentException("Number of bits per request limited to 262144");
        }
        if (this._reseedCounter > 140737488355328L) {
            return -1;
        }
        if (b) {
            this.reseed(array2);
            array2 = null;
        }
        if (array2 != null) {
            this.hmac_DRBG_Update(array2);
        }
        final byte[] array3 = new byte[array.length];
        final int n2 = array.length / this._V.length;
        this._hMac.init(new KeyParameter(this._K));
        for (int i = 0; i < n2; ++i) {
            this._hMac.update(this._V, 0, this._V.length);
            this._hMac.doFinal(this._V, 0);
            System.arraycopy(this._V, 0, array3, i * this._V.length, this._V.length);
        }
        if (n2 * this._V.length < array3.length) {
            this._hMac.update(this._V, 0, this._V.length);
            this._hMac.doFinal(this._V, 0);
            System.arraycopy(this._V, 0, array3, n2 * this._V.length, array3.length - n2 * this._V.length);
        }
        this.hmac_DRBG_Update(array2);
        ++this._reseedCounter;
        System.arraycopy(array3, 0, array, 0, array.length);
        return n;
    }
    
    public void reseed(final byte[] array) {
        this.hmac_DRBG_Update(Arrays.concatenate(this.getEntropy(), array));
        this._reseedCounter = 1L;
    }
    
    private byte[] getEntropy() {
        final byte[] entropy = this._entropySource.getEntropy();
        if (entropy.length < (this._securityStrength + 7) / 8) {
            throw new IllegalStateException("Insufficient entropy provided by entropy source");
        }
        return entropy;
    }
}
