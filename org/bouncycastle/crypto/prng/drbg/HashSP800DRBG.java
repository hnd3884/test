package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.Digest;
import java.util.Hashtable;

public class HashSP800DRBG implements SP80090DRBG
{
    private static final byte[] ONE;
    private static final long RESEED_MAX = 140737488355328L;
    private static final int MAX_BITS_REQUEST = 262144;
    private static final Hashtable seedlens;
    private Digest _digest;
    private byte[] _V;
    private byte[] _C;
    private long _reseedCounter;
    private EntropySource _entropySource;
    private int _securityStrength;
    private int _seedLength;
    
    public HashSP800DRBG(final Digest digest, final int securityStrength, final EntropySource entropySource, final byte[] array, final byte[] array2) {
        if (securityStrength > Utils.getMaxSecurityStrength(digest)) {
            throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
        }
        if (entropySource.entropySize() < securityStrength) {
            throw new IllegalArgumentException("Not enough entropy for security strength required");
        }
        this._digest = digest;
        this._entropySource = entropySource;
        this._securityStrength = securityStrength;
        this._seedLength = HashSP800DRBG.seedlens.get(digest.getAlgorithmName());
        this._V = Utils.hash_df(this._digest, Arrays.concatenate(this.getEntropy(), array2, array), this._seedLength);
        final byte[] array3 = new byte[this._V.length + 1];
        System.arraycopy(this._V, 0, array3, 1, this._V.length);
        this._C = Utils.hash_df(this._digest, array3, this._seedLength);
        this._reseedCounter = 1L;
    }
    
    public int getBlockSize() {
        return this._digest.getDigestSize() * 8;
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
            final byte[] array3 = new byte[1 + this._V.length + array2.length];
            array3[0] = 2;
            System.arraycopy(this._V, 0, array3, 1, this._V.length);
            System.arraycopy(array2, 0, array3, 1 + this._V.length, array2.length);
            this.addTo(this._V, this.hash(array3));
        }
        final byte[] hashgen = this.hashgen(this._V, n);
        final byte[] array4 = new byte[this._V.length + 1];
        System.arraycopy(this._V, 0, array4, 1, this._V.length);
        array4[0] = 3;
        this.addTo(this._V, this.hash(array4));
        this.addTo(this._V, this._C);
        this.addTo(this._V, new byte[] { (byte)(this._reseedCounter >> 24), (byte)(this._reseedCounter >> 16), (byte)(this._reseedCounter >> 8), (byte)this._reseedCounter });
        ++this._reseedCounter;
        System.arraycopy(hashgen, 0, array, 0, array.length);
        return n;
    }
    
    private byte[] getEntropy() {
        final byte[] entropy = this._entropySource.getEntropy();
        if (entropy.length < (this._securityStrength + 7) / 8) {
            throw new IllegalStateException("Insufficient entropy provided by entropy source");
        }
        return entropy;
    }
    
    private void addTo(final byte[] array, final byte[] array2) {
        int n = 0;
        for (int i = 1; i <= array2.length; ++i) {
            final int n2 = (array[array.length - i] & 0xFF) + (array2[array2.length - i] & 0xFF) + n;
            n = ((n2 > 255) ? 1 : 0);
            array[array.length - i] = (byte)n2;
        }
        for (int j = array2.length + 1; j <= array.length; ++j) {
            final int n3 = (array[array.length - j] & 0xFF) + n;
            n = ((n3 > 255) ? 1 : 0);
            array[array.length - j] = (byte)n3;
        }
    }
    
    public void reseed(final byte[] array) {
        this._V = Utils.hash_df(this._digest, Arrays.concatenate(HashSP800DRBG.ONE, this._V, this.getEntropy(), array), this._seedLength);
        final byte[] array2 = new byte[this._V.length + 1];
        array2[0] = 0;
        System.arraycopy(this._V, 0, array2, 1, this._V.length);
        this._C = Utils.hash_df(this._digest, array2, this._seedLength);
        this._reseedCounter = 1L;
    }
    
    private byte[] hash(final byte[] array) {
        final byte[] array2 = new byte[this._digest.getDigestSize()];
        this.doHash(array, array2);
        return array2;
    }
    
    private void doHash(final byte[] array, final byte[] array2) {
        this._digest.update(array, 0, array.length);
        this._digest.doFinal(array2, 0);
    }
    
    private byte[] hashgen(final byte[] array, final int n) {
        final int n2 = n / 8 / this._digest.getDigestSize();
        final byte[] array2 = new byte[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        final byte[] array3 = new byte[n / 8];
        final byte[] array4 = new byte[this._digest.getDigestSize()];
        for (int i = 0; i <= n2; ++i) {
            this.doHash(array2, array4);
            System.arraycopy(array4, 0, array3, i * array4.length, (array3.length - i * array4.length > array4.length) ? array4.length : (array3.length - i * array4.length));
            this.addTo(array2, HashSP800DRBG.ONE);
        }
        return array3;
    }
    
    static {
        ONE = new byte[] { 1 };
        (seedlens = new Hashtable()).put("SHA-1", Integers.valueOf(440));
        HashSP800DRBG.seedlens.put("SHA-224", Integers.valueOf(440));
        HashSP800DRBG.seedlens.put("SHA-256", Integers.valueOf(440));
        HashSP800DRBG.seedlens.put("SHA-512/256", Integers.valueOf(440));
        HashSP800DRBG.seedlens.put("SHA-512/224", Integers.valueOf(440));
        HashSP800DRBG.seedlens.put("SHA-384", Integers.valueOf(888));
        HashSP800DRBG.seedlens.put("SHA-512", Integers.valueOf(888));
    }
}
