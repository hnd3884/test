package sun.security.provider;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.security.interfaces.DSAParams;
import java.security.Key;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.DSAPrivateKeySpec;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.KeyFactorySpi;

public class DSAKeyFactory extends KeyFactorySpi
{
    static final boolean SERIAL_INTEROP;
    private static final String SERIAL_PROP = "sun.security.key.serial.interop";
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            if (keySpec instanceof DSAPublicKeySpec) {
                final DSAPublicKeySpec dsaPublicKeySpec = (DSAPublicKeySpec)keySpec;
                if (DSAKeyFactory.SERIAL_INTEROP) {
                    return new DSAPublicKey(dsaPublicKeySpec.getY(), dsaPublicKeySpec.getP(), dsaPublicKeySpec.getQ(), dsaPublicKeySpec.getG());
                }
                return new DSAPublicKeyImpl(dsaPublicKeySpec.getY(), dsaPublicKeySpec.getP(), dsaPublicKeySpec.getQ(), dsaPublicKeySpec.getG());
            }
            else {
                if (!(keySpec instanceof X509EncodedKeySpec)) {
                    throw new InvalidKeySpecException("Inappropriate key specification");
                }
                if (DSAKeyFactory.SERIAL_INTEROP) {
                    return new DSAPublicKey(((X509EncodedKeySpec)keySpec).getEncoded());
                }
                return new DSAPublicKeyImpl(((X509EncodedKeySpec)keySpec).getEncoded());
            }
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidKeySpecException("Inappropriate key specification: " + ex.getMessage());
        }
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            if (keySpec instanceof DSAPrivateKeySpec) {
                final DSAPrivateKeySpec dsaPrivateKeySpec = (DSAPrivateKeySpec)keySpec;
                return new DSAPrivateKey(dsaPrivateKeySpec.getX(), dsaPrivateKeySpec.getP(), dsaPrivateKeySpec.getQ(), dsaPrivateKeySpec.getG());
            }
            if (keySpec instanceof PKCS8EncodedKeySpec) {
                return new DSAPrivateKey(((PKCS8EncodedKeySpec)keySpec).getEncoded());
            }
            throw new InvalidKeySpecException("Inappropriate key specification");
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidKeySpecException("Inappropriate key specification: " + ex.getMessage());
        }
    }
    
    @Override
    protected <T extends KeySpec> T engineGetKeySpec(final Key key, final Class<T> clazz) throws InvalidKeySpecException {
        try {
            if (key instanceof java.security.interfaces.DSAPublicKey) {
                final Class<?> forName = Class.forName("java.security.spec.DSAPublicKeySpec");
                final Class<?> forName2 = Class.forName("java.security.spec.X509EncodedKeySpec");
                if (forName.isAssignableFrom(clazz)) {
                    final java.security.interfaces.DSAPublicKey dsaPublicKey = (java.security.interfaces.DSAPublicKey)key;
                    final DSAParams params = dsaPublicKey.getParams();
                    return clazz.cast(new DSAPublicKeySpec(dsaPublicKey.getY(), params.getP(), params.getQ(), params.getG()));
                }
                if (forName2.isAssignableFrom(clazz)) {
                    return clazz.cast(new X509EncodedKeySpec(key.getEncoded()));
                }
                throw new InvalidKeySpecException("Inappropriate key specification");
            }
            else {
                if (!(key instanceof java.security.interfaces.DSAPrivateKey)) {
                    throw new InvalidKeySpecException("Inappropriate key type");
                }
                final Class<?> forName3 = Class.forName("java.security.spec.DSAPrivateKeySpec");
                final Class<?> forName4 = Class.forName("java.security.spec.PKCS8EncodedKeySpec");
                if (forName3.isAssignableFrom(clazz)) {
                    final java.security.interfaces.DSAPrivateKey dsaPrivateKey = (java.security.interfaces.DSAPrivateKey)key;
                    final DSAParams params2 = dsaPrivateKey.getParams();
                    return clazz.cast(new DSAPrivateKeySpec(dsaPrivateKey.getX(), params2.getP(), params2.getQ(), params2.getG()));
                }
                if (forName4.isAssignableFrom(clazz)) {
                    return clazz.cast(new PKCS8EncodedKeySpec(key.getEncoded()));
                }
                throw new InvalidKeySpecException("Inappropriate key specification");
            }
        }
        catch (final ClassNotFoundException ex) {
            throw new InvalidKeySpecException("Unsupported key specification: " + ex.getMessage());
        }
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        try {
            if (key instanceof java.security.interfaces.DSAPublicKey) {
                if (key instanceof DSAPublicKey) {
                    return key;
                }
                return this.engineGeneratePublic(this.engineGetKeySpec(key, DSAPublicKeySpec.class));
            }
            else {
                if (!(key instanceof java.security.interfaces.DSAPrivateKey)) {
                    throw new InvalidKeyException("Wrong algorithm type");
                }
                if (key instanceof DSAPrivateKey) {
                    return key;
                }
                return this.engineGeneratePrivate(this.engineGetKeySpec(key, DSAPrivateKeySpec.class));
            }
        }
        catch (final InvalidKeySpecException ex) {
            throw new InvalidKeyException("Cannot translate key: " + ex.getMessage());
        }
    }
    
    static {
        SERIAL_INTEROP = "true".equalsIgnoreCase(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.security.key.serial.interop", null)));
    }
}
