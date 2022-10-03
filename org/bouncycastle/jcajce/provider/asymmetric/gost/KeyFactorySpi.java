package org.bouncycastle.jcajce.provider.asymmetric.gost;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.spec.GOST3410PrivateKeySpec;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.jce.spec.GOST3410PublicKeySpec;
import java.security.spec.KeySpec;
import java.security.Key;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;

public class KeyFactorySpi extends BaseKeyFactorySpi
{
    @Override
    protected KeySpec engineGetKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        if (clazz.isAssignableFrom(GOST3410PublicKeySpec.class) && key instanceof GOST3410PublicKey) {
            final GOST3410PublicKey gost3410PublicKey = (GOST3410PublicKey)key;
            final GOST3410PublicKeyParameterSetSpec publicKeyParameters = gost3410PublicKey.getParameters().getPublicKeyParameters();
            return new GOST3410PublicKeySpec(gost3410PublicKey.getY(), publicKeyParameters.getP(), publicKeyParameters.getQ(), publicKeyParameters.getA());
        }
        if (clazz.isAssignableFrom(GOST3410PrivateKeySpec.class) && key instanceof GOST3410PrivateKey) {
            final GOST3410PrivateKey gost3410PrivateKey = (GOST3410PrivateKey)key;
            final GOST3410PublicKeyParameterSetSpec publicKeyParameters2 = gost3410PrivateKey.getParameters().getPublicKeyParameters();
            return new GOST3410PrivateKeySpec(gost3410PrivateKey.getX(), publicKeyParameters2.getP(), publicKeyParameters2.getQ(), publicKeyParameters2.getA());
        }
        return super.engineGetKeySpec(key, clazz);
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        if (key instanceof GOST3410PublicKey) {
            return new BCGOST3410PublicKey((GOST3410PublicKey)key);
        }
        if (key instanceof GOST3410PrivateKey) {
            return new BCGOST3410PrivateKey((GOST3410PrivateKey)key);
        }
        throw new InvalidKeyException("key type unknown");
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof GOST3410PrivateKeySpec) {
            return new BCGOST3410PrivateKey((GOST3410PrivateKeySpec)keySpec);
        }
        return super.engineGeneratePrivate(keySpec);
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof GOST3410PublicKeySpec) {
            return new BCGOST3410PublicKey((GOST3410PublicKeySpec)keySpec);
        }
        return super.engineGeneratePublic(keySpec);
    }
    
    public PrivateKey generatePrivate(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (algorithm.equals(CryptoProObjectIdentifiers.gostR3410_94)) {
            return new BCGOST3410PrivateKey(privateKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
    
    public PublicKey generatePublic(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (algorithm.equals(CryptoProObjectIdentifiers.gostR3410_94)) {
            return new BCGOST3410PublicKey(subjectPublicKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
}
