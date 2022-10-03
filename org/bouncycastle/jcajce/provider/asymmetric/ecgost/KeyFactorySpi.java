package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.jce.spec.ECParameterSpec;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECPrivateKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.KeySpec;
import java.security.Key;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;

public class KeyFactorySpi extends BaseKeyFactorySpi
{
    @Override
    protected KeySpec engineGetKeySpec(final Key key, final Class clazz) throws InvalidKeySpecException {
        if (clazz.isAssignableFrom(ECPublicKeySpec.class) && key instanceof ECPublicKey) {
            final ECPublicKey ecPublicKey = (ECPublicKey)key;
            if (ecPublicKey.getParams() != null) {
                return new ECPublicKeySpec(ecPublicKey.getW(), ecPublicKey.getParams());
            }
            final ECParameterSpec ecImplicitlyCa = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPublicKeySpec(ecPublicKey.getW(), EC5Util.convertSpec(EC5Util.convertCurve(ecImplicitlyCa.getCurve(), ecImplicitlyCa.getSeed()), ecImplicitlyCa));
        }
        else if (clazz.isAssignableFrom(ECPrivateKeySpec.class) && key instanceof ECPrivateKey) {
            final ECPrivateKey ecPrivateKey = (ECPrivateKey)key;
            if (ecPrivateKey.getParams() != null) {
                return new ECPrivateKeySpec(ecPrivateKey.getS(), ecPrivateKey.getParams());
            }
            final ECParameterSpec ecImplicitlyCa2 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPrivateKeySpec(ecPrivateKey.getS(), EC5Util.convertSpec(EC5Util.convertCurve(ecImplicitlyCa2.getCurve(), ecImplicitlyCa2.getSeed()), ecImplicitlyCa2));
        }
        else if (clazz.isAssignableFrom(org.bouncycastle.jce.spec.ECPublicKeySpec.class) && key instanceof ECPublicKey) {
            final ECPublicKey ecPublicKey2 = (ECPublicKey)key;
            if (ecPublicKey2.getParams() != null) {
                return new org.bouncycastle.jce.spec.ECPublicKeySpec(EC5Util.convertPoint(ecPublicKey2.getParams(), ecPublicKey2.getW(), false), EC5Util.convertSpec(ecPublicKey2.getParams(), false));
            }
            return new org.bouncycastle.jce.spec.ECPublicKeySpec(EC5Util.convertPoint(ecPublicKey2.getParams(), ecPublicKey2.getW(), false), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa());
        }
        else {
            if (!clazz.isAssignableFrom(org.bouncycastle.jce.spec.ECPrivateKeySpec.class) || !(key instanceof ECPrivateKey)) {
                return super.engineGetKeySpec(key, clazz);
            }
            final ECPrivateKey ecPrivateKey2 = (ECPrivateKey)key;
            if (ecPrivateKey2.getParams() != null) {
                return new org.bouncycastle.jce.spec.ECPrivateKeySpec(ecPrivateKey2.getS(), EC5Util.convertSpec(ecPrivateKey2.getParams(), false));
            }
            return new org.bouncycastle.jce.spec.ECPrivateKeySpec(ecPrivateKey2.getS(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa());
        }
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        throw new InvalidKeyException("key type unknown");
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof org.bouncycastle.jce.spec.ECPrivateKeySpec) {
            return new BCECGOST3410PrivateKey((org.bouncycastle.jce.spec.ECPrivateKeySpec)keySpec);
        }
        if (keySpec instanceof ECPrivateKeySpec) {
            return new BCECGOST3410PrivateKey((ECPrivateKeySpec)keySpec);
        }
        return super.engineGeneratePrivate(keySpec);
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof org.bouncycastle.jce.spec.ECPublicKeySpec) {
            return new BCECGOST3410PublicKey((org.bouncycastle.jce.spec.ECPublicKeySpec)keySpec, BouncyCastleProvider.CONFIGURATION);
        }
        if (keySpec instanceof ECPublicKeySpec) {
            return new BCECGOST3410PublicKey((ECPublicKeySpec)keySpec);
        }
        return super.engineGeneratePublic(keySpec);
    }
    
    public PrivateKey generatePrivate(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (algorithm.equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
            return new BCECGOST3410PrivateKey(privateKeyInfo);
        }
        if (algorithm.equals(CryptoProObjectIdentifiers.gostR3410_2001DH)) {
            return new BCECGOST3410PrivateKey(privateKeyInfo);
        }
        if (algorithm.equals(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH)) {
            return new BCECGOST3410PrivateKey(privateKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
    
    public PublicKey generatePublic(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        final ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (algorithm.equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
            return new BCECGOST3410PublicKey(subjectPublicKeyInfo);
        }
        if (algorithm.equals(CryptoProObjectIdentifiers.gostR3410_2001DH)) {
            return new BCECGOST3410PublicKey(subjectPublicKeyInfo);
        }
        if (algorithm.equals(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH)) {
            return new BCECGOST3410PublicKey(subjectPublicKeyInfo);
        }
        throw new IOException("algorithm identifier " + algorithm + " in key not recognised");
    }
}
