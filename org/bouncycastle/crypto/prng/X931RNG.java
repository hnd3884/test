package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.BlockCipher;

public class X931RNG
{
    private static final long BLOCK64_RESEED_MAX = 32768L;
    private static final long BLOCK128_RESEED_MAX = 8388608L;
    private static final int BLOCK64_MAX_BITS_REQUEST = 4096;
    private static final int BLOCK128_MAX_BITS_REQUEST = 262144;
    private final BlockCipher engine;
    private final EntropySource entropySource;
    private final byte[] DT;
    private final byte[] I;
    private final byte[] R;
    private byte[] V;
    private long reseedCounter;
    
    public X931RNG(final BlockCipher engine, final byte[] array, final EntropySource entropySource) {
        this.reseedCounter = 1L;
        this.engine = engine;
        this.entropySource = entropySource;
        System.arraycopy(array, 0, this.DT = new byte[engine.getBlockSize()], 0, this.DT.length);
        this.I = new byte[engine.getBlockSize()];
        this.R = new byte[engine.getBlockSize()];
    }
    
    int generate(final byte[] array, final boolean b) {
        if (this.R.length == 8) {
            if (this.reseedCounter > 32768L) {
                return -1;
            }
            if (isTooLarge(array, 512)) {
                throw new IllegalArgumentException("Number of bits per request limited to 4096");
            }
        }
        else {
            if (this.reseedCounter > 8388608L) {
                return -1;
            }
            if (isTooLarge(array, 32768)) {
                throw new IllegalArgumentException("Number of bits per request limited to 262144");
            }
        }
        if (b || this.V == null) {
            this.V = this.entropySource.getEntropy();
            if (this.V.length != this.engine.getBlockSize()) {
                throw new IllegalStateException("Insufficient entropy returned");
            }
        }
        final int n = array.length / this.R.length;
        for (int i = 0; i < n; ++i) {
            this.engine.processBlock(this.DT, 0, this.I, 0);
            this.process(this.R, this.I, this.V);
            this.process(this.V, this.R, this.I);
            System.arraycopy(this.R, 0, array, i * this.R.length, this.R.length);
            this.increment(this.DT);
        }
        final int n2 = array.length - n * this.R.length;
        if (n2 > 0) {
            this.engine.processBlock(this.DT, 0, this.I, 0);
            this.process(this.R, this.I, this.V);
            this.process(this.V, this.R, this.I);
            System.arraycopy(this.R, 0, array, n * this.R.length, n2);
            this.increment(this.DT);
        }
        ++this.reseedCounter;
        return array.length;
    }
    
    void reseed() {
        this.V = this.entropySource.getEntropy();
        if (this.V.length != this.engine.getBlockSize()) {
            throw new IllegalStateException("Insufficient entropy returned");
        }
        this.reseedCounter = 1L;
    }
    
    EntropySource getEntropySource() {
        return this.entropySource;
    }
    
    private void process(final byte[] array, final byte[] array2, final byte[] array3) {
        for (int i = 0; i != array.length; ++i) {
            array[i] = (byte)(array2[i] ^ array3[i]);
        }
        this.engine.processBlock(array, 0, array, 0);
    }
    
    private void increment(final byte[] array) {
        for (int i = array.length - 1; i >= 0; --i) {
            final int n = i;
            if (++array[n] != 0) {
                break;
            }
        }
    }
    
    private static boolean isTooLarge(final byte[] array, final int n) {
        return array != null && array.length > n;
    }
}
