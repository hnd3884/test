package sun.security.rsa;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.math.BigInteger;
import java.security.ProviderException;
import java.security.interfaces.RSAKey;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;

public class RSAKeyFactory extends KeyFactorySpi
{
    private static final Class<?> RSA_PUB_KEYSPEC_CLS;
    private static final Class<?> RSA_PRIV_KEYSPEC_CLS;
    private static final Class<?> RSA_PRIVCRT_KEYSPEC_CLS;
    private static final Class<?> X509_KEYSPEC_CLS;
    private static final Class<?> PKCS8_KEYSPEC_CLS;
    public static final int MIN_MODLEN = 512;
    public static final int MAX_MODLEN = 16384;
    private final RSAUtil.KeyType type;
    public static final int MAX_MODLEN_RESTRICT_EXP = 3072;
    public static final int MAX_RESTRICTED_EXPLEN = 64;
    private static final boolean restrictExpLen;
    
    static RSAKeyFactory getInstance(final RSAUtil.KeyType keyType) {
        return new RSAKeyFactory(keyType);
    }
    
    private static void checkKeyAlgo(final Key key, final String s) throws InvalidKeyException {
        final String algorithm = key.getAlgorithm();
        if (algorithm == null || !algorithm.equalsIgnoreCase(s)) {
            throw new InvalidKeyException("Expected a " + s + " key, but got " + algorithm);
        }
    }
    
