package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.Key;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import java.security.KeyFactorySpi;

public abstract class BaseKeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter
{
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            try {
                return this.generatePrivate(PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec)keySpec).getEncoded()));
            }
            catch (final Exception ex) {
                throw new InvalidKeySpecException("encoded key spec not recognized: " + ex.getMessage());
            }
        }
        throw new InvalidKeySpecException("key spec not recognized");
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof X509EncodedKeySpec) {
            try {
                return this.generatePublic(SubjectPublicKeyInfo.getInstance(((X509EncodedKeySpec)keySpec).getEncoded()));
            }
            catch (final Exception ex) {
                throw new InvalidKeySpecException("encoded key spec not recognized: " + ex.getMessage());
            }
        }
        throw new InvalidKeySpecException("key spec not recognized");
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
}
