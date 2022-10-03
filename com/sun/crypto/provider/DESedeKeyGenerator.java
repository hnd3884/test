package com.sun.crypto.provider;

import java.security.InvalidKeyException;
import java.util.Arrays;
import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import javax.crypto.KeyGeneratorSpi;

public final class DESedeKeyGenerator extends KeyGeneratorSpi
{
    private SecureRandom random;
    private int keysize;
    
    public DESedeKeyGenerator() {
        this.random = null;
        this.keysize = 168;
    }
    
    @Override
    protected void engineInit(final SecureRandom random) {
        this.random = random;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("Triple DES key generation does not take any parameters");
    }
    
    @Override
    protected void engineInit(final int keysize, final SecureRandom secureRandom) {
        if (keysize != 112 && keysize != 168) {
            throw new InvalidParameterException("Wrong keysize: must be equal to 112 or 168");
        }
        this.keysize = keysize;
        this.engineInit(secureRandom);
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.random == null) {
            this.random = SunJCE.getRandom();
        }
        final byte[] array = new byte[24];
        if (this.keysize == 168) {
            this.random.nextBytes(array);
            DESKeyGenerator.setParityBit(array, 0);
            DESKeyGenerator.setParityBit(array, 8);
            DESKeyGenerator.setParityBit(array, 16);
        }
        else {
            final byte[] array2 = new byte[16];
            this.random.nextBytes(array2);
            DESKeyGenerator.setParityBit(array2, 0);
            DESKeyGenerator.setParityBit(array2, 8);
            System.arraycopy(array2, 0, array, 0, array2.length);
            System.arraycopy(array2, 0, array, 16, 8);
            Arrays.fill(array2, (byte)0);
        }
        DESedeKey deSedeKey;
        try {
            deSedeKey = new DESedeKey(array);
        }
        catch (final InvalidKeyException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        Arrays.fill(array, (byte)0);
        return deSedeKey;
    }
}
