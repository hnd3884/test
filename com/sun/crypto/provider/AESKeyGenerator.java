package com.sun.crypto.provider;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import javax.crypto.KeyGeneratorSpi;

public final class AESKeyGenerator extends KeyGeneratorSpi
{
    private SecureRandom random;
    private int keySize;
    
    public AESKeyGenerator() {
        this.random = null;
        this.keySize = 16;
    }
    
    @Override
    protected void engineInit(final SecureRandom random) {
        this.random = random;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("AES key generation does not take any parameters");
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        if (n % 8 != 0 || !AESCrypt.isKeySizeValid(n / 8)) {
            throw new InvalidParameterException("Wrong keysize: must be equal to 128, 192 or 256");
        }
        this.keySize = n / 8;
        this.engineInit(secureRandom);
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.random == null) {
            this.random = SunJCE.getRandom();
        }
        final byte[] array = new byte[this.keySize];
        this.random.nextBytes(array);
        return new SecretKeySpec(array, "AES");
    }
}
