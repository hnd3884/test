package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.InvalidKeyException;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.interfaces.DHPrivateKey;
import java.security.Key;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.jce.spec.ElGamalPublicKeySpec;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.DHPrivateKeySpec;
import org.bouncycastle.jce.spec.ElGamalPrivateKeySpec;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;

public class KeyFactorySpi extends BaseKeyFactorySpi
{
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof ElGamalPrivateKeySpec) {
            return new BCElGamalPrivateKey((ElGamalPrivateKeySpec)keySpec);
        }
        if (keySpec instanceof DHPrivateKeySpec) {
            return new BCElGamalPrivateKey((DHPrivateKeySpec)keySpec);
        }
        return super.engineGeneratePrivate(keySpec);
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof ElGamalPublicKeySpec) {
            return new BCElGamalPublicKey((ElGamalPublicKeySpec)keySpec);
        }
        if (keySpec instanceof DHPublicKeySpec) {
            return new BCElGamalPublicKey((DHPublicKeySpec)keySpec);
        }
        return super.engineGeneratePublic(keySpec);
    }
    
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
            return new BCElGamalPublicKey((DHPublicKey)key);
        }
        if (key instanceof DHPrivateKey) {
            return new BCElGamalPrivateKey((DHPrivateKey)key);
        }
        if (key instanceof ElGamalPublicKey) {
            return new BCElGamalPublicKey((ElGamalPublicKey)key);
        }
        if (key instanceof ElGamalPrivateKey) {
            return new BCElGamalPrivateKey((ElGamalPrivateKey)key);
        }
        throw new InvalidKeyException("key type unknown");
    }
    
    public PrivateKey generatePrivate(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (algorithm.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
            return new BCElGamalPrivateKey(privateKeyInfo);
        }
        if (algorithm.equals(X9ObjectIdentifiers.dhpublicnumber)) {
            return new BCElGamalPrivateKey(privateKeyInfo);
        }
        if (algorithm.equals(OIWObjectIdentifiers.elGamalAlgorithm)) {
            return new BCElGamalPrivateKey(privateKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
    
    public PublicKey generatePublic(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (algorithm.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
            return new BCElGamalPublicKey(subjectPublicKeyInfo);
        }
        if (algorithm.equals(X9ObjectIdentifiers.dhpublicnumber)) {
            return new BCElGamalPublicKey(subjectPublicKeyInfo);
        }
        if (algorithm.equals(OIWObjectIdentifiers.elGamalAlgorithm)) {
            return new BCElGamalPublicKey(subjectPublicKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
}
