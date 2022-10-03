package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;

public class RSABlindedEngine implements AsymmetricBlockCipher
{
    private static final BigInteger ONE;
    private RSACoreEngine core;
    private RSAKeyParameters key;
    private SecureRandom random;
    
    public RSABlindedEngine() {
        this.core = new RSACoreEngine();
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        this.core.init(b, cipherParameters);
        if (cipherParameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.key = (RSAKeyParameters)parametersWithRandom.getParameters();
            this.random = parametersWithRandom.getRandom();
        }
        else {
            this.key = (RSAKeyParameters)cipherParameters;
            this.random = new SecureRandom();
        }
    }
    
    public int getInputBlockSize() {
        return this.core.getInputBlockSize();
    }
    
    public int getOutputBlockSize() {
        return this.core.getOutputBlockSize();
    }
    
    public byte[] processBlock(final byte[] array, final int n, final int n2) {
        if (this.key == null) {
            throw new IllegalStateException("RSA engine not initialised");
        }
        final BigInteger convertInput = this.core.convertInput(array, n, n2);
        BigInteger bigInteger;
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            final RSAPrivateCrtKeyParameters rsaPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters)this.key;
            final BigInteger publicExponent = rsaPrivateCrtKeyParameters.getPublicExponent();
            if (publicExponent != null) {
                final BigInteger modulus = rsaPrivateCrtKeyParameters.getModulus();
                final BigInteger randomInRange = BigIntegers.createRandomInRange(RSABlindedEngine.ONE, modulus.subtract(RSABlindedEngine.ONE), this.random);
                bigInteger = this.core.processBlock(randomInRange.modPow(publicExponent, modulus).multiply(convertInput).mod(modulus)).multiply(randomInRange.modInverse(modulus)).mod(modulus);
                if (!convertInput.equals(bigInteger.modPow(publicExponent, modulus))) {
                    throw new IllegalStateException("RSA engine faulty decryption/signing detected");
                }
            }
            else {
                bigInteger = this.core.processBlock(convertInput);
            }
        }
        else {
            bigInteger = this.core.processBlock(convertInput);
        }
        return this.core.convertOutput(bigInteger);
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
