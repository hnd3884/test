package org.bouncycastle.jcajce.provider.asymmetric.dh;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.util.ExtendedInvalidKeySpecException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHPrivateKeySpec;
import java.security.spec.KeySpec;
import java.security.Key;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;

public class KeyFactorySpi extends BaseKeyFactorySpi
{
    @Override
    protected KeySpec engineGetKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        if (clazz.isAssignableFrom(DHPrivateKeySpec.class) && key instanceof DHPrivateKey) {
            final DHPrivateKey dhPrivateKey = (DHPrivateKey)key;
            return new DHPrivateKeySpec(dhPrivateKey.getX(), dhPrivateKey.getParams().getP(), dhPrivateKey.getParams().getG());
        }
        if (clazz.isAssignableFrom(DHPublicKeySpec.class) && key instanceof DHPublicKey) {
            final DHPublicKey dhPublicKey = (DHPublicKey)key;
            return new DHPublicKeySpec(dhPublicKey.getY(), dhPublicKey.getParams().getP(), dhPublicKey.getParams().getG());
        }
        return super.engineGetKeySpec(key, clazz);
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        if (key instanceof DHPublicKey) {
            return new BCDHPublicKey((DHPublicKey)key);
        }
        if (key instanceof DHPrivateKey) {
            return new BCDHPrivateKey((DHPrivateKey)key);
        }
        throw new InvalidKeyException("key type unknown");
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof DHPrivateKeySpec) {
            return new BCDHPrivateKey((DHPrivateKeySpec)keySpec);
        }
        return super.engineGeneratePrivate(keySpec);
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof DHPublicKeySpec) {
            try {
                return new BCDHPublicKey((DHPublicKeySpec)keySpec);
            }
            catch (final IllegalArgumentException ex) {
                throw new ExtendedInvalidKeySpecException(ex.getMessage(), ex);
            }
        }
        return super.engineGeneratePublic(keySpec);
    }
    
    public PrivateKey generatePrivate(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (algorithm.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
            return new BCDHPrivateKey(privateKeyInfo);
        }
        if (algorithm.equals(X9ObjectIdentifiers.dhpublicnumber)) {
            return new BCDHPrivateKey(privateKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
    
    public PublicKey generatePublic(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (algorithm.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
            return new BCDHPublicKey(subjectPublicKeyInfo);
        }
        if (algorithm.equals(X9ObjectIdentifiers.dhpublicnumber)) {
            return new BCDHPublicKey(subjectPublicKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
}
