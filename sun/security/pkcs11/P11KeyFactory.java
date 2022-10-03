package sun.security.pkcs11;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.GeneralSecurityException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;

abstract class P11KeyFactory extends KeyFactorySpi
{
    final Token token;
    final String algorithm;
    
    P11KeyFactory(final Token token, final String algorithm) {
        this.token = token;
        this.algorithm = algorithm;
    }
    
    static P11Key convertKey(final Token token, final Key key, final String s) throws InvalidKeyException {
        return (P11Key)token.getKeyFactory(s).engineTranslateKey(key);
    }
    
    @Override
    protected final <T extends KeySpec> T engineGetKeySpec(final Key key, final Class<T> clazz) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (key == null || clazz == null) {
            throw new InvalidKeySpecException("key and keySpec must not be null");
        }
        P11Key p11Key = null;
        Label_0065: {
            if (!PKCS8EncodedKeySpec.class.isAssignableFrom(clazz)) {
                if (!X509EncodedKeySpec.class.isAssignableFrom(clazz)) {
                    break Label_0065;
                }
            }
            try {
                return this.implGetSoftwareFactory().getKeySpec(key, clazz);
            }
            catch (final GeneralSecurityException ex) {
                throw new InvalidKeySpecException("Could not encode key", ex);
            }
            try {
                p11Key = (P11Key)this.engineTranslateKey(key);
            }
            catch (final InvalidKeyException ex2) {
                throw new InvalidKeySpecException("Could not convert key", ex2);
            }
        }
        final Session[] array = { null };
        try {
            if (p11Key.isPublic()) {
                return this.implGetPublicKeySpec(p11Key, clazz, array);
            }
            return this.implGetPrivateKeySpec(p11Key, clazz, array);
        }
        catch (final PKCS11Exception ex3) {
            throw new InvalidKeySpecException("Could not generate KeySpec", ex3);
        }
        finally {
            array[0] = this.token.releaseSession(array[0]);
        }
    }
    
    @Override
    protected final Key engineTranslateKey(final Key key) throws InvalidKeyException {
        this.token.ensureValid();
        if (key == null) {
            throw new InvalidKeyException("Key must not be null");
        }
        if (!key.getAlgorithm().equals(this.algorithm)) {
            throw new InvalidKeyException("Key algorithm must be " + this.algorithm);
        }
        if (key instanceof P11Key && ((P11Key)key).token == this.token) {
            return key;
        }
        final P11Key value = this.token.privateCache.get(key);
        if (value != null) {
            return value;
        }
        if (key instanceof PublicKey) {
            final PublicKey implTranslatePublicKey = this.implTranslatePublicKey((PublicKey)key);
            this.token.privateCache.put(key, (P11Key)implTranslatePublicKey);
            return implTranslatePublicKey;
        }
        if (key instanceof PrivateKey) {
            final PrivateKey implTranslatePrivateKey = this.implTranslatePrivateKey((PrivateKey)key);
            this.token.privateCache.put(key, (P11Key)implTranslatePrivateKey);
            return implTranslatePrivateKey;
        }
        throw new InvalidKeyException("Key must be instance of PublicKey or PrivateKey");
    }
    
    abstract <T extends KeySpec> T implGetPublicKeySpec(final P11Key p0, final Class<T> p1, final Session[] p2) throws PKCS11Exception, InvalidKeySpecException;
    
    abstract <T extends KeySpec> T implGetPrivateKeySpec(final P11Key p0, final Class<T> p1, final Session[] p2) throws PKCS11Exception, InvalidKeySpecException;
    
    abstract PublicKey implTranslatePublicKey(final PublicKey p0) throws InvalidKeyException;
    
    abstract PrivateKey implTranslatePrivateKey(final PrivateKey p0) throws InvalidKeyException;
    
    abstract KeyFactory implGetSoftwareFactory() throws GeneralSecurityException;
}