    public static RSAKey toRSAKey(final Key key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("Key must not be null");
        }
        if (key instanceof RSAPrivateKeyImpl || key instanceof RSAPrivateCrtKeyImpl || key instanceof RSAPublicKeyImpl) {
            return (RSAKey)key;
        }
        try {
            return (RSAKey)getInstance(RSAUtil.KeyType.lookup(key.getAlgorithm())).engineTranslateKey(key);
        }
        catch (final ProviderException ex) {
            throw new InvalidKeyException(ex);
        }
    }
    
    static void checkRSAProviderKeyLengths(final int n, final BigInteger bigInteger) throws InvalidKeyException {
        checkKeyLengths(n + 7 & 0xFFFFFFF8, bigInteger, 512, Integer.MAX_VALUE);
    }
    
    public static void checkKeyLengths(final int n, final BigInteger bigInteger, final int n2, final int n3) throws InvalidKeyException {
        if (n2 > 0 && n < n2) {
            throw new InvalidKeyException("RSA keys must be at least " + n2 + " bits long");
        }
        final int min = Math.min(n3, 16384);
        if (n > min) {
            throw new InvalidKeyException("RSA keys must be no longer than " + min + " bits");
        }
        if (RSAKeyFactory.restrictExpLen && bigInteger != null && n > 3072 && bigInteger.bitLength() > 64) {
            throw new InvalidKeyException("RSA exponents can be no longer than 64 bits  if modulus is greater than 3072 bits");
        }
    }
    
    private RSAKeyFactory() {
        this.type = RSAUtil.KeyType.RSA;
    }
    
    public RSAKeyFactory(final RSAUtil.KeyType type) {
        this.type = type;
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("Key must not be null");
        }
        checkKeyAlgo(key, this.type.keyAlgo());
        if (key instanceof RSAPrivateKeyImpl || key instanceof RSAPrivateCrtKeyImpl || key instanceof RSAPublicKeyImpl) {
            return key;
        }
        if (key instanceof PublicKey) {
            return this.translatePublicKey((PublicKey)key);
        }
        if (key instanceof PrivateKey) {
            return this.translatePrivateKey((PrivateKey)key);
        }
        throw new InvalidKeyException("Neither a public nor a private key");
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            return this.generatePublic(keySpec);
        }
        catch (final InvalidKeySpecException ex) {
            throw ex;
        }
        catch (final GeneralSecurityException ex2) {
            throw new InvalidKeySpecException(ex2);
        }
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            return this.generatePrivate(keySpec);
        }
        catch (final InvalidKeySpecException ex) {
            throw ex;
        }
        catch (final GeneralSecurityException ex2) {
            throw new InvalidKeySpecException(ex2);
        }
    }
    
    private PublicKey translatePublicKey(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof RSAPublicKey) {
            final RSAPublicKey rsaPublicKey = (RSAPublicKey)publicKey;
            try {
                return new RSAPublicKeyImpl(RSAUtil.createAlgorithmId(this.type, rsaPublicKey.getParams()), rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
            }
            catch (final ProviderException ex) {
                throw new InvalidKeyException("Invalid key", ex);
            }
        }
        if ("X.509".equals(publicKey.getFormat())) {
            final RSAPublicKeyImpl rsaPublicKeyImpl = new RSAPublicKeyImpl(publicKey.getEncoded());
            checkKeyAlgo(rsaPublicKeyImpl, this.type.keyAlgo());
            return rsaPublicKeyImpl;
        }
        throw new InvalidKeyException("Public keys must be instance of RSAPublicKey or have X.509 encoding");
    }
    
    private PrivateKey translatePrivateKey(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof RSAPrivateCrtKey) {
            final RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey)privateKey;
            try {
                return new RSAPrivateCrtKeyImpl(RSAUtil.createAlgorithmId(this.type, rsaPrivateCrtKey.getParams()), rsaPrivateCrtKey.getModulus(), rsaPrivateCrtKey.getPublicExponent(), rsaPrivateCrtKey.getPrivateExponent(), rsaPrivateCrtKey.getPrimeP(), rsaPrivateCrtKey.getPrimeQ(), rsaPrivateCrtKey.getPrimeExponentP(), rsaPrivateCrtKey.getPrimeExponentQ(), rsaPrivateCrtKey.getCrtCoefficient());
            }
            catch (final ProviderException ex) {
                throw new InvalidKeyException("Invalid key", ex);
            }
        }
        if (privateKey instanceof RSAPrivateKey) {
            final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)privateKey;
            try {
                return new RSAPrivateKeyImpl(RSAUtil.createAlgorithmId(this.type, rsaPrivateKey.getParams()), rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
            }
            catch (final ProviderException ex2) {
                throw new InvalidKeyException("Invalid key", ex2);
            }
        }
        if ("PKCS#8".equals(privateKey.getFormat())) {
            final RSAPrivateKey key = RSAPrivateCrtKeyImpl.newKey(privateKey.getEncoded());
            checkKeyAlgo(key, this.type.keyAlgo());
            return key;
        }
        throw new InvalidKeyException("Private keys must be instance of RSAPrivate(Crt)Key or have PKCS#8 encoding");
    }
    
    private PublicKey generatePublic(final KeySpec keySpec) throws GeneralSecurityException {
        if (keySpec instanceof X509EncodedKeySpec) {
            final RSAPublicKeyImpl rsaPublicKeyImpl = new RSAPublicKeyImpl(((X509EncodedKeySpec)keySpec).getEncoded());
            checkKeyAlgo(rsaPublicKeyImpl, this.type.keyAlgo());
            return rsaPublicKeyImpl;
        }
        if (keySpec instanceof RSAPublicKeySpec) {
            final RSAPublicKeySpec rsaPublicKeySpec = (RSAPublicKeySpec)keySpec;
            try {
                return new RSAPublicKeyImpl(RSAUtil.createAlgorithmId(this.type, rsaPublicKeySpec.getParams()), rsaPublicKeySpec.getModulus(), rsaPublicKeySpec.getPublicExponent());
            }
            catch (final ProviderException ex) {
                throw new InvalidKeySpecException(ex);
            }
        }
        throw new InvalidKeySpecException("Only RSAPublicKeySpec and X509EncodedKeySpec supported for RSA public keys");
    }
    
    private PrivateKey generatePrivate(final KeySpec keySpec) throws GeneralSecurityException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            final RSAPrivateKey key = RSAPrivateCrtKeyImpl.newKey(((PKCS8EncodedKeySpec)keySpec).getEncoded());
            checkKeyAlgo(key, this.type.keyAlgo());
            return key;
        }
        if (keySpec instanceof RSAPrivateCrtKeySpec) {
            final RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = (RSAPrivateCrtKeySpec)keySpec;
            try {
                return new RSAPrivateCrtKeyImpl(RSAUtil.createAlgorithmId(this.type, rsaPrivateCrtKeySpec.getParams()), rsaPrivateCrtKeySpec.getModulus(), rsaPrivateCrtKeySpec.getPublicExponent(), rsaPrivateCrtKeySpec.getPrivateExponent(), rsaPrivateCrtKeySpec.getPrimeP(), rsaPrivateCrtKeySpec.getPrimeQ(), rsaPrivateCrtKeySpec.getPrimeExponentP(), rsaPrivateCrtKeySpec.getPrimeExponentQ(), rsaPrivateCrtKeySpec.getCrtCoefficient());
            }
            catch (final ProviderException ex) {
                throw new InvalidKeySpecException(ex);
            }
        }
        if (keySpec instanceof RSAPrivateKeySpec) {
            final RSAPrivateKeySpec rsaPrivateKeySpec = (RSAPrivateKeySpec)keySpec;
            try {
                return new RSAPrivateKeyImpl(RSAUtil.createAlgorithmId(this.type, rsaPrivateKeySpec.getParams()), rsaPrivateKeySpec.getModulus(), rsaPrivateKeySpec.getPrivateExponent());
            }
            catch (final ProviderException ex2) {
                throw new InvalidKeySpecException(ex2);
            }
        }
        throw new InvalidKeySpecException("Only RSAPrivate(Crt)KeySpec and PKCS8EncodedKeySpec supported for RSA private keys");
    }
    
    @Override
    protected <T extends KeySpec> T engineGetKeySpec(Key engineTranslateKey, final Class<T> clazz) throws InvalidKeySpecException {
        try {
            engineTranslateKey = this.engineTranslateKey(engineTranslateKey);
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidKeySpecException(ex);
        }
        if (engineTranslateKey instanceof RSAPublicKey) {
            final RSAPublicKey rsaPublicKey = (RSAPublicKey)engineTranslateKey;
            if (RSAKeyFactory.RSA_PUB_KEYSPEC_CLS.isAssignableFrom(clazz)) {
                return clazz.cast(new RSAPublicKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent(), rsaPublicKey.getParams()));
            }
            if (RSAKeyFactory.X509_KEYSPEC_CLS.isAssignableFrom(clazz)) {
                return clazz.cast(new X509EncodedKeySpec(engineTranslateKey.getEncoded()));
            }
            throw new InvalidKeySpecException("KeySpec must be RSAPublicKeySpec or X509EncodedKeySpec for RSA public keys");
        }
        else {
            if (!(engineTranslateKey instanceof RSAPrivateKey)) {
                throw new InvalidKeySpecException("Neither public nor private key");
            }
            if (RSAKeyFactory.PKCS8_KEYSPEC_CLS.isAssignableFrom(clazz)) {
                return clazz.cast(new PKCS8EncodedKeySpec(engineTranslateKey.getEncoded()));
            }
            if (RSAKeyFactory.RSA_PRIVCRT_KEYSPEC_CLS.isAssignableFrom(clazz)) {
                if (engineTranslateKey instanceof RSAPrivateCrtKey) {
                    final RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey)engineTranslateKey;
                    return clazz.cast(new RSAPrivateCrtKeySpec(rsaPrivateCrtKey.getModulus(), rsaPrivateCrtKey.getPublicExponent(), rsaPrivateCrtKey.getPrivateExponent(), rsaPrivateCrtKey.getPrimeP(), rsaPrivateCrtKey.getPrimeQ(), rsaPrivateCrtKey.getPrimeExponentP(), rsaPrivateCrtKey.getPrimeExponentQ(), rsaPrivateCrtKey.getCrtCoefficient(), rsaPrivateCrtKey.getParams()));
                }
                throw new InvalidKeySpecException("RSAPrivateCrtKeySpec can only be used with CRT keys");
            }
            else {
                if (RSAKeyFactory.RSA_PRIV_KEYSPEC_CLS.isAssignableFrom(clazz)) {
                    final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)engineTranslateKey;
                    return clazz.cast(new RSAPrivateKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent(), rsaPrivateKey.getParams()));
                }
                throw new InvalidKeySpecException("KeySpec must be RSAPrivate(Crt)KeySpec or PKCS8EncodedKeySpec for RSA private keys");
            }
        }
    }
    
    static {
        RSA_PUB_KEYSPEC_CLS = RSAPublicKeySpec.class;
        RSA_PRIV_KEYSPEC_CLS = RSAPrivateKeySpec.class;
        RSA_PRIVCRT_KEYSPEC_CLS = RSAPrivateCrtKeySpec.class;
        X509_KEYSPEC_CLS = X509EncodedKeySpec.class;
        PKCS8_KEYSPEC_CLS = PKCS8EncodedKeySpec.class;
        restrictExpLen = "true".equalsIgnoreCase(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.security.rsa.restrictRSAExponent", "true")));
    }
    
    public static final class Legacy extends RSAKeyFactory
    {
        public Legacy() {
            super(RSAUtil.KeyType.RSA);
        }
    }
    
    public static final class PSS extends RSAKeyFactory
    {
        public PSS() {
            super(RSAUtil.KeyType.PSS);
        }
    }
}
