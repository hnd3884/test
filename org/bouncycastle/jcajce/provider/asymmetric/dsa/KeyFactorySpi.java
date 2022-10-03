package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import java.security.PrivateKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.KeySpec;
import java.security.Key;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;

public class KeyFactorySpi extends BaseKeyFactorySpi
{
    @Override
    protected KeySpec engineGetKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        if (clazz.isAssignableFrom(DSAPublicKeySpec.class) && key instanceof DSAPublicKey) {
            final DSAPublicKey dsaPublicKey = (DSAPublicKey)key;
            return new DSAPublicKeySpec(dsaPublicKey.getY(), dsaPublicKey.getParams().getP(), dsaPublicKey.getParams().getQ(), dsaPublicKey.getParams().getG());
        }
        if (clazz.isAssignableFrom(DSAPrivateKeySpec.class) && key instanceof DSAPrivateKey) {
            final DSAPrivateKey dsaPrivateKey = (DSAPrivateKey)key;
            return new DSAPrivateKeySpec(dsaPrivateKey.getX(), dsaPrivateKey.getParams().getP(), dsaPrivateKey.getParams().getQ(), dsaPrivateKey.getParams().getG());
        }
        return super.engineGetKeySpec(key, clazz);
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        if (key instanceof DSAPublicKey) {
            return new BCDSAPublicKey((DSAPublicKey)key);
        }
        if (key instanceof DSAPrivateKey) {
            return new BCDSAPrivateKey((DSAPrivateKey)key);
        }
        throw new InvalidKeyException("key type unknown");
    }
    
    public PrivateKey generatePrivate(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (DSAUtil.isDsaOid(algorithm)) {
            return new BCDSAPrivateKey(privateKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
    
    public PublicKey generatePublic(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (DSAUtil.isDsaOid(algorithm)) {
            return new BCDSAPublicKey(subjectPublicKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof DSAPrivateKeySpec) {
            return new BCDSAPrivateKey((DSAPrivateKeySpec)keySpec);
        }
        return super.engineGeneratePrivate(keySpec);
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof DSAPublicKeySpec) {
            try {
                return new BCDSAPublicKey((DSAPublicKeySpec)keySpec);
            }
            catch (final Exception ex) {
                throw new InvalidKeySpecException("invalid KeySpec: " + ex.getMessage()) {
                    @Override
                    public Throwable getCause() {
                        return ex;
                    }
                };
            }
        }
        return super.engineGeneratePublic(keySpec);
    }
}
