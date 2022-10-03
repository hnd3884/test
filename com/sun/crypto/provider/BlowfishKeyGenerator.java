package com.sun.crypto.provider;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import javax.crypto.KeyGeneratorSpi;

public final class BlowfishKeyGenerator extends KeyGeneratorSpi
{
    private SecureRandom random;
    private int keysize;
    
    public BlowfishKeyGenerator() {
        this.random = null;
        this.keysize = 16;
    }
    
    @Override
    protected void engineInit(final SecureRandom random) {
        this.random = random;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("Blowfish key generation does not take any parameters");
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        if (n % 8 != 0 || n < 32 || n > 448) {
            throw new InvalidParameterException("Keysize must be multiple of 8, and can only range from 32 to 448 (inclusive)");
        }
        this.keysize = n / 8;
        this.engineInit(secureRandom);
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.random == null) {
            this.random = SunJCE.getRandom();
        }
        final byte[] array = new byte[this.keysize];
        this.random.nextBytes(array);
        return new SecretKeySpec(array, "Blowfish");
    }
}
