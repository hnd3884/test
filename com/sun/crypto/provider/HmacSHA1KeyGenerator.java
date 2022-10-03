package com.sun.crypto.provider;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import javax.crypto.KeyGeneratorSpi;

public final class HmacSHA1KeyGenerator extends KeyGeneratorSpi
{
    private SecureRandom random;
    private int keysize;
    
    public HmacSHA1KeyGenerator() {
        this.random = null;
        this.keysize = 64;
    }
    
    @Override
    protected void engineInit(final SecureRandom random) {
        this.random = random;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("HMAC-SHA1 key generation does not take any parameters");
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        this.keysize = (n + 7) / 8;
        this.engineInit(secureRandom);
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.random == null) {
            this.random = SunJCE.getRandom();
        }
        final byte[] array = new byte[this.keysize];
        this.random.nextBytes(array);
        return new SecretKeySpec(array, "HmacSHA1");
    }
}
