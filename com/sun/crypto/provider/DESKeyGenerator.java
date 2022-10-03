package com.sun.crypto.provider;

import java.security.InvalidKeyException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import javax.crypto.KeyGeneratorSpi;

public final class DESKeyGenerator extends KeyGeneratorSpi
{
    private SecureRandom random;
    
    public DESKeyGenerator() {
        this.random = null;
    }
    
    @Override
    protected void engineInit(final SecureRandom random) {
        this.random = random;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("DES key generation does not take any parameters");
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        if (n != 56) {
            throw new InvalidParameterException("Wrong keysize: must be equal to 56");
        }
        this.engineInit(secureRandom);
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        SecretKey secretKey = null;
        if (this.random == null) {
            this.random = SunJCE.getRandom();
        }
        try {
            final byte[] array = new byte[8];
            do {
                this.random.nextBytes(array);
                setParityBit(array, 0);
            } while (DESKeySpec.isWeak(array, 0));
            secretKey = new DESKey(array);
        }
        catch (final InvalidKeyException ex) {}
        return secretKey;
    }
    
    static void setParityBit(final byte[] array, int n) {
        if (array == null) {
            return;
        }
        for (int i = 0; i < 8; ++i) {
            final int n2 = array[n] & 0xFE;
            array[n++] = (byte)(n2 | ((Integer.bitCount(n2) & 0x1) ^ 0x1));
        }
    }
}
