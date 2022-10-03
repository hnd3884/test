package cryptix.jce.provider.key;

import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import javax.crypto.KeyGeneratorSpi;

abstract class RawKeyGenerator extends KeyGeneratorSpi
{
    private final String algorithm;
    private final int defaultKeySize;
    private SecureRandom random;
    private int keySize;
    
    protected void engineInit(final SecureRandom random) {
        this.random = random;
        this.keySize = this.defaultKeySize;
    }
    
    protected void engineInit(final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("No AlgorithmParameterSpec supported.");
    }
    
    protected void engineInit(final int keysize, final SecureRandom random) {
        if (!this.isValidSize(keysize)) {
            throw new InvalidParameterException("Key size not supported [" + keysize + "]");
        }
        this.random = random;
        this.keySize = keysize;
    }
    
    protected SecretKey engineGenerateKey() {
        if (this.random == null) {
            throw new IllegalStateException("KeyGenerator not initialized.");
        }
        byte[] keyBytes = new byte[(this.strengthToBits(this.keySize) + 7) / 8];
        do {
            this.random.nextBytes(keyBytes);
            keyBytes = this.fixUp(keyBytes);
        } while (this.isWeak(keyBytes));
        return new RawSecretKey(this.algorithm, keyBytes);
    }
    
    protected int strengthToBits(final int strength) {
        return strength;
    }
    
    protected byte[] fixUp(final byte[] key) {
        return key;
    }
    
    protected abstract boolean isWeak(final byte[] p0);
    
    protected abstract boolean isValidSize(final int p0);
    
    protected RawKeyGenerator(final String algorithm, final int defaultKeySize) {
        this.random = null;
        this.keySize = 0;
        this.algorithm = algorithm;
        this.defaultKeySize = defaultKeySize;
    }
}
