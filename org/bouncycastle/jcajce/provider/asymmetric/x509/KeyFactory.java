package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.InvalidKeyException;
import java.security.Key;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import java.security.KeyFactorySpi;

public class KeyFactory extends KeyFactorySpi
{
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            try {
                final PrivateKeyInfo instance = PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec)keySpec).getEncoded());
                final PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(instance);
                if (privateKey != null) {
                    return privateKey;
                }
                throw new InvalidKeySpecException("no factory found for OID: " + instance.getPrivateKeyAlgorithm().getAlgorithm());
            }
            catch (final Exception ex) {
                throw new InvalidKeySpecException(ex.toString());
            }
        }
        throw new InvalidKeySpecException("Unknown KeySpec type: " + keySpec.getClass().getName());
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof X509EncodedKeySpec) {
            try {
                final SubjectPublicKeyInfo instance = SubjectPublicKeyInfo.getInstance(((X509EncodedKeySpec)keySpec).getEncoded());
                final PublicKey publicKey = BouncyCastleProvider.getPublicKey(instance);
                if (publicKey != null) {
                    return publicKey;
                }
                throw new InvalidKeySpecException("no factory found for OID: " + instance.getAlgorithm().getAlgorithm());
            }
            catch (final Exception ex) {
                throw new InvalidKeySpecException(ex.toString());
            }
        }
        throw new InvalidKeySpecException("Unknown KeySpec type: " + keySpec.getClass().getName());
    }
    
    @Override
    protected KeySpec engineGetKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        if (clazz.isAssignableFrom(PKCS8EncodedKeySpec.class) && key.getFormat().equals("PKCS#8")) {
            return new PKCS8EncodedKeySpec(key.getEncoded());
        }
        if (clazz.isAssignableFrom(X509EncodedKeySpec.class) && key.getFormat().equals("X.509")) {
            return new X509EncodedKeySpec(key.getEncoded());
        }
        throw new InvalidKeySpecException("not implemented yet " + key + " " + clazz);
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        throw new InvalidKeyException("not implemented yet " + key);
    }
}
