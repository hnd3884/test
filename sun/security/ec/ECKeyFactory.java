package sun.security.ec;

import java.security.spec.ECPrivateKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.InvalidKeyException;
import java.security.interfaces.ECKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.KeyFactory;
import java.security.KeyFactorySpi;

public final class ECKeyFactory extends KeyFactorySpi
{
    private static KeyFactory instance;
    
    private static KeyFactory getInstance() {
        if (ECKeyFactory.instance == null) {
            try {
                ECKeyFactory.instance = KeyFactory.getInstance("EC", "SunEC");
            }
            catch (final NoSuchProviderException ex) {
                throw new RuntimeException(ex);
            }
            catch (final NoSuchAlgorithmException ex2) {
                throw new RuntimeException(ex2);
            }
        }
        return ECKeyFactory.instance;
    }
    
    public static ECKey toECKey(final Key key) throws InvalidKeyException {
        if (key instanceof ECKey) {
            final ECKey ecKey = (ECKey)key;
            checkKey(ecKey);
            return ecKey;
        }
        return (ECKey)getInstance().translateKey(key);
    }
    
    private static void checkKey(final ECKey ecKey) throws InvalidKeyException {
        if (ecKey instanceof ECPublicKey) {
            if (ecKey instanceof ECPublicKeyImpl) {
                return;
            }
        }
        else {
            if (!(ecKey instanceof ECPrivateKey)) {
                throw new InvalidKeyException("Neither a public nor a private key");
            }
            if (ecKey instanceof ECPrivateKeyImpl) {
                return;
            }
        }
        final String algorithm = ((Key)ecKey).getAlgorithm();
        if (!algorithm.equals("EC")) {
            throw new InvalidKeyException("Not an EC key: " + algorithm);
        }
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("Key must not be null");
        }
        final String algorithm = key.getAlgorithm();
        if (!algorithm.equals("EC")) {
            throw new InvalidKeyException("Not an EC key: " + algorithm);
        }
        if (key instanceof PublicKey) {
            return this.implTranslatePublicKey((PublicKey)key);
        }
        if (key instanceof PrivateKey) {
            return this.implTranslatePrivateKey((PrivateKey)key);
        }
        throw new InvalidKeyException("Neither a public nor a private key");
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            return this.implGeneratePublic(keySpec);
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
            return this.implGeneratePrivate(keySpec);
        }
        catch (final InvalidKeySpecException ex) {
            throw ex;
        }
        catch (final GeneralSecurityException ex2) {
            throw new InvalidKeySpecException(ex2);
        }
    }
    
    private PublicKey implTranslatePublicKey(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof ECPublicKey) {
            if (publicKey instanceof ECPublicKeyImpl) {
                return publicKey;
            }
            final ECPublicKey ecPublicKey = (ECPublicKey)publicKey;
            return new ECPublicKeyImpl(ecPublicKey.getW(), ecPublicKey.getParams());
        }
        else {
            if ("X.509".equals(publicKey.getFormat())) {
                return new ECPublicKeyImpl(publicKey.getEncoded());
            }
            throw new InvalidKeyException("Public keys must be instance of ECPublicKey or have X.509 encoding");
        }
    }
    
    private PrivateKey implTranslatePrivateKey(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof ECPrivateKey) {
            if (privateKey instanceof ECPrivateKeyImpl) {
                return privateKey;
            }
            final ECPrivateKey ecPrivateKey = (ECPrivateKey)privateKey;
            return new ECPrivateKeyImpl(ecPrivateKey.getS(), ecPrivateKey.getParams());
        }
        else {
            if ("PKCS#8".equals(privateKey.getFormat())) {
                return new ECPrivateKeyImpl(privateKey.getEncoded());
            }
            throw new InvalidKeyException("Private keys must be instance of ECPrivateKey or have PKCS#8 encoding");
        }
    }
    
    private PublicKey implGeneratePublic(final KeySpec keySpec) throws GeneralSecurityException {
        if (keySpec instanceof X509EncodedKeySpec) {
            return new ECPublicKeyImpl(((X509EncodedKeySpec)keySpec).getEncoded());
        }
        if (keySpec instanceof ECPublicKeySpec) {
            final ECPublicKeySpec ecPublicKeySpec = (ECPublicKeySpec)keySpec;
            return new ECPublicKeyImpl(ecPublicKeySpec.getW(), ecPublicKeySpec.getParams());
        }
        throw new InvalidKeySpecException("Only ECPublicKeySpec and X509EncodedKeySpec supported for EC public keys");
    }
    
    private PrivateKey implGeneratePrivate(final KeySpec keySpec) throws GeneralSecurityException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            return new ECPrivateKeyImpl(((PKCS8EncodedKeySpec)keySpec).getEncoded());
        }
        if (keySpec instanceof ECPrivateKeySpec) {
            final ECPrivateKeySpec ecPrivateKeySpec = (ECPrivateKeySpec)keySpec;
            return new ECPrivateKeyImpl(ecPrivateKeySpec.getS(), ecPrivateKeySpec.getParams());
        }
        throw new InvalidKeySpecException("Only ECPrivateKeySpec and PKCS8EncodedKeySpec supported for EC private keys");
    }
    
    @Override
    protected <T extends KeySpec> T engineGetKeySpec(Key engineTranslateKey, final Class<T> clazz) throws InvalidKeySpecException {
        try {
            engineTranslateKey = this.engineTranslateKey(engineTranslateKey);
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidKeySpecException(ex);
        }
        if (engineTranslateKey instanceof ECPublicKey) {
            final ECPublicKey ecPublicKey = (ECPublicKey)engineTranslateKey;
            if (ECPublicKeySpec.class.isAssignableFrom(clazz)) {
                return clazz.cast(new ECPublicKeySpec(ecPublicKey.getW(), ecPublicKey.getParams()));
            }
            if (X509EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return clazz.cast(new X509EncodedKeySpec(engineTranslateKey.getEncoded()));
            }
            throw new InvalidKeySpecException("KeySpec must be ECPublicKeySpec or X509EncodedKeySpec for EC public keys");
        }
        else {
            if (!(engineTranslateKey instanceof ECPrivateKey)) {
                throw new InvalidKeySpecException("Neither public nor private key");
            }
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return clazz.cast(new PKCS8EncodedKeySpec(engineTranslateKey.getEncoded()));
            }
            if (ECPrivateKeySpec.class.isAssignableFrom(clazz)) {
                final ECPrivateKey ecPrivateKey = (ECPrivateKey)engineTranslateKey;
                return clazz.cast(new ECPrivateKeySpec(ecPrivateKey.getS(), ecPrivateKey.getParams()));
            }
            throw new InvalidKeySpecException("KeySpec must be ECPrivateKeySpec or PKCS8EncodedKeySpec for EC private keys");
        }
    }
}
