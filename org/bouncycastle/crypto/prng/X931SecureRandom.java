package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;

public class X931SecureRandom extends SecureRandom
{
    private final boolean predictionResistant;
    private final SecureRandom randomSource;
    private final X931RNG drbg;
    
    X931SecureRandom(final SecureRandom randomSource, final X931RNG drbg, final boolean predictionResistant) {
        this.randomSource = randomSource;
        this.drbg = drbg;
        this.predictionResistant = predictionResistant;
    }
    
    @Override
    public void setSeed(final byte[] seed) {
        synchronized (this) {
            if (this.randomSource != null) {
                this.randomSource.setSeed(seed);
            }
        }
    }
    
    @Override
    public void setSeed(final long seed) {
        synchronized (this) {
            if (this.randomSource != null) {
                this.randomSource.setSeed(seed);
            }
        }
    }
    
    @Override
    public void nextBytes(final byte[] array) {
        synchronized (this) {
            if (this.drbg.generate(array, this.predictionResistant) < 0) {
                this.drbg.reseed();
                this.drbg.generate(array, this.predictionResistant);
            }
        }
    }
    
    @Override
    public byte[] generateSeed(final int n) {
        return EntropyUtil.generateSeed(this.drbg.getEntropySource(), n);
    }
}
