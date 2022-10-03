package sun.security.mscapi;

import java.security.KeyException;
import java.security.ProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;
import java.security.KeyPair;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.math.BigInteger;
import sun.security.rsa.RSAKeyFactory;
import java.security.SecureRandom;
import sun.security.util.SecurityProviderConstants;
import java.security.KeyPairGeneratorSpi;

public abstract class CKeyPairGenerator extends KeyPairGeneratorSpi
{
    protected String keyAlg;
    
    public CKeyPairGenerator(final String keyAlg) {
        this.keyAlg = keyAlg;
    }
    
    public static class RSA extends CKeyPairGenerator
    {
        static final int KEY_SIZE_MIN = 512;
        static final int KEY_SIZE_MAX = 16384;
        private int keySize;
        
        public RSA() {
            super("RSA");
            this.initialize(SecurityProviderConstants.DEF_RSA_KEY_SIZE, null);
        }
        
        @Override
        public void initialize(final int keySize, final SecureRandom secureRandom) {
            try {
                RSAKeyFactory.checkKeyLengths(keySize, null, 512, 16384);
            }
            catch (final InvalidKeyException ex) {
                throw new InvalidParameterException(ex.getMessage());
            }
            this.keySize = keySize;
        }
        
        @Override
        public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            int keySize;
            if (algorithmParameterSpec == null) {
                keySize = SecurityProviderConstants.DEF_RSA_KEY_SIZE;
            }
            else {
                if (!(algorithmParameterSpec instanceof RSAKeyGenParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("Params must be an instance of RSAKeyGenParameterSpec");
                }
                if (((RSAKeyGenParameterSpec)algorithmParameterSpec).getPublicExponent() != null) {
                    throw new InvalidAlgorithmParameterException("Exponent parameter is not supported");
                }
                keySize = ((RSAKeyGenParameterSpec)algorithmParameterSpec).getKeysize();
            }
            try {
                RSAKeyFactory.checkKeyLengths(keySize, null, 512, 16384);
            }
            catch (final InvalidKeyException ex) {
                throw new InvalidAlgorithmParameterException("Invalid Key sizes", ex);
            }
            this.keySize = keySize;
        }
        
        @Override
        public KeyPair generateKeyPair() {
            try {
                final CKeyPair generateCKeyPair = generateCKeyPair(this.keyAlg, this.keySize, "{" + UUID.randomUUID().toString() + "}");
                return new KeyPair(generateCKeyPair.getPublic(), generateCKeyPair.getPrivate());
            }
            catch (final KeyException ex) {
                throw new ProviderException(ex);
            }
        }
        
        private static native CKeyPair generateCKeyPair(final String p0, final int p1, final String p2) throws KeyException;
    }
}
