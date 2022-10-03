package com.sun.crypto.provider;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.ProviderException;
import java.security.KeyPair;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.provider.ParameterCache;
import java.security.InvalidParameterException;
import sun.security.util.SecurityProviderConstants;
import java.security.SecureRandom;
import javax.crypto.spec.DHParameterSpec;
import java.security.KeyPairGeneratorSpi;

public final class DHKeyPairGenerator extends KeyPairGeneratorSpi
{
    private DHParameterSpec params;
    private int pSize;
    private int lSize;
    private SecureRandom random;
    
    public DHKeyPairGenerator() {
        this.initialize(SecurityProviderConstants.DEF_DH_KEY_SIZE, null);
    }
    
    private static void checkKeySize(final int n) throws InvalidParameterException {
        if (n < 512 || n > 8192 || (n & 0x3F) != 0x0) {
            throw new InvalidParameterException("DH key size must be multiple of 64, and can only range from 512 to 8192 (inclusive). The specific key size " + n + " is not supported");
        }
    }
    
    @Override
    public void initialize(final int pSize, final SecureRandom random) {
        checkKeySize(pSize);
        this.params = ParameterCache.getCachedDHParameterSpec(pSize);
        if (this.params == null && pSize > 1024) {
            throw new InvalidParameterException("Unsupported " + pSize + "-bit DH parameter generation");
        }
        this.pSize = pSize;
        this.lSize = 0;
        this.random = random;
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof DHParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Inappropriate parameter type");
        }
        this.params = (DHParameterSpec)algorithmParameterSpec;
        this.pSize = this.params.getP().bitLength();
        try {
            checkKeySize(this.pSize);
        }
        catch (final InvalidParameterException ex) {
            throw new InvalidAlgorithmParameterException(ex.getMessage());
        }
        this.lSize = this.params.getL();
        if (this.lSize != 0 && this.lSize > this.pSize) {
            throw new InvalidAlgorithmParameterException("Exponent size must not be larger than modulus size");
        }
        this.random = random;
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (this.random == null) {
            this.random = SunJCE.getRandom();
        }
        if (this.params == null) {
            try {
                this.params = ParameterCache.getDHParameterSpec(this.pSize, this.random);
            }
            catch (final GeneralSecurityException ex) {
                throw new ProviderException(ex);
            }
        }
        final BigInteger p = this.params.getP();
        final BigInteger g = this.params.getG();
        if (this.lSize <= 0) {
            this.lSize = this.pSize >> 1;
            if (this.lSize < 384) {
                this.lSize = 384;
            }
        }
        final BigInteger subtract = p.subtract(BigInteger.valueOf(2L));
        BigInteger bigInteger;
        do {
            bigInteger = new BigInteger(this.lSize, this.random);
        } while (bigInteger.compareTo(BigInteger.ONE) < 0 || bigInteger.compareTo(subtract) > 0 || bigInteger.bitLength() != this.lSize);
        return new KeyPair(new DHPublicKey(g.modPow(bigInteger, p), p, g, this.lSize), new DHPrivateKey(bigInteger, p, g, this.lSize));
    }
}
