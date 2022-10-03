package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import java.security.PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.KeySpec;
import java.security.Key;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;

public class KeyFactorySpi extends BaseKeyFactorySpi
{
    @Override
    protected KeySpec engineGetKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        if (clazz.isAssignableFrom(RSAPublicKeySpec.class) && key instanceof RSAPublicKey) {
            final RSAPublicKey rsaPublicKey = (RSAPublicKey)key;
            return new RSAPublicKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
        }
        if (clazz.isAssignableFrom(RSAPrivateKeySpec.class) && key instanceof RSAPrivateKey) {
            final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)key;
            return new RSAPrivateKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
        }
        if (clazz.isAssignableFrom(RSAPrivateCrtKeySpec.class) && key instanceof RSAPrivateCrtKey) {
            final RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey)key;
            return new RSAPrivateCrtKeySpec(rsaPrivateCrtKey.getModulus(), rsaPrivateCrtKey.getPublicExponent(), rsaPrivateCrtKey.getPrivateExponent(), rsaPrivateCrtKey.getPrimeP(), rsaPrivateCrtKey.getPrimeQ(), rsaPrivateCrtKey.getPrimeExponentP(), rsaPrivateCrtKey.getPrimeExponentQ(), rsaPrivateCrtKey.getCrtCoefficient());
        }
        return super.engineGetKeySpec(key, clazz);
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        if (key instanceof RSAPublicKey) {
            return new BCRSAPublicKey((RSAPublicKey)key);
        }
        if (key instanceof RSAPrivateCrtKey) {
            return new BCRSAPrivateCrtKey((RSAPrivateCrtKey)key);
        }
        if (key instanceof RSAPrivateKey) {
            return new BCRSAPrivateKey((RSAPrivateKey)key);
        }
        throw new InvalidKeyException("key type unknown");
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            try {
                return this.generatePrivate(PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec)keySpec).getEncoded()));
            }
            catch (final Exception ex) {
                try {
                    return new BCRSAPrivateCrtKey(org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(((PKCS8EncodedKeySpec)keySpec).getEncoded()));
                }
                catch (final Exception ex2) {
                    throw new ExtendedInvalidKeySpecException("unable to process key spec: " + ex.toString(), ex);
                }
            }
        }
        if (keySpec instanceof RSAPrivateCrtKeySpec) {
            return new BCRSAPrivateCrtKey((RSAPrivateCrtKeySpec)keySpec);
        }
        if (keySpec instanceof RSAPrivateKeySpec) {
            return new BCRSAPrivateKey((RSAPrivateKeySpec)keySpec);
        }
        throw new InvalidKeySpecException("Unknown KeySpec type: " + keySpec.getClass().getName());
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof RSAPublicKeySpec) {
            return new BCRSAPublicKey((RSAPublicKeySpec)keySpec);
        }
        return super.engineGeneratePublic(keySpec);
    }
    
    public PrivateKey generatePrivate(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (!RSAUtil.isRsaOid(algorithm)) {
            throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
        }
        final org.bouncycastle.asn1.pkcs.RSAPrivateKey instance = org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
        if (instance.getCoefficient().intValue() == 0) {
            return new BCRSAPrivateKey(instance);
        }
        return new BCRSAPrivateCrtKey(privateKeyInfo);
    }
    
    public PublicKey generatePublic(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (RSAUtil.isRsaOid(algorithm)) {
            return new BCRSAPublicKey(subjectPublicKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
}
